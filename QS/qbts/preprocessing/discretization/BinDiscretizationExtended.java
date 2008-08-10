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
import java.util.HashMap;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.discretization.BinDiscretization;
import com.rapidminer.operator.preprocessing.discretization.DiscretizationModel;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.tools.Ontology;

/**
 * This operator discretizes all numeric attributes in the dataset into nominal attributes. 
 * This discretization is performed by simple binning, i.e. the specified number of equally sized bins is created and the numerical values are simply sorted into
 * those bins. Skips all special attributes including the label.
 * If the parameter Discretize_all_together  is true the values of all the input attributes are process together, resulting one discretization scheme.
 * 
 * @author Sebastian Land, Ingo Mierswa, F.J. Cuberos
 * @version $Id: BinDiscretizationSeries.java,v 1.0 2008/06/28 10:52:02 fjcuberos Exp $
 */
public class BinDiscretizationExtended extends BinDiscretization {
	/** Indicates if attributes must be grouped for discretization. */
	public static final String PARAMETER_ALL_ATTRIBUTES = "discretize_all_together";
	
	
	public BinDiscretizationExtended(OperatorDescription description) {
		super(description);
	}

	public Model createPreprocessingModel(ExampleSet exampleSet) throws OperatorException {
		if (getParameterAsBoolean(PARAMETER_ALL_ATTRIBUTES)){
			DiscretizationModelSeries model = new DiscretizationModelSeries(exampleSet);

			exampleSet.recalculateAllAttributeStatistics();
			int numberOfBins = getParameterAsInt(PARAMETER_NUMBER_OF_BINS);
			HashMap<Attribute, double[]> ranges = new HashMap<Attribute, double[]>();

			//Get the values of every attibute
			double min = Double.POSITIVE_INFINITY;
			double max = Double.NEGATIVE_INFINITY;
			for (Attribute attribute : exampleSet.getAttributes()) {
				if (attribute.isNumerical()) { // skip nominal and date attributes
					double mi = exampleSet.getStatistics(attribute, Statistics.MINIMUM);
					double ma = exampleSet.getStatistics(attribute, Statistics.MAXIMUM);
					if (mi < min) min=mi;
					if (ma > max) max=ma;
				}
			}
			// Compute the limits
			double[] binRange = new double[numberOfBins];
			for (int b = 0; b < numberOfBins - 1; b++) {
				binRange[b] = min + (((double) (b + 1) / (double) numberOfBins) * (max - min));
			}
			binRange[numberOfBins - 1] = Double.POSITIVE_INFINITY;
			// Assign the same limits to every attribute  
			for (Attribute attribute : exampleSet.getAttributes()) {
				ranges.put(attribute, binRange);
			}
			//ranges.put(null,binRange);
//			model.setOneSchemeForAll(true);
			model.setRanges(ranges, "range", getParameterAsBoolean(PARAMETER_USE_LONG_RANGE_NAMES));
			
			return (model);
		}
		else{
			return ( super.createPreprocessingModel(exampleSet));
		}
	}

	
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();

		ParameterType type = new ParameterTypeBoolean(PARAMETER_ALL_ATTRIBUTES , "Indicates if ALL the attributes are discretized together.", false);
		type.setExpert(false);
		types.add(type);
		return types;
	}
}
