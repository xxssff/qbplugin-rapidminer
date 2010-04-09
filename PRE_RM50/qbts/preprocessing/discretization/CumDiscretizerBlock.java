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


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.rapidminer.example.Example;

/**
 * A helperclass for the preprocessing operator based in accumlation of values 
 * between discretization limits as Chi Square. 
 * It allows 
 * to store an example with one numerical attribute value. Due to the
 * implementation of the Comparable Interface Arrays of this class may be sorted
 * by Arrays.sort() after the values. So it is possible to sort without loosing
 * the connection between value and its example.
 * 
 * @author Sebastian Land, F.J. Cuberos
 * @version $Id: FrequencyDiscretizerExample.java,v 1.3 2006/08/03 14:39:29 ingomierswa Exp $
 */
public class CumDiscretizerBlock implements Comparable<CumDiscretizerBlock> {

	private double attributeValue;
	private Example attributeExample;
	private int  attributeNumber;
	private int[]  occu;
	private int[]  acum;
	private int  clas;
	private double methodValue;	

	/**
	 * Crea un bloque auxiliar para acumular las apariciones que permite simplificar los discretizadores
	 * que requieran de ello.
	 * @param value				valor numérico que identifica al bloque
	 * @param example			ejemplo en que aparece
	 * @param attributeNumber	atributo del ejemplo en que aparece (para el caso de series)
	 * @param clase				número de clase a que pertenece el ejemplo anterior
	 * @param numClases			total de clases que existen
	 * 
	 * Cada bloque contiene además de estos parámetros un par de vectores, del tamaño del número de clases,
	 * que presentan 
	 * occu   el número de apariciones para este valor que hay de cada clase
	 * acum   el número de apariciones de cada clase de valores iguales o menores a éste
	 * 
	 * Además de crear el bloque se contabiliza la ocurrencia del valor indicado en la clase indicada.
	 */
	
	public CumDiscretizerBlock(double value, Example example, int attributeNumber, int clase, int numClases) {
		if (clase>numClases)  {
			
		}
		this.attributeValue = value;
		this.attributeExample = example;
		this.attributeNumber= attributeNumber;
		this.occu= new int[numClases];
		this.occu[clase]=1;
		this.acum= new int[numClases];
		this.clas = clase;
	}

	public CumDiscretizerBlock(double value, int clase, int numClases) {
		if (clase>numClases)  {
			
		}
		this.attributeValue = value;
		this.attributeExample = null;
		this.attributeNumber= -1;
		this.occu= new int[numClases];
		this.occu[clase]=1;
		this.acum= new int[numClases];
		this.clas = clase;
	}

	
	public String toString(){
		String cad;
		cad="V:"+ String.format("%12s",String.format("%f",attributeValue))+ " P:"+ String.format("%4d",attributeNumber)+ " C:"+
		String.format("%3d",clas)+ "  OCU:"+Arrays.toString(occu) +"  ACU:"+Arrays.toString(acum); 
		return cad;
	}
	
	// ACUMULA
	/*
	 * Coloca los acumulados del objeto actual como los acumulados
	 * del objeto de entrada más las ocurrencias de él mismo.
	 */
	public void Acumula(CumDiscretizerBlock other){
		int size = this.getAcum().length;
		
		for(int i=0;i<size;i++){
			this.acum[i]=other.acum[i]+this.occu[i];
		}
	}
	
	/**
	 * Calcula los acumulados para todos los elementos de la lista que se
	 * pasa como parámetros. Cada elemento tiene como acumulados para cada clase
	 * los valores acumulados del elemento inmediatamente anterior más las apariciones
	 * que presente.
	 * @param li
	 */
	public static void computeAcums(List<CumDiscretizerBlock> li){
		Iterator<CumDiscretizerBlock> it=li.iterator();
		CumDiscretizerBlock b=null,a=null;
		if(it.hasNext()){
			b=it.next();
            b.acum=b.occu;		
		}
		int size =b.getAcum().length;
		while(it.hasNext()){
			a=b;
			b=it.next();
			for(int i=0;i<size;i++){
				b.acum[i]=a.acum[i]+b.occu[i];
			}
			
		}
	}
	
/**
 * Ordena los elementos de la lista y elimina los que tengan un valor ya
 * existente arrastrando la información de acumulados y apariciones. En caso de que
 * los duplicados tengan la misma clase se deja en el que queda pero en caso
 * contrario se coloca un valor de -1.
 * @param li
 */
	public static void joinValues(List<CumDiscretizerBlock> li){
		
		ListIterator<CumDiscretizerBlock> it=li.listIterator();
		CumDiscretizerBlock b=null,a=null;
		if(it.hasNext()){
			b=it.next();
		}
		int size =b.getAcum().length;
		while(it.hasNext()){
			a=b;
			b=it.next();
			if (b.attributeValue==a.attributeValue){
				a.acum=b.acum;
				for(int i=0;i<size;i++){
					a.occu[i]+=b.occu[i];
				}
				if (b.clas!=a.clas){
					a.clas=-1;
				}
				
				it.remove();
				b=a;
			}
		}

	}
	
	public double getValue() {
		return this.attributeValue;
	}

	public Example getExample() {
		return this.attributeExample;
	}

	public int compareTo(CumDiscretizerBlock toCompare) {
		return Double.compare(this.getValue(), toCompare.getValue());
	}
	
	public int getAttributeNumber() {
		return attributeNumber;
	}



	public int[] getAcum() {
		return acum;
	}
	
	public void setAcum(int[] acum) {
		this.acum = acum;
	}

	public int getClas() {
		return clas;
	}

	public void setClas(int clas) {
		this.clas = clas;
	}

	public int[] getOccu() {
		return occu;
	}
	

	public void setOccu(int[] occu) {
		this.occu = occu;
	}

	public double getMethodValue() {
		return methodValue;
	}

	public void setMethodValue(double methodValue) {
		this.methodValue = methodValue;
	}


}
