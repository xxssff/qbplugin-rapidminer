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
import java.util.List;

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
			
			
/*			// Para todos los rangos
			int pos=(int) (examplesPerBin-1);
			double value=(double) valores.get(0);
			for (int i=0;i<numberOfBins-1;i++){
				do{
					pos++;
				}while(value==(double) valores.get(pos) );
				
				attributeRanges[i]=(value + (double) valores.get(pos))/2;
				pos=(int) (pos + examplesPerBin-1);
				value=(double) valores.get(pos);
			}*/
			attributeRanges[numberOfBins - 1] = Double.POSITIVE_INFINITY;
			// Se asignan los cortes a los atributos 
			for (Attribute currentAttribute : exampleSet.getAttributes()) {
				ranges.put(currentAttribute, attributeRanges);
			}
	/*		
			for (Attribute currentAttribute : exampleSet.getAttributes()) {
				double[] attributeRanges = new double[numberOfBins];
				ExampleSet sortedSet = new SortedExampleSet(exampleSet, currentAttribute, SortedExampleSet.INCREASING);

				
				// finding ranges
				double examplesPerBin = exampleSet.size() / (double) numberOfBins;
				double currentBinSpace = examplesPerBin;
				double lastValue = Double.NaN;
				int currentBin = 0;

				for (Example example : sortedSet) {
					double value = example.getValue(currentAttribute);
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
				attributeRanges[numberOfBins - 1] = Double.POSITIVE_INFINITY;
				ranges.put(currentAttribute, attributeRanges);
			}
			*/
			
			DiscretizationModelSeries model = new DiscretizationModelSeries(exampleSet);
			model.setRanges(ranges, "range", getParameterAsBoolean(PARAMETER_USE_LONG_RANGE_NAMES));
			return model;
		}
		else{
			return ( super.createPreprocessingModel(exampleSet));
		}

	}

	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();

		// FJ Modification
		types.add(new ParameterTypeBoolean(PARAMETER_ALL_ATTRIBUTES , "Indicates if ALL the attributes are discretized together.", false));
		return types;
	}
}
