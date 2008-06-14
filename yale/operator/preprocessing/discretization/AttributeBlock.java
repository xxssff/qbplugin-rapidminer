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

package yale.operator.preprocessing.discretization;

import java.util.ArrayList;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.tools.Ontology;

/**
 * A block represents a set of attributes from a series or a single attribute.  
 * 
 * @author F.J. Cuberos
 *
 */
public class AttributeBlock {
	private String[] atriNames;
	private boolean isSeries;
	private boolean isNumerical;
	
	/**
	 * Return a List<AttributeBlock> with a block for each single value attribute 
	 *    or ,if the dSeries parameter is true, a block for each series and for each single attribute.
	 */
	public static List<AttributeBlock> getBlockList(ExampleSet eSet,boolean dSeries){
		
		List<AttributeBlock> lBlocks = new ArrayList<AttributeBlock>();

		List<String> names=null;
		AttributeBlock bl=null;
		for (Attribute currAtt : eSet.getAttributes()){
			int type = currAtt.getBlockType();
			if ((dSeries) && ((names!=null)||(type==Ontology.VALUE_SERIES_START))){
				if (currAtt.getBlockType()==Ontology.VALUE_SERIES_START){
					bl=new AttributeBlock();
					bl.isSeries = false;
					bl.isNumerical = !currAtt.isNominal();
					names= (new ArrayList<String>());
					names.add(currAtt.getName());
					
				}
				else if (type == Ontology.VALUE_SERIES_END){
					names.add(currAtt.getName());
					bl.atriNames=names.toArray(new String[names.size()]);
					lBlocks.add(bl);
					names=null;
				}else{
					names.add(currAtt.getName());
				}
			}
			else{
				bl=new AttributeBlock();
				bl.isSeries = false;
				bl.atriNames= new String[1];
				bl.atriNames[0]=currAtt.getName();
				bl.isNumerical = !currAtt.isNominal();
			}
		}
		return lBlocks;
		}

	public boolean isNumerical() {
		return isNumerical;
	}
	public void setNumerical(boolean isNumerical) {
		this.isNumerical = isNumerical;
	}
	public boolean isSeries() {
		return isSeries;
	}
	public void setSeries(boolean isSeries) {
		this.isSeries = isSeries;
	}

	public String[] getAtriNames() {
		return atriNames;
	}

	public void setAtriNames(String[] atriNames) {
		this.atriNames = atriNames;
	}
}
