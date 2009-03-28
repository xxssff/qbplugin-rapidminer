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
package com.rapidminer.operator.preprocessing.discretization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
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
public class BinDiscretizationExtended extends BinDiscretization {
	
//TODO: Cuando existan constructores públicos de DiscretizationModel hay que volverlo a preprocessing.discretization.rm
	// otra posibilidad es crear un DiscretizatioModelSeries vacío que herede todo de DiscretizationModel
	
	public BinDiscretizationExtended(OperatorDescription description) {
		super(description);
	}


	public Model createPreprocessingModel(ExampleSet exampleSet) throws OperatorException {
		DiscretizationModel model = new DiscretizationModel(exampleSet);
		List<Attribute> lAtt=new ArrayList<Attribute>();

		exampleSet.recalculateAllAttributeStatistics();
		HashMap<Attribute, double[]> ranges = new HashMap<Attribute, double[]>();

		for (Attribute attribute : exampleSet.getAttributes()) {
			if (attribute.isNumerical()) { // skip nominal and date attributes
				switch(attribute.getBlockType()){
				case Ontology.VALUE_SERIES_START:
					if (lAtt.isEmpty())
						lAtt.add(attribute);
					else
						throw new OperatorException("Bad series definition (expected END, START found). ExampleSet definition error.");
					break;
				case Ontology.VALUE_SERIES_END:
					if (lAtt.isEmpty())
						throw new OperatorException("Bad series definition (END without START). ExampleSet definition error.");
					else{
						lAtt.add(attribute);
						computeValues(exampleSet,lAtt,ranges);
						lAtt.clear();
					}
					break;
				case Ontology.VALUE_SERIES:
					if (lAtt.isEmpty())
						throw new OperatorException("Bad series definition (element without START). ExampleSet definition error.");							
					else
						lAtt.add(attribute);
					break;
				case Ontology.SINGLE_VALUE:
					if (lAtt.isEmpty()){
						lAtt.add(attribute);
						computeValues(exampleSet,lAtt,ranges);
						lAtt.clear();
					}
					break;
				default:	
					throw new OperatorException("VALUE_MATRIX attributes not supported.");
				}	
			}
		}
		model.setRanges(ranges, "range", getParameterAsInt(PARAMETER_RANGE_NAME_TYPE));
		return (model);
	}

	private void computeValues(ExampleSet eSet, List<Attribute> lA, HashMap<Attribute, double[]> ranges ) throws OperatorException{
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
	


}
