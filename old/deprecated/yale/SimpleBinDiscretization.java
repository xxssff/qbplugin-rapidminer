/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2007 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as 
 *  published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version. 
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA.
 */
package deprecated.yale;

/*
 *  YALE - Yet Another Learning Environment
 *
 *  Copyright (C) 2001-2006 by the class authors
 *
 *  Project administrator: Ingo Mierswa
 *
 *      YALE was mainly written by (former) members of the
 *      Artificial Intelligence Unit
 *      Computer Science Department
 *      University of Dortmund
 *      44221 Dortmund,  Germany
 *
 *  Complete list of YALE developers available at our web site:
 *
 *       http://yale.sf.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as 
 *  published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version. 
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA.
 */
//package edu.udo.cs.yale.operator.preprocessing.discretization;

import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.PreprocessingOperator;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.UndefinedParameterError;


/**
 * An example filter that discretizes all numeric attributes in the dataset into
 * nominal attributes. This discretization is performed by simple binning. Skips
 * all special attributes including the label.
 * 
 * @author Ingo Mierswa, Buelent Moeller
 * @version $Id: SimpleBinDiscretization.java,v 1.9 2006/04/05 08:57:27
 *          ingomierswa Exp $
 */
@Deprecated
public class SimpleBinDiscretization extends PreprocessingOperator {

	
	
	public SimpleBinDiscretization(OperatorDescription description) {
		super(description);
	}

	@Override
	public Model createPreprocessingModel(ExampleSet exampleSet)
			throws OperatorException {

		List<AttributeBlock> lBlocks= AttributeBlock.getBlockList(exampleSet,getParameterAsBoolean("discretize_series"));

		DiscretizationModel model = new DiscretizationModel(exampleSet,lBlocks,getRanges(exampleSet,lBlocks));
		return model;
	}

	
	public double[][] getRanges(ExampleSet exampleSet, List<AttributeBlock> lBlocks) throws UndefinedParameterError {
		exampleSet.recalculateAllAttributeStatistics();
		int numberOfBins = getParameterAsInt("number_of_bins");
		
		double[][] ranges = new double[lBlocks.size()][numberOfBins];
		
		for (int a = 0; a < lBlocks.size(); a++) {
			AttributeBlock block =lBlocks.get(a); 

			if (block.isSeries() && block.isNumerical()){
				double min = Double.MAX_VALUE;
				double max = Double.MIN_VALUE;
				// get the min and max of every Attribute in the series
				
				String[] names=block.getAtriNames();
				for (int b=0;b<names.length;b++){
					Attribute attribute = exampleSet.getAttributes().get(names[b]);
					 double mi=exampleSet.getStatistics(attribute, Statistics.MINIMUM);
					if (mi<min)
						min=mi;
					double ma=exampleSet.getStatistics(attribute,Statistics.MAXIMUM);
					if (ma>max)
						max=ma;
				}
				
				for (int b = 0; b < numberOfBins-1; b++) {
					ranges[a][b] = min + (((double) (b + 1) / (double) numberOfBins) * (max - min));
				}
				ranges[a][numberOfBins-1]=Double.MAX_VALUE;
			}
			else
			{
				Attribute attribute = exampleSet.getAttributes().get(block.getAtriNames()[0]);
				if (! attribute.isNominal()) { // skip nominal attributes
					double min=exampleSet.getStatistics(attribute,Statistics.MINIMUM);
					double max=exampleSet.getStatistics(attribute,Statistics.MAXIMUM);
					for (int b = 0; b < numberOfBins-1; b++) {
						ranges[a][b] = min + (((double) (b + 1) / (double) numberOfBins) * (max - min));
					}
					ranges[a][numberOfBins-1]=Double.MAX_VALUE;
				}
			}
		}
		return ranges;
	}

	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		ParameterType type = new ParameterTypeInt("number_of_bins", "Defines the number of bins which should be used for each attribute.", 2, Integer.MAX_VALUE, 2);
		type.setExpert(false);
		types.add(type);
		type = new ParameterTypeBoolean("discretize_series", "Indicates if the attributes forming each series are discretized together.", false);
		type.setExpert(false);
		types.add(type);
		return types;
	}
}
