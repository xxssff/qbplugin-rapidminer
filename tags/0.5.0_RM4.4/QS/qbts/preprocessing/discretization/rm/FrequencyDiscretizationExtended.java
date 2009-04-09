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

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.example.set.SortedExampleSet;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.preprocessing.discretization.DiscretizationModel;
import com.rapidminer.operator.preprocessing.discretization.FrequencyDiscretization;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.conditions.BooleanParameterCondition;

/**
 * This operator discretizes all numeric attributes in the dataset into nominal attributes. This discretization is performed by equal frequency binning, i.e. the thresholds of all bins is selected in a way that all bins contain the same number of
 * numerical values. The number of bins is specified by a parameter, or, alternatively, is calculated as the square root of the number of examples with non-missing values (calculated for every single attribute). Skips all special attributes including
 * the label.
 * 
 * @author Sebastian Land, Ingo Mierswa
 * @version $Id: FrequencyDiscretization.java,v 1.5 2008/05/09 19:23:25 ingomierswa Exp $
 */
public class FrequencyDiscretizationExtended extends FrequencyDiscretization {
	/** The parameter name for &quot;If true, the number of bins is instead determined by the square root of the number of non-missing values.&quot; */
	public static final String PARAMETER_USE_SQRT_OF_EXAMPLES = "use_sqrt_of_examples";

	/** The parameter for the number of bins. */
	public static final String PARAMETER_NUMBER_OF_BINS = "number_of_bins";

	
	public FrequencyDiscretizationExtended(OperatorDescription description) {
		super(description);
	}
	
	
	
	public void computeValues (ExampleSet eSet, List<Attribute> lA, HashMap<Attribute, double[]> ranges ) throws OperatorException{
		
		// Get and check parametervalues
		boolean useSqrt = getParameterAsBoolean(PARAMETER_USE_SQRT_OF_EXAMPLES);
		int numberOfBins = getParameterAsInt(PARAMETER_NUMBER_OF_BINS);
		if (!useSqrt) {
			// if not automatic sizing of bins, use parametervalue
			numberOfBins = getParameterAsInt(PARAMETER_NUMBER_OF_BINS);
			if (numberOfBins >= (eSet.size() - 1)) {
				throw new UserError(this, 116, PARAMETER_NUMBER_OF_BINS, "number of bins must be smaller than number of examples (here: " + eSet.size() + ")");
			}
		} else {
			eSet.recalculateAllAttributeStatistics();
		}

		if (useSqrt) {
			for (Attribute currentAttribute : lA) {
					numberOfBins = numberOfBins + eSet.size() - (int) eSet.getStatistics(currentAttribute, Statistics.UNKNOWN);
			}
			numberOfBins = (int) Math.round(Math.sqrt(numberOfBins));
		}

		List<Double> valores = new ArrayList<Double>();
		for (Example example : eSet) {
			for (Attribute currentAttribute : eSet.getAttributes()) {
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

		// Se asignan los cortes a los atributos 
		for (Attribute currentAttribute : eSet.getAttributes()) {
			ranges.put(currentAttribute, attributeRanges);
		}

	}
	
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		
		types.add(new ParameterTypeBoolean(PARAMETER_USE_SQRT_OF_EXAMPLES, "If true, the number of bins is instead determined by the square root of the number of non-missing values.", false));
		
		ParameterType type = new ParameterTypeInt(PARAMETER_NUMBER_OF_BINS, "Defines the number of bins which should be used for each attribute.", 2, Integer.MAX_VALUE, 2);
		type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_USE_SQRT_OF_EXAMPLES, false, false));
		type.setExpert(false);
		types.add(type);
		
		types.add(new ParameterTypeCategory(PARAMETER_RANGE_NAME_TYPE, "Indicates if long range names including the limits should be used.", DiscretizationModel.RANGE_NAME_TYPES, DiscretizationModel.RANGE_NAME_LONG));
		
		return types;
	}
}
