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
package rm.utils;

/**
 * Class used to represent an object together with a double weight.
 * 
 * Creo una copia de este objeto porque en el m�todo compareTo
 * se hac�a una comparaci�n entre los hashings de los objetos en caso
 * de que los pesos fuesen iguales. Esto hac�a que los resultados
 * pudiesen variar entre una ejecuci�n y otra pues el hash depende
 * de la posici�n de memoria de un objeto.
 * 
 * @author F.J. Cuberos
 * @version $Id: WeightedObject.java,v 1.31 2008/07/26 19:22:55 fjcuberos Exp $
 * 
 */
public class WeightedObject<E> implements Comparable<WeightedObject<E>> {

	/** The object. */
	private final E object;

	/** The associated weight. */
	private final double weight;

	public WeightedObject(E object, double weight) {
		this.object = object;
		this.weight = weight;
	}

	public int compareTo(WeightedObject<E> objToCompare) {
		if (this.getWeight() > objToCompare.getWeight())
			return 1;
		else if (this.getWeight() < objToCompare.getWeight())
			return -1;
		else
			return 0;
	}

	/**
	 * Returns the object.
	 * 
	 * @return Object
	 */
	public E getObject() {
		return object;
	}

	/**
	 * Returns the weight.
	 * 
	 * @return double
	 */
	public double getWeight() {
		return weight;
	}

	public String toString() {
		return object.toString() + ":" + weight;
	}

	public boolean equals(Object obj) {
		if (obj instanceof WeightedObject) {
			if (((WeightedObject) obj).getObject().equals(getObject()))
				return true;
			else
				return false;
		} else
			return false;
	}

	public int hashCode() {
		return object.hashCode();
	}
}
