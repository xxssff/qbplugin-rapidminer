package qbts.distances;

import java.util.HashMap;
import java.util.SortedSet;

import qbts.preprocessing.discretization.DiscretizationModelSeries;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.Tupel;

public class IntervalKernel extends AbstractDiscretizedRealValueBasedSimilarity {

	
	
/*	PROBLEMA:
 * El Kernel intervalar depende de los valores l�mite de los intervalos de discretizaci�n
 * (tanto el menor como el mayor) pero en el caso de las discretizaciones los valores
 * m�nimos y m�ximo de los rangos menor y mayor respectivamente son almacenados como 
 * -infinito y +infinito (o ni siquiera son almacenados).

 * Para obtener esos valores requerir�a accceder al ExampleSet original y obtenerlos de
 * las estad�sticas de los propios atributos. Pero se supone que el conjunto que llega aqu� 
 * ya est� discretizado por lo que eso se ha perdido.
 * 
 * Si me llegan los vectores de valores 
 * 
	�Como relaciono elemento con atributo?
		La �nica forma es por posici�n. 
		Pero el almacenaje de ranges en el DM es por un tupla Atributo,cortes 
		por tanto habr� que almacenar una nueva tupla posici�n_atributo, cortes
		HashMap<Integer, SortedSet<Tupel<Double, String>>>
		Aunque como voy a perder toda referencia a los atributos puedo hacer una array
		con tamtas filas como atributos y cada una tantas columnas como cortes.
		*/
			
	HashMap<Integer, SortedSet<Tupel<Double, String>>> iMap;
	SortedSet<Integer> in;
	
	
	public double similarity(double[] e1, double[] e2) {
		
		return 0;
	}

	public boolean isDistance() {
		return false;
	}
	
	public void init(ExampleSet es, DiscretizationModelSeries dm)throws OperatorException {
		//TODO Procesar el modelo y almacenarlo
		super.init(es);
	}


}
