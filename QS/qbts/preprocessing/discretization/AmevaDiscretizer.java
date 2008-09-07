package qbts.preprocessing.discretization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.rapidminer.operator.OperatorDescription;

public class AmevaDiscretizer extends AbstractCAIMAmevaDiscretizer {

	public AmevaDiscretizer(OperatorDescription description) {
		super(description);
	}
	
	@Override
	protected List<Integer> compute_Values(List<CumDiscretizerBlock> lCumB) {
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
	

}
