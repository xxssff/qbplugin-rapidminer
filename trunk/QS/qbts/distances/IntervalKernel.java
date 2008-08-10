package qbts.distances;

import qbts.preprocessing.discretization.DiscretizationModelSeries;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;

public class IntervalKernel extends AbstractDiscretizedRealValueBasedSimilarity {

	
	
/*	Si me llegan los vectores de valores 
	¿Como relaciono elemento con atributo?
		La única forma es por posición.*/
			
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
