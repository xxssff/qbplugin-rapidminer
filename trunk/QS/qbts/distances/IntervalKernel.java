package qbts.distances;

import java.util.HashMap;
import java.util.SortedSet;

import qbts.preprocessing.discretization.DiscretizationModelSeries;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.Tupel;

public class IntervalKernel extends AbstractDiscretizedRealValueBasedSimilarity {

	
	
/*	PROBLEMA:
 * El Kernel intervalar depende de los valores límite de los intervalos de discretización
 * (tanto el menor como el mayor) pero en el caso de las discretizaciones los valores
 * mínimos y máximo de los rangos menor y mayor respectivamente son almacenados como 
 * -infinito y +infinito (o ni siquiera son almacenados).

 * Para obtener esos valores requeriría accceder al ExampleSet original y obtenerlos de
 * las estadísticas de los propios atributos. Pero se supone que el conjunto que llega aquí 
 * ya está discretizado por lo que eso se ha perdido.
 * 
 * Si me llegan los vectores de valores 
 * 
	¿Como relaciono elemento con atributo?
		La única forma es por posición. 
		Pero el almacenaje de ranges en el DM es por un tupla Atributo,cortes 
		por tanto habrá que almacenar una nueva tupla posición_atributo, cortes
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
