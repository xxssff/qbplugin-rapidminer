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
package qbts.distances;

import java.lang.reflect.Method;

import srctest.HelperOperatorConstructor;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Tools;
import com.rapidminer.operator.GroupedModel;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.MissingIOObjectException;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.NoiseOperator;
import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.tools.math.similarity.SimilarityMeasure;;


/**
 * A similarity measure based on "Longest Common String".  
 * 
 * @author F.J. Cuberos
 * @version $Id: QSISimilarity.java,v 2.0 2009/02/14 Exp $
 */

public class QSISimilarity extends SimilarityMeasure {
	private static final long serialVersionUID = 3640959448681534457L;
	

	/*
	protected double pointDistance(int i, int j, double[] ts1, double[] ts2) {
		return ( ts1[i] == ts2[j] ? 1 : 0);
	}
	*/


	public double calculateSimilarity(double[] pcVx, double[] pcVy){
		return similarity(pcVx,pcVy);
	}
	
	public double calculateDistance(double[] pcVx, double[] pcVy){
		return similarity(pcVx,pcVy);
	}
	
	public double similarity(double[] pcVx, double[] pcVy) {
		if ((pcVx == null) || (pcVy == null))
			 return Double.NaN;
		
		int sizex = pcVx.length;
		int sizey = pcVy.length;
		
		int valor;
		double dvalor = 0;
		int[][] D = new int[sizex + 1][sizey + 1];
	
		for (int i = 0; i < (sizex + 1); i++)
			for (int j = 0; j < (sizey + 1); j++)
				D[i][j] = 0;
	
		for (int i = 1; i <= sizex; i++)
			for (int j = 1; j <= sizey; j++) {
				if (pcVx[i - 1] == pcVy[j - 1]) {
					D[i][j] = D[i - 1][j - 1] + 1;
				} else if (D[i - 1][j] > D[i][j - 1]) {
					D[i][j] = D[i - 1][j];
				} else {
					D[i][j] = D[i][j - 1];
				}
			}
	
		valor = D[sizex][sizey];
	
		if (sizey > sizex)
			dvalor = ((double) valor) / ((double) sizey);
		else
			dvalor = ((double) valor) / ((double) sizex);
		return ((double) dvalor);
	}

	
	public void init(ExampleSet es) throws OperatorException {
        Tools.onlyNominalAttributes(es, "QSI similarity");
        //super.init(es);
	}	
	

}
