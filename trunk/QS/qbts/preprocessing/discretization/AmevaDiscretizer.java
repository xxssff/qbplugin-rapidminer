package qbts.preprocessing.discretization;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import yale.operator.preprocessing.discretization.AttributeBlock;
import yale.operator.preprocessing.discretization.DiscretizationModel;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
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
 * nominal attributes. This discretization is performed by the CAIM method over
 * all special attributes including the label.
 * 
 * For information on CAIM method read 
 * 
 *
 *

 * @author F.J. Cuberos
 * @version $Id: SimpleBinDiscretization.java,v 1.9 2006/04/05 08:57:27
 *          ingomierswa Exp $
 */
public class AmevaDiscretizer extends PreprocessingOperator {

	/** Indicates if long range names should be used. */
	public static final String PARAMETER_USE_LONG_RANGE_NAMES = "use_long_range_names";
	/** Indicates if attributes must be grouped for discretization. */
	public static final String PARAMETER_ALL_ATTRIBUTES = "discretize_all_together";
//	 FJ Begin
	public static final String PARAMETER_INCLUDE_LIMITS = "include_extrem_limits";
	// FJ End


	public AmevaDiscretizer(OperatorDescription description) {
		super(description);
	}

	@Override
	public Model createPreprocessingModel(ExampleSet exampleSet)
	throws OperatorException {

		Attribute labelAtt=exampleSet.getAttributes().getLabel();
		if (!labelAtt.isNominal()){
			throw new UnsupportedOperationException("The Ameva discretization method need a nominal label attribute!");			
		}
		int numClasses = labelAtt.getMapping().getValues().size();

		exampleSet.recalculateAllAttributeStatistics();
		HashMap<Attribute, double[]> ranges = new HashMap<Attribute, double[]>();

		List<CumDiscretizerBlock> lVal = new ArrayList<CumDiscretizerBlock>();
		for (Attribute attribute : exampleSet.getAttributes()) {
			if (attribute.isNumerical()) { // skip nominal and date attributes
				for (Example example : exampleSet) {
						double value = example.getValue(attribute);
						if (!Double.isNaN(value)) {
							lVal.add(new CumDiscretizerBlock(example.getValue(attribute),
									(int) example.getLabel(),numClasses));
					}
				}
				if (!getParameterAsBoolean(PARAMETER_ALL_ATTRIBUTES)){
					Collections.sort(lVal);  //sort the values
					//compute the acumulators
					CumDiscretizerBlock.computeAcums(lVal);
					//delete duplicated values updating the occurences and acumulators
					CumDiscretizerBlock.joinValues(lVal);

					//Apply CAIM method
					List<Integer> cortes=compute_CAIM( lVal);
					
					double[] attributeRanges = new double[cortes.size()-1];
					for (int i=1;i<cortes.size()-1;i++){
						attributeRanges[i-1]=lVal.get(cortes.get(i)).getValue();
						}
					attributeRanges[cortes.size() - 2] = Double.POSITIVE_INFINITY;
					ranges.put(attribute, attributeRanges);

					lVal = new ArrayList<CumDiscretizerBlock>();
				}
			}
		}

		if (getParameterAsBoolean(PARAMETER_ALL_ATTRIBUTES)){
			Collections.sort(lVal);  //sort the values
			//compute the acumulators
			CumDiscretizerBlock.computeAcums(lVal);
			//delete duplicated values updating the occurences and acumulators
			CumDiscretizerBlock.joinValues(lVal);

			//Apply CAIM method
			List<Integer> cortes=compute_CAIM( lVal);
			
			double[] attributeRanges = new double[cortes.size()-1];
			for (int i=1;i<cortes.size()-1;i++){
				attributeRanges[i-1]=lVal.get(cortes.get(i)).getValue();
				}
			attributeRanges[cortes.size() - 2] = Double.POSITIVE_INFINITY;
			
			for (Attribute currentAttribute : exampleSet.getAttributes()) {
				ranges.put(currentAttribute, attributeRanges);
			}
		}

		
		DiscretizationModelSeries model = new DiscretizationModelSeries(exampleSet);
		model.setRanges(ranges, "range", getParameterAsBoolean(PARAMETER_USE_LONG_RANGE_NAMES));
		
		if (getParameterAsBoolean(PARAMETER_INCLUDE_LIMITS)){
			model.setLimitsIncluded(true);
			/*			CASO GENERAL
			 * 				double[][] values = new double[exampleSet.getAttributes().size()][2];
							int index = 0;
							for (Attribute attribute : exampleSet.getAttributes()){
								values[index][0] = valores.get(0);
								values[index++][1] = valores.get(valores.size()-1);
							}*/

			double[][] values = new double[1][2];
			values[0][0] = lVal.get(0).getValue();
			values[0][1] = lVal.get(lVal.size()-1).getValue();
			model.setExtremLimits(values);
		}

		
		return model;
	} 
	
	 private List<Integer> compute_CAIM( List<CumDiscretizerBlock> lCumB ) {
		 List<Integer> cortes=new ArrayList<Integer>();
		 cortes.add(Integer.valueOf(0));
		 cortes.add(Integer.valueOf(lCumB.size()-1));
		 
		double tmpCriterion = Double.MIN_VALUE;
		int paso = 1;
		double globalCriterion = Double.MIN_VALUE;
		int bestPos;
		
		do {
			bestPos = nextMaxAmeva(cortes, lCumB);
			tmpCriterion=lCumB.get(bestPos).getMethodValue();
					
			if (tmpCriterion > globalCriterion)  {
				cortes.add(Integer.valueOf(bestPos));
				globalCriterion = tmpCriterion;
			} else if (tmpCriterion == globalCriterion)
				tmpCriterion = Double.MIN_VALUE;
			paso++;
		} while (tmpCriterion >= globalCriterion) ;
		return cortes;
	}
	
	 


	/**
	 * Computes the best Ameva value obtained adding a new component to the
	 * limits list (D). Check all the not used values from the initial set.
	 */
	private int nextMaxAmeva(List<Integer> D, List<CumDiscretizerBlock> lCumB) {

		int numClases=lCumB.get(0).getAcum().length;
		
		Iterator<CumDiscretizerBlock> itCum = lCumB.iterator();
		double maximo = Double.MIN_VALUE;
		int posMejor=-1;
		
		int actual=0;
		while (itCum.hasNext()){
			CumDiscretizerBlock b=itCum.next();
			if (D.contains(Integer.valueOf(actual))){
				b.setMethodValue(Double.MIN_VALUE);
			}
			else{
				// añadir  a la lista D
				D.add(Integer.valueOf(actual));
				// ordenar D
				Collections.sort(D);
				// calcular la matriz
				int numCor=D.size()-1;
				double[][] matriz=new double[numClases+1][numCor+1];
				
				
				for (int r=0;r<numCor; r++ ){
					for (int c=0;c<numClases;c++){
						if (r==0){
						    matriz[c][r]=lCumB.get(D.get(1)).getAcum()[c];
						}
						else
						{
							matriz[c][r]=lCumB.get(D.get(r+1)).getAcum()[c]-lCumB.get(D.get(r)).getAcum()[c];
						}
						matriz[numClases][numCor]+=matriz[c][r];
						matriz[numClases][r]+=matriz[c][r];
						matriz[c][numCor]+=matriz[c][r];
					}
				}
				
				double valorAmeva=0;
				for (int icorte = 0; icorte < numCor; icorte++)
					for (int cla = 0; cla < numClases; cla++) {
						valorAmeva += Math.pow(matriz[cla][icorte], 2)
								/ (matriz[cla][numCor] * matriz[numClases][icorte]);
					}

				//compute the value and store in actual element
				valorAmeva = valorAmeva - 1;
				valorAmeva = valorAmeva * matriz[numClases][numCor] / ((numClases-1) * (numCor));
	
				b.setMethodValue(valorAmeva);
				if (valorAmeva>maximo){
					posMejor=actual;
					maximo=valorAmeva;
				}
				//Borro el último insertado en  la lista
				D.remove(Integer.valueOf(actual));
			}
			actual++;
		}
		return posMejor;
	 }
	

	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		ParameterType type = new ParameterTypeBoolean(PARAMETER_ALL_ATTRIBUTES , "Indicates if ALL the attributes are discretized together.", false);
		type.setExpert(false);
		types.add(type);
		types.add(new ParameterTypeBoolean(PARAMETER_USE_LONG_RANGE_NAMES, "Indicates if long range names including the limits should be used.", true));
		types.add(new ParameterTypeBoolean(PARAMETER_INCLUDE_LIMITS, "Indicates if extrem limits must be included in the model.", false));
		return types;
	}
}
