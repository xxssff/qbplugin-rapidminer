package qbts.distances;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import qbts.preprocessing.discretization.DiscretizationModelSeries;

import com.rapidminer.example.Attribute;
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
			
	
	SortedSet<Integer> in;
	double[] discre; 
	
	public double similarity(double[] e1, double[] e2) {
		return Kernel_Intervalar(e1, e2, discre, 0.7);
	}

	public boolean isDistance() {
		return false;
	}
	
	public void init(ExampleSet es, DiscretizationModelSeries dm)throws OperatorException {
		
		if (dm.getExtremLimits()==null)
			throw new OperatorException("Error. The distance IntervalKernel needs extrem limits of discretization.");
		
		super.init(es);
		
		// Almacenar el esquema de discretizaci�n.
		// Como s�lo se aplica a series s�lo hay uno y no uno por atributo.
		
		//Attribute firstAtt = (Attribute) (es.getAttributes().iterator().next());
		//SortedSet<Tupel<Double, String>> cortes = dm.getRanges().get( firstAtt.getName());
		
		SortedSet<Tupel<Double, String>> cortes = (SortedSet<Tupel<Double, String>>)((Map.Entry)dm.getRanges().entrySet().iterator().next()).getValue();
		
		discre= new double[cortes.size()+1];
		int p=0;
		discre[p++] = dm.getExtremLimits()[0][0];
		
		Iterator itc = cortes.iterator();
		while ( itc.hasNext()){
			Tupel<Double, String> tu = (Tupel<Double, String>) itc.next();
			discre[p++]=tu.getFirst();
		}
		discre[--p] = dm.getExtremLimits()[0][1];
	}


	//********************************************************************************
	//********************************************************************************
	/*
	 * Computes the Intervalar Distance presented in ...(cite).
	 */
	private double Kernel_Intervalar(double[] dVx, double[] dVy, double[] discre,double paraSimil) {
		// La distancia intervalar est� definida para s�lo una serie.
	
		double lambda = paraSimil;
	
		int rangos = discre.length - 1;
		// CREO LA MATRIZ DE DISTANCIAS ENTRE INTERVALOS
		double[][] dist = new double[rangos][rangos]; //mantiene la distancia
	
		for (int i = 0; i < rangos; i++)
			for (int j = i; j < rangos; j++)
				if (i == j)
					dist[i][j] = 0;
				else {
					// diferencia de centros al cuadrado + diferencia de radios
					// al cuadrado
					dist[i][j] = Math.pow((discre[j] + discre[j + 1]) / 2
							- (discre[i] + discre[i + 1]) / 2, 2)
							+ Math.pow((discre[j + 1] - discre[j]) / 2
									- (discre[i + 1] - discre[i]) / 2, 2);
					dist[j][i] = dist[i][j];
				}
	
		double coste = 0.0;
		int sizex = dVx.length;
		for (int i = 0; i < sizex; i++)
			coste += Math.pow(lambda, dist[(int) dVx[i]][(int) dVy[i]]);
	
		return coste;
	}


}
