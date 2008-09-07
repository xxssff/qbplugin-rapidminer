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

import java.util.Iterator;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.operator.AbstractModel;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.Ontology;


/**
 * The scheme used in the discretization of an ExampleSet is saved (if selected)
 * for later use. 
 * The attributes of ExampleSet can be discretized in blocks, one block for series,
 * if the user select the option.
 * The DiscretizarionModel include the list of blocks and the ranges for each blocks.   
 * The discretized labels from ranges (for block b) are 
 * 	   ranges[b][0]   <= range1 <= ranges[b][1]
 *     ranges[b][1]   <  range2 <= ranges[b][2]
 *     ...
 *     ranges[b][n-1] <  rangeN <= ranges[b][n]
 *      
 * The application over other ExampleSet (with differents min and max values)
 * need the first and last limits were considered to -infinite and +infinite.
 * 
 * @author F.J. Cuberos
 *
 */
@Deprecated
public class DiscretizationModel extends AbstractModel {

	private List<AttributeBlock> lBlocks;
	private double[][] ranges;
	static final long serialVersionUID = -7791326331963220278L;
	//TODO: Add serialVersionUID field


	/*
	 * Create a new DiscretizationModel.
	 */
	public DiscretizationModel(ExampleSet eSet,List<AttributeBlock> lBlocks,double[][] ranges){
		super(eSet);
		this.lBlocks=lBlocks;
		this.ranges=ranges;

	}
	

	public ExampleSet apply(ExampleSet testSet) throws OperatorException {
		// change attribute type
		for (int a = 0; a < lBlocks.size(); a++) {
			AttributeBlock currBlock=lBlocks.get(a);
			if (currBlock.isNumerical()){     // skip nominal blocks
				String[] names=currBlock.getAtriNames();
				for (int i=0;i<names.length;i++){
					Attribute attCurr =testSet.getAttributes().get(names[i]);
					attCurr=testSet.getAttributes().replace(attCurr, AttributeFactory.changeValueType(attCurr, Ontology.NOMINAL));

					for (int b = 0; b < ranges[a].length; b++) {
						attCurr.getMapping().mapString("range" + (b + 1));
					}
				}
			}
		}

		// change data
		Iterator<Example> reader = testSet.iterator();
		while (reader.hasNext()) {
			Example example = reader.next();
			for (int a = 0; a < lBlocks.size(); a++) {
				AttributeBlock currBlock=lBlocks.get(a);
				if (currBlock.isNumerical() && ranges[a]!=null){
					String[] names=currBlock.getAtriNames();
					for (int i=0;i<names.length;i++){
						Attribute attribute =testSet.getAttributes().get(names[i]);
						double value = example.getValue(attribute);
						for (int b = 0; b < ranges[a].length; b++) {
							if (value <= ranges[a][b]) {
								double res = attribute.getMapping().mapString("range" + (b + 1));
								example.setValue(attribute, res);
								break;
							}
						}
					}
				}
			}
		}
		return(testSet);
	}
	
/*	public void readData(ObjectInputStream in) throws IOException {
		try{
			lBlocks=(List<AttributeBlock>) in.readObject();
			ranges=(double [][]) in.readObject();
		}catch (Exception e){
			throw new IOException(e.getMessage());
		}
	}

	
	public void writeData(ObjectOutputStream out) throws IOException {
		out.writeObject(ranges);
        out.writeObject(lBlocks);
	}*/

	public String toString() {
		String cad;
		cad="DiscretizationModel \nDiscretization Scheme \n";
		Iterator<AttributeBlock> itBlock = lBlocks.iterator();
		int b=0;
		while (itBlock.hasNext()){
			AttributeBlock block=itBlock.next();
			cad=cad+"  Block "+b+" from Attributes "+block.getAtriNames()[0] +" to "+ block.getAtriNames()[block.getAtriNames().length-1]+" -> -infinite , ";
			for (int i=1;i<ranges[b].length-1;i++){
				cad=cad+ ranges[b][i]+" , ";
			}
			cad=cad+"infinite \n";
			b++;
		}
		return cad;
	}
	

}
