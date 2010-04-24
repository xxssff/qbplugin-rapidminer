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
package qbts.preprocessing.discretization.rm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import qbts.preprocessing.discretization.DiscretizationModelSeries;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.preprocessing.discretization.DiscretizationModel;
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
	public static final String PARAMETER_ALL_ATTRIBUTES = "discretize_all_together";
	public static final String PARAMETER_INCLUDE_LIMITS = "include_extrem_limits";
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
				numberOfBins = (int) Math.round(Math.sqrt(numberOfBins));
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
			

			
/*			if (getParameterAsBoolean(PARAMETER_INCLUDE_LIMITS)){
				
				// Ampliar attributeRanges por el principio
				double[] nattRanges=new double[attributeRanges.length+1];
				// y grabar el valor m�nimo
				attributeRanges[0] = valores.get(0);
				// y el valor m�ximo
				attributeRanges[numberOfBins - 1] = valores.get(valores.size()-1);
				for(int i=0;i<attributeRanges.length;i++)
					nattRanges[i+1]=attributeRanges[i];
				attributeRanges=nattRanges;
			}
			else*/
			
				attributeRanges[numberOfBins - 1] = Double.POSITIVE_INFINITY;

			// Se asignan los cortes a los atributos 
			for (Attribute currentAttribute : exampleSet.getAttributes()) {
				ranges.put(currentAttribute, attributeRanges);
			}
			
			DiscretizationModelSeries model = new DiscretizationModelSeries(exampleSet);
			model.setRanges(ranges, "range", getParameterAsBoolean(PARAMETER_USE_LONG_RANGE_NAMES));
			if (getParameterAsBoolean(PARAMETER_INCLUDE_LIMITS)){
				model.setLimitsIncluded(true);
/*			CASO GENERAL
 * 				double[][] values = new double[exampleSet.getAttributes().size()][2];
				int index = 0;
				for (Attribute attribute : exampleSet.getAttributes()){
					values[index][0] = valores.get(0);
					values[index++][1] = valores.get(valores.size()-1);
				}*/
				double[][] values = new double[1][2];
				values[0][0] = valores.get(0);
				values[0][1] = valores.get(valores.size()-1);
				model.setExtremLimits(values);
			}
				
			return model;
		}
		else{
			if (getParameterAsBoolean(PARAMETER_INCLUDE_LIMITS)){
				throw new OperatorException("Extrem limits can�t be included if attributes aren�t discretize altogether.");
			}
			DiscretizationModel model=(DiscretizationModel) super.createPreprocessingModel(exampleSet);

			// AMPLIACION PARA INTEGRAR DISCRETIZATIONMODELSERIES CON DISCRETIZATIONMODEL
			// Ver DiscretizationModelSeries 
/*
			DiscretizationModel m = (DiscretizationModel) super.createPreprocessingModel(exampleSet);
			DiscretizationModelSeries model=new DiscretizationModelSeries(exampleSet,m);

			if (getParameterAsBoolean(PARAMETER_INCLUDE_LIMITS)){
				model.setLimitsIncluded(true);
				double[][] values = new double[exampleSet.getAttributes().size()][2];
				int index = 0;
				for (Attribute attribute : exampleSet.getAttributes()){
					values[index][0] = exampleSet.getStatistics(attribute, Statistics.MINIMUM);
					values[index++][1] =  exampleSet.getStatistics(attribute, Statistics.MAXIMUM);
				}
				model.setExtremLimits(values);
			}
	*/
			return model;
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