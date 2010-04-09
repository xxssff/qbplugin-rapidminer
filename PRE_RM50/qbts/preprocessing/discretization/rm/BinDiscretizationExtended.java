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
//package qbts.preprocessing.discretization.rm;
package qbts.preprocessing.discretization.rm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qbts.preprocessing.discretization.Discretizer;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.discretization.BinDiscretization;
import com.rapidminer.operator.preprocessing.discretization.DiscretizationModel;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.tools.Ontology;

/**
 * This operator discretizes all numeric attributes in the dataset into nominal attributes. 
 * This discretization is performed by simple binning, i.e. the specified number of equally sized bins is created and the numerical values are simply sorted into
 * those bins. Skips all special attributes including the label.
 * The values of everi series are processed together, resulting the same discretization scheme for each series attribute.
 * 
 * @author Sebastian Land, Ingo Mierswa, F.J. Cuberos
 * @version $Id: BinDiscretizationSeries.java,v 1.0 2008/06/28 10:52:02 fjcuberos Exp $
 */
public class BinDiscretizationExtended extends Discretizer {
	/** Indicates the number of used bins. */
	public static final String PARAMETER_NUMBER_OF_BINS = "number_of_bins";


	public BinDiscretizationExtended(OperatorDescription description) {
		super(description);
	}
	

	public void computeValues (ExampleSet eSet, List<Attribute> lA, HashMap<Attribute, double[]> ranges ) throws OperatorException{
		int numberOfBins = getParameterAsInt(PARAMETER_NUMBER_OF_BINS);
		double[] binRange = new double[numberOfBins];
		double min=Double.POSITIVE_INFINITY;
		double max=Double.NEGATIVE_INFINITY;
		for (Attribute attribute : lA) {
			double mi = eSet.getStatistics(attribute, Statistics.MINIMUM);
			double ma = eSet.getStatistics(attribute, Statistics.MAXIMUM);
			if (mi<min) min=mi;
			if (ma>max) max=ma;
		}
		for (int b = 0; b < numberOfBins - 1; b++) {
			binRange[b] = min + (((double) (b + 1) / (double) numberOfBins) * (max - min));
		}
		binRange[numberOfBins - 1] = Double.POSITIVE_INFINITY;
		for (Attribute attribute : lA) {
			ranges.put(attribute, binRange);
		}
	}	
	
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		
		ParameterType type = new ParameterTypeInt(PARAMETER_NUMBER_OF_BINS, "Defines the number of bins which should be used for each attribute.", 2, Integer.MAX_VALUE, 2);
		type.setExpert(false);
		types.add(type);
		
		return types;
	}


}
