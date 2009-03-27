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
package zz.deprecated.yale;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.tools.Ontology;


/**
 * An example filter that discretizes all numeric attributes in the dataset into
 * nominal attributes. This discretization is performed by equal frequency
 * binning. The number of bins is determined by a parameter, or, chooseable via
 * another parameter, by the square root of the number of examples with
 * non-missing values (calculated for every single attribute). Skips all special
 * attributes including the label.
 * 
 * @author Sebastian Land, Ingo Mierswa
 * @version $Id: FrequencyDiscretizer.java,v 1.12 2006/04/14 11:42:27
 *          ingomierswa Exp $
 */
@Deprecated
public class FrequencyDiscretizer extends Operator {

	/** The parameter name for &quot;If true, the number of bins is instead determined by the square root of the number of non-missing values.&quot; */
	public static final String PARAMETER_USE_SQRT_OF_EXAMPLES = "use_sqrt_of_examples";
    public static final String PARAMETER_NUMBER_OF_BINS = "number_of_bins";
    
	public FrequencyDiscretizer(OperatorDescription description) {
		super(description);
	}

	public Class[] getInputClasses() {
		return new Class[] { ExampleSet.class };
	}

	public Class[] getOutputClasses() {
        if (getParameterAsBoolean("return_preprocessing_model"))
            return new Class[] { ExampleSet.class, Model.class };
        else
            return new Class[] { ExampleSet.class };
	}
	
	public IOObject[] apply() throws OperatorException {
		ExampleSet exampleSet = getInput(ExampleSet.class);
		int block=0;


		// Get and check parametervalues
		boolean useSqrt = getParameterAsBoolean(PARAMETER_USE_SQRT_OF_EXAMPLES);
		int numberOfBins = 0;
		if (!useSqrt) {
			// if not automatic sizing of bins, use parametervalue
			numberOfBins = getParameterAsInt(PARAMETER_NUMBER_OF_BINS);
			if (numberOfBins >= (exampleSet.size() - 1)) {
				throw new UserError(this, 116, PARAMETER_NUMBER_OF_BINS, "number of bins must be smaller than number of examples (here: " + exampleSet.size() + ")");
			}
		}

		List<AttributeBlock> lBlocks= AttributeBlock.getBlockList(exampleSet,getParameterAsBoolean("discretize_series"));
		double[][] ranges=new double[lBlocks.size()][];

		for (int i = 0; i < lBlocks.size(); i++) {
			AttributeBlock currBlock =lBlocks.get(i); 
			List<FrequencyDiscretizerExampleBlock> exampleAttributeRel = new ArrayList<FrequencyDiscretizerExampleBlock>();
			if (currBlock.isNumerical()){     // skip nominal blocks
				String[] names=currBlock.getAtriNames();
				int numberOfNotMissing = 0;
				// get examples with value and attributeNumber and store as
				// a relation and compute the number of not missing values
				Iterator<Example> iterator = exampleSet.iterator();
				while (iterator.hasNext()) {
					Example currentExample = iterator.next();
					for (int a=0;a<names.length;a++){
						Attribute atri=exampleSet.getAttributes().get(names[a]);
						if (!Double.isNaN(currentExample.getValue(atri))) {
							numberOfNotMissing++;
							exampleAttributeRel.add( new FrequencyDiscretizerExampleBlock(currentExample.getValue(atri), currentExample,names[a]));
						}
					}
					checkForStop();
				}
				// sort pairs and compute number of Bins
				Collections.sort(exampleAttributeRel);
				if (useSqrt) {
					numberOfBins = (int) Math.round(Math.sqrt(numberOfNotMissing));
				}
				// change attributetype of the attributes in currentBlock
				for (int a=0;a<names.length;i++){
					Attribute currentAttribute = exampleSet.getAttributes().get(names[a]);
					currentAttribute = exampleSet.getAttributes().replace(currentAttribute, AttributeFactory.changeValueType(currentAttribute, Ontology.NOMINAL));
					for (int b = 0; b < numberOfBins; b++) {
						currentAttribute.getMapping().mapString("range" + (b + 1));

					}
					// set new nominal value
					double examplesPerBin = numberOfNotMissing / (double) numberOfBins;
					double currentBinSpace = 0;
					int currentBin = 0;
					ranges[i]=new double[numberOfBins];
					Iterator<FrequencyDiscretizerExampleBlock> iteRel = exampleAttributeRel.iterator();
					while (iteRel.hasNext()) {
						FrequencyDiscretizerExampleBlock fb = iteRel.next();

						// change bin if full and not last
						if (currentBinSpace < 1 && currentBin < numberOfBins) {
							currentBin++;
							currentBinSpace = currentBinSpace + examplesPerBin;
						}
						// set number of bin as nominal value
						fb.getExample().setValue(exampleSet.getAttributes().get(fb.getAttributeName()), "range" + currentBin);
						currentBinSpace = currentBinSpace - 1;
						if (currentBinSpace<1){
							ranges[i][currentBin-1]=fb.getValue();
						}
					}
					block++;


				}
			}
		}

		if (getParameterAsBoolean("return_preprocessing_model")) {
			DiscretizationModel model = new DiscretizationModel(exampleSet,lBlocks,ranges);
			return new IOObject[] { exampleSet, model };
		} else {
			return new IOObject[] { exampleSet };
		}
		
	}

	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		
		types.add(new ParameterTypeBoolean("return_preprocessing_model", "Indicates if the preprocessing model should also be returned", false));
		ParameterType type = new ParameterTypeInt("number_of_bins", "Defines the number of bins which should be used for each attribute.", 2, Integer.MAX_VALUE, 2);
		type.setExpert(false);
		types.add(type);
		type = new ParameterTypeBoolean("use_sqrt_of_examples", "If true, the number of bins is instead determined by the square root of the number of non-missing values.", false);
		type.setExpert(false);
		types.add(type);
		type = new ParameterTypeBoolean("discretize_series", "Indicates if the attributes forming each series are discretized together.", false);
		type.setExpert(false);
		types.add(type);

		return types;
	}
}



