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

import java.util.ArrayList;

import yale.operator.preprocessing.discretization.DiscretizationModel;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Tools;
import com.rapidminer.operator.ContainerModel;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.similarity.attributebased.AbstractRealValueBasedSimilarity;


/**
 * A similarity measure based on "Longest Common String".  
 * 
 * @author F.J. Cuberos
 * @version $Id: QSISimilarity.java,v 1.1 2007/09/14 Exp $
 */

public class QSISimilarity extends AbstractRealValueBasedSimilarity {
	private static final long serialVersionUID = 3640959448681534457L;

	ContainerModel dm;
	
	protected double pointDistance(int i, int j, double[] ts1, double[] ts2) {
		return ( ts1[i] == ts2[j] ? 1 : 0);
	}

	public double similarity(double[] e1, double[] e2) {
		ArrayList<Double> l1 = new ArrayList<Double>();
		ArrayList<Double> l2 = new ArrayList<Double>();
		int i, j;
		/** Filter NaNs */
		for (i = 0; i < e1.length; i++) {
			double value = e1[i];
			if (!Double.isNaN(value)) {
				l1.add(value);
			}
		}
		for (i = 0; i < e2.length; i++) {
			double value = e2[i];
			if (!Double.isNaN(value)) {
				l2.add(new Double(value));
			}
		}
		/** Transform the examples to vectors */
		double[] ts1 = new double[l1.size()];
		double[] ts2 = new double[l2.size()];
		for (i = 0; i < ts1.length; i++) {
			ts1[i] = l1.get(i);
		}
		for (i = 0; i < ts2.length; i++) {
			ts2[i] = l2.get(i);
		}
		/** Build a point-to-point coincidence matrix */
		double[][] dP2P = new double[ts1.length][ts2.length];
		for (i = 0; i < ts1.length; i++) {
			for (j = 0; j < ts2.length; j++) {
				dP2P[i][j] = pointDistance(i, j, ts1, ts2);
			}
		}
		/** Check for some special cases due to ultra short time series */
		if (ts1.length == 0 || ts2.length == 0) {
			return Double.NaN;
		}
		if (ts1.length == 1 && ts2.length == 1) {
			return dP2P[0][0];
		}
		/**
		 * Build the optimal distance matrix using a dynamic programming approach
		 */
		double[][] D = new double[ts1.length][ts2.length];
		D[0][0] = dP2P[0][0]; // Starting point
		for (i = 1; i < ts1.length; i++) { // Fill the first column of our
			// distance matrix with optimal
			// values
			if (dP2P[i][0]==1){
				D[i][0]=1;
			}
			else{
			D[i][0] = D[i - 1][0];
			}
		}
		if (ts2.length == 1) { // TS2 is a point
			return ( (D[ts1.length][0] )/ ts1.length);
		}
		for (j = 1; j < ts2.length; j++) { // Fill the first row of our
			// distance matrix with optimal
			// values
			if (dP2P[0][j] == 1){
				D[0][j]=1;
			}
			else{
				D[0][j] = D[0][j - 1];	
			}
			
		}
		if (ts1.length == 1) { // TS1 is a point
			return ( D[0][ts2.length] / ts2.length);
		}

		/**
		 * Calculate the distance between the two time series through optimal alignment.
		 */
		for (i = 1; i < ts1.length; i++) { // Fill the rest
			for (j = 1; j < ts2.length; j++) {
				if (dP2P[i][j]==1){
					D[i][j] = dP2P[i-1][j-1] + 1;
				}
				else{
					double[] steps = {
							 D[i - 1][j], D[i][j - 1]};
					double max = Math.max(steps[0],steps[1]);
					D[i][j] = dP2P[i][j] + max;
				};
			}
		}
		return ( D[ts1.length][ts2.length] / Math.max(ts1.length ,ts2.length));
	}

	public void init(ExampleSet es) throws OperatorException {
		// hay que cargar el modelo de discretización. Lo que hay que hacer es deserializar el modelo 
		//¿como ejecutar un operador si el experimento ya está lanzado?
		Tools.onlyNominalAttributes(es, "QSI similarity");
		super.init(es);
	}
	
	public void setModel(ContainerModel model){
		//TODO: Tendrá que crear un Container con el último modelo que sea un PreprocessingModel
		// Y eso dependiendo de la similitud. De momento sólo para para el kernel
		dm=model;
	}

	public boolean isDistance() {
		return false;
	}
}
