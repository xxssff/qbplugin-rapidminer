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

import java.util.HashMap;
import java.util.SortedSet;

import srctest.HelperOperatorConstructor;

import com.rapidminer.example.ExampleSet;
import rm.utils.DiscretizationModel;
import com.rapidminer.tools.Tupel;

/**
 * The generic discretization model.
 * 
 * @author Sebastian Land
 * @version $Id: DiscretizationModel.java,v 1.13 2008/05/28 10:52:02 ingomierswa Exp $
 */

public class DiscretizationModelSeries extends DiscretizationModel {
    static final long serialVersionUID = -1792856176020803111L;

    private boolean limitsIncluded = false;
    private double[][] extremLimits;

    
	public DiscretizationModelSeries(ExampleSet exampleSet) {
		this(exampleSet, true);
	}
	
	public DiscretizationModelSeries(ExampleSet exampleSet, boolean removeUseless) {
		super(exampleSet,removeUseless);
	}

	public HashMap<String, SortedSet<Tupel<Double, String>>> getRanges(){
		return this.rangesMap;
	}

	public boolean isLimitsIncluded() {
		return limitsIncluded;
	}

	public void setLimitsIncluded(boolean limitsIncluded) {
		this.limitsIncluded = limitsIncluded;
	}

	public double[][] getExtremLimits() {
		return extremLimits;
	}

	public void setExtremLimits(double[][] extremLimits) {
		this.extremLimits = extremLimits;
	}
	
}
