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


import com.rapidminer.example.Example;

/**
 * A helperclass for the preprocessing operator FrequencyDiscretizer. It allows
 * to store an example with one numerical attribute value. Due to the
 * implementation of the Comparable Interface Arrays of this class may be sorted
 * by Arrays.sort() after the values. So it is possible to sort without loosing
 * the connection between value and its example.
 * 
 * @author Sebastian Land
 * @version $Id: FrequencyDiscretizerExample.java,v 1.3 2006/08/03 14:39:29 ingomierswa Exp $
 */
@Deprecated
public class FrequencyDiscretizerExampleBlock implements Comparable<FrequencyDiscretizerExampleBlock> {

	private double attributeValue;
	private Example attributeExample;
	private String  attributeName;

	public FrequencyDiscretizerExampleBlock(double value, Example example, String attributeName) {
		this.attributeValue = value;
		this.attributeExample = example;
		this.attributeName= attributeName;
	}

	public double getValue() {
		return this.attributeValue;
	}

	public Example getExample() {
		return this.attributeExample;
	}

	public int compareTo(FrequencyDiscretizerExampleBlock toCompare) {
		return Double.compare(this.getValue(), toCompare.getValue());
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	

}
