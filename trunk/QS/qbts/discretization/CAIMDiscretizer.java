package qbts.discretization;

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
import java.util.Iterator;
import java.util.List;

import yale.operator.preprocessing.discretization.AttributeBlock;
import yale.operator.preprocessing.discretization.DiscretizationModel;

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
import com.rapidminer.tools.LogService;

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
public class CAIMDiscretizer extends PreprocessingOperator {



	public CAIMDiscretizer(OperatorDescription description) {
		super(description);
	}

	@Override
	public Model createPreprocessingModel(ExampleSet exampleSet)
	throws OperatorException {

		List<AttributeBlock> lBlocks= AttributeBlock.getBlockList(exampleSet,getParameterAsBoolean("discretize_series"));

		DiscretizationModel model = new DiscretizationModel(exampleSet,lBlocks,getRanges(exampleSet,lBlocks));
		return model;
	}


	private double[][] getRanges(ExampleSet exampleSet, List<AttributeBlock> lBlocks) throws UndefinedParameterError {

		LogService.getGlobal().log("LLEGA A GETRANGES",LogService.MAXIMUM);
		/* LogService.logMessage("LLEGA A GETRANGES2",LogService.MAXIMUM);*/
		
		exampleSet.recalculateAllAttributeStatistics();

		double[][] ranges=new double[lBlocks.size()][];
		int numClasses = exampleSet.getAttributes().getLabel().getMapping().getValues().size();
	
		for (int a = 0; a < lBlocks.size(); a++) {
			AttributeBlock block =lBlocks.get(a); 

			List<CumDiscretizerBlock> lVal = new ArrayList<CumDiscretizerBlock>();
			
			if (block.isNumerical()){
				Iterator<Example> itEx = exampleSet.iterator();
				while(itEx.hasNext()){
					Example ex=itEx.next();
					
					// Compute  CAIM limits for (int atri=block.getStart();atri<=block.getEnd();atri++){
					String[] names=block.getAtriNames();
					for (int atri=0;atri<names.length;atri++){
						//get all the possible values
						lVal.add(new CumDiscretizerBlock(ex.getValue(exampleSet.getAttributes().get(names[atri])),
								(int) ex.getLabel(),numClasses));
					}
				}
				
				Collections.sort(lVal);  //sort the values
				//compute the acumulators
				CumDiscretizerBlock.computeAcums(lVal);
				//delete duplicated values updating the occurences and acumulators
				CumDiscretizerBlock.joinValues(lVal);

				//Apply CAIM method
				List<Integer> cortes=compute_CAIM( lVal);
				// update ranges matrix
				ranges[a]=new double[cortes.size()];
				ranges[a][0]=Double.MIN_VALUE;
				for (int i=1;i<cortes.size()-1;i++){
					ranges[a][i]=lVal.get(cortes.get(i)).getValue();
					}
				ranges[a][cortes.size()-1]=Double.MAX_VALUE;
			}
		}


		String cad="";
		for (int i=0;i<ranges.length;i++){
			for (int j=0;j<ranges[0].length;j++){
				cad+=" \t" + ranges[i][j];
			}
			cad+="\n";
		}
				
		LogService.getGlobal().log("Discretization Ranges= " +cad, LogService.MAXIMUM);

		return ranges;
	}
	
	 private List<Integer> compute_CAIM( List<CumDiscretizerBlock> lCumB ) {
		 List<Integer> cortes=new ArrayList<Integer>();
		 cortes.add(Integer.valueOf(0));
		 cortes.add(Integer.valueOf(lCumB.size()-1));
		 
		int numClases=lCumB.get(0).getAcum().length;

		double tmpCriterion = Double.MIN_VALUE;
		int paso = 1;
		double globalCriterion = Double.MIN_VALUE;
		int bestPos;
		
		do {
			bestPos = nextMaxCAIM(cortes, lCumB);
			tmpCriterion=lCumB.get(bestPos).getMethodValue();
					
			if ((tmpCriterion > globalCriterion) || (paso < numClases)) {
				cortes.add(Integer.valueOf(bestPos));
				globalCriterion = tmpCriterion;
			} else if (tmpCriterion == globalCriterion)
				tmpCriterion = Double.MIN_VALUE;
			paso++;
		} while ((tmpCriterion >= globalCriterion) || (paso < numClases));
		return cortes;
	}
	

	/**
	 * Computes the best CAIM value obtained adding a new component to the
	 * limits list (D). Check all the not used values from the initial set.
	 */
	private int nextMaxCAIM(List<Integer> D, List<CumDiscretizerBlock> lCumB) {

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
				double[][] matriz=new double[numClases][D.size()-1];
				/**TODO: La matriz puede ser un vector puesto que no se relacionan valores 
				 *       de columnas diferentes.
				 */ 
				
				double valorCAIM=0;
				for (int r=0;r<(D.size()-1); r++ ){
					double total=0;
					double maxQ=0;
					for (int c=0;c<numClases;c++){
						if (r==0){
						    matriz[c][r]=lCumB.get(D.get(1)).getAcum()[c];
							if (matriz[c][r]>maxQ)
						    maxQ=matriz[c][r];
						}
						else
						{
							matriz[c][r]=lCumB.get(D.get(r+1)).getAcum()[c]-lCumB.get(D.get(r)).getAcum()[c];
							if (matriz[c][r]>maxQ)
								maxQ=matriz[c][r];
						}
						total+=matriz[c][r];
					}
					valorCAIM += (Math.pow(maxQ, 2) / total);
				}
				//compute the value and store in actual element
				valorCAIM=valorCAIM/(D.size()-1);
				b.setMethodValue(valorCAIM);
				if (valorCAIM>maximo){
					posMejor=actual;
					maximo=valorCAIM;
				}
				//Borro el último insertado en  la lista
				D.remove(Integer.valueOf(actual));
			}
			actual++;
		}
		return posMejor;
	 }
	
	
	/*
	//**********************************************************************************************
		//**********************************************************************************************
	static final	 public void compute_CAIM_Ameva(List D, int metodo, int numClases,
				int numCortes, int[][] acumulados) {
			int[] mejor = new int[1];
			mejor[0] = 0;
			float tmpCriterion = 0;

			int paso = (metodo == Ameva) ? 10 * numClases : 1;
			float globalCriterion = 0;

			do {
				tmpCriterion = nextMaxCAIM(D, numCortes, numClases, acumulados,
							mejor);

				if ((tmpCriterion > globalCriterion) || (paso < numClases)) {
					D.add(new Integer(mejor[0]));
					globalCriterion = tmpCriterion;
				} else if (tmpCriterion == globalCriterion)
					tmpCriterion = -9999999;
				paso++;
			} while ((tmpCriterion >= globalCriterion) || (paso < numClases));
		}



	//**********************************************************************************************
	//**********************************************************************************************
	 //**
	 * Computes the best CAIM value obtained adding a new component to the
	 * limits list (D). Check all the not used values from the initial set.
	 *//*
	static final private float nextMaxCAIM(List D, int numCortes, int numClases,
			int[][] acu, int[] amejor) {

		float maxActual = -99999;

		for (int co = 1; co < numCortes; co++) {
			boolean vale = true;
			for (int k = 0; k < D.size(); k++)
				if (co == ((Integer) D.get(k)).intValue())
					vale = false;
			if (vale) {
				//Add current limit to auxiliar list.
				LinkedList Daux = new LinkedList(D);
				Daux.add(new Integer(co));
				Collections.sort(Daux);
				float valorCAIM = 0;
				for (int icorte = 1; icorte < Daux.size(); icorte++) {
					float totalM = 0;
					float MaxQ = 0;
					for (int cla = 0; cla < numClases; cla++) {
						// debo contar los elementos que ay en el intervalo
						// actual
						int qi = 0;
						for (int k = ((Integer) Daux.get(icorte - 1))
								.intValue(); k < ((Integer) Daux.get(icorte))
								.intValue(); k++) {
							qi += acu[cla][k];
						}
						if (qi > MaxQ)
							MaxQ = qi;
						totalM += qi;
					}
					valorCAIM += (Math.pow(MaxQ, 2) / totalM);
				}
				valorCAIM = valorCAIM / (Daux.size() - 1);
				if (valorCAIM > maxActual) {
					maxActual = valorCAIM;
					amejor[0] = co;
				}
			}
		}

		return maxActual;
	}
	  */

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
