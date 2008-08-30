/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2008 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package qbts.preprocessing.discretization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Vector;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.preprocessing.discretization.FrequencyDiscretization;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.tools.Tupel;

/**
 * This operator discretizes all numeric attributes in the dataset into nominal attributes. This discretization is performed by equal frequency binning, i.e. the thresholds of all bins is selected in a way that all bins contain the same number of
 * numerical values. The number of bins is specified by a parameter, or, alternatively, is calculated as the square root of the number of examples with non-missing values (calculated for every single attribute). Skips all special attributes including
 * the label.
 * 
 * @author Sebastian Land, Ingo Mierswa
 * @version $Id: FrequencyDiscretization.java,v 1.5 2008/05/09 19:23:25 ingomierswa Exp $
 */
public class FrequencyDiscretizationExtended extends FrequencyDiscretization {

	
	// FJ Modification	
	/** Indicates if long range names should be used. */
	public static final String PARAMETER_ALL_ATTRIBUTES = "discretize_all_together";
	public static final String PARAMETER_INCLUDE_LIMITS = "discretize_all_together";
	// FJ End
	
	public FrequencyDiscretizationExtended(OperatorDescription description) {
		super(description);
	}

	public Model createPreprocessingModel(ExampleSet exampleSet) throws OperatorException {
		
		if (getParameterAsBoolean(PARAMETER_ALL_ATTRIBUTES)){
			HashMap<Attribute, double[]> ranges = new HashMap<Attribute, double[]>();
			// Get and check parametervalues
			boolean useSqrt = getParameterAsBoolean(PARAMETER_USE_SQRT_OF_EXAMPLES);
			int numberOfBins = 0;
			if (!useSqrt) {
				// if not automatic sizing of bins, use parametervalue
				numberOfBins = getParameterAsInt(PARAMETER_NUMBER_OF_BINS);
				if (numberOfBins >= (exampleSet.size() - 1)) {
					throw new UserError(this, 116, PARAMETER_NUMBER_OF_BINS, "number of bins must be smaller than number of examples (here: " + exampleSet.size() + ")");
				}
			} else {
				exampleSet.recalculateAllAttributeStatistics();
			}

			// FJ get number of bins
			if (useSqrt) {
				numberOfBins=0;
				for (Attribute currentAttribute : exampleSet.getAttributes()) {
						numberOfBins = numberOfBins + exampleSet.size() - (int) exampleSet.getStatistics(currentAttribute, Statistics.UNKNOWN);
				}
				numberOfBins = (int) Math.ceil(Math.sqrt(numberOfBins));
			}
			
			List<Double> valores = new ArrayList<Double>();
			for (Example example : exampleSet) {
				for (Attribute currentAttribute : exampleSet.getAttributes()) {
					double value = example.getValue(currentAttribute);
					if (!Double.isNaN(value)) {
						valores.add(value);
					}
				}
			}
			
			Collections.sort(valores);
			
			// finding ranges
			double examplesPerBin = valores.size() / (double) numberOfBins;
			double[] attributeRanges = new double[numberOfBins];
			double currentBinSpace = examplesPerBin;
			double lastValue = Double.NaN;
			int currentBin = 0;

			for (double value : valores) {
				// ** double value = example.getValue(currentAttribute);
				if (!Double.isNaN(value)) {
					// change bin if full and not last
					if (currentBinSpace < 1 && currentBin < numberOfBins && value != lastValue) {
						if (!Double.isNaN(lastValue)) {
							attributeRanges[currentBin] = (lastValue + value) / 2;
							currentBin++;
							currentBinSpace += examplesPerBin;
						}
					}
					currentBinSpace--;
					lastValue = value;
				}
			}
			

			
			if (getParameterAsBoolean(PARAMETER_INCLUDE_LIMITS)){
				
				// Ampliar attributeRanges por el principio
				double[] nattRanges=new double[attributeRanges.length+1];
				// y grabar el valor mínimo
				attributeRanges[0] = valores.get(0);
				// y el valor máximo
				attributeRanges[numberOfBins - 1] = valores.get(valores.size()-1);
				for(int i=0;i<attributeRanges.length;i++)
					nattRanges[i+1]=attributeRanges[i];
				attributeRanges=nattRanges;
			}
			else
				attributeRanges[numberOfBins - 1] = Double.POSITIVE_INFINITY;

			// Se asignan los cortes a los atributos 
			for (Attribute currentAttribute : exampleSet.getAttributes()) {
				
				
				ranges.put(currentAttribute, attributeRanges);
			}

			
			
			DiscretizationModelSeries model = new DiscretizationModelSeries(exampleSet);
			model.setRanges(ranges, "range", getParameterAsBoolean(PARAMETER_USE_LONG_RANGE_NAMES));
			if (getParameterAsBoolean(PARAMETER_INCLUDE_LIMITS))
				model.setLimitsIncluded(true);
			return model;
		}
		else{
			DiscretizationModelSeries model=(DiscretizationModelSeries) super.createPreprocessingModel(exampleSet);
			// Pero cuando no hago el modelo tengo que modificar los rangos
			
			HashMap<Attribute, double[]> ranges = new HashMap<Attribute, double[]>();
			Iterator it = model.rangesMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String attName = (String) entry.getKey();
				SortedSet<Tupel<Double, String>> rmap = (SortedSet<Tupel<Double, String>>) entry.getValue();
				
				double[] newRanges = new double[rmap.size()+1];
				Iterator its = rmap.iterator();
				int pos=1;
				while (its.hasNext()){
					//Map.Entry ent2 = (Map.Entry) its.next();
					Tupel<Double, String> tup =  (Tupel<Double, String>) its.next();
					newRanges[pos]=tup.getFirst();
				}
				// Añado 
				Attribute att=exampleSet.getAttributes().get(attName);
				newRanges[0]= exampleSet.getStatistics(att, Statistics.MINIMUM);
				newRanges[newRanges.length-1]= exampleSet.getStatistics(att, Statistics.MAXIMUM);
			
				ranges.put(att, newRanges);
			}
			
			DiscretizationModelSeries model2 = new DiscretizationModelSeries(exampleSet);
			model2.setRanges(ranges, "range", getParameterAsBoolean(PARAMETER_USE_LONG_RANGE_NAMES));
			if (getParameterAsBoolean(PARAMETER_INCLUDE_LIMITS))
				model2.setLimitsIncluded(true);
			return model2;
			
			
		}

	}

	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();

		// FJ Modification
		types.add(new ParameterTypeBoolean(PARAMETER_ALL_ATTRIBUTES , "Indicates if ALL the attributes are discretized together.", false));
		types.add(new ParameterTypeBoolean(PARAMETER_INCLUDE_LIMITS, "Indicates if extrem limitsmust be included in the model.", false));
		
		return types;
	}
}
