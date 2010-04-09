package qbts.preprocessing.discretization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;

public class CAIMDiscretizer extends Discretizer {

	public CAIMDiscretizer(OperatorDescription description) {
		super(description);
	}

	public void computeValues (ExampleSet eSet, List<Attribute> lA, HashMap<Attribute, double[]> ranges ) throws OperatorException{

		Attribute labelAtt=eSet.getAttributes().getLabel();
		if (!labelAtt.isNominal()){
			throw new UnsupportedOperationException("The Ameva discretization method needs a nominal label attribute!");			
		}
		int numClasses = labelAtt.getMapping().getValues().size();


		List<CumDiscretizerBlock> lVal = new ArrayList<CumDiscretizerBlock>();
		for (Attribute attribute : lA) {
			if (attribute.isNumerical()) { // skip nominal and date attributes
				for (Example example : eSet) {
					double value = example.getValue(attribute);
					if (!Double.isNaN(value)) {
						lVal.add(new CumDiscretizerBlock(example.getValue(attribute),
								(int) example.getLabel(),numClasses));
					}
				}
			}
		}

		Collections.sort(lVal);  //sort the values
		//compute the acumulators
		CumDiscretizerBlock.computeAcums(lVal);
		//delete duplicated values updating the occurences and acumulators
		CumDiscretizerBlock.joinValues(lVal);

		//Apply method
		List<Integer> cortes=compute_Values( lVal);

		double[] attributeRanges = new double[cortes.size()-1];
		for (int i=1;i<cortes.size()-1;i++){
			attributeRanges[i-1]=lVal.get(cortes.get(i)).getValue();
		}
		attributeRanges[cortes.size() - 2] = Double.POSITIVE_INFINITY;

		for (Attribute currentAttribute : lA) {
			ranges.put(currentAttribute, attributeRanges);
		}
	}
	
	 protected List<Integer> compute_Values( List<CumDiscretizerBlock> lCumB ) {
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
	


}
