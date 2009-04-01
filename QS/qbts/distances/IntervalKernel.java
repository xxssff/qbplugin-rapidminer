package qbts.distances;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import srctest.HelperOperatorConstructor;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.GroupedModel;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.MissingIOObjectException;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.discretization.DiscretizationModel;
import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.tools.container.Tupel;
import com.rapidminer.tools.math.similarity.SimilarityMeasure;;

//public class IntervalKernel extends AbstractDiscretizedRealValueBasedSimilarity {
public class IntervalKernel extends SimilarityMeasure{
	
	
/*	PROBLEMA:
 * 
 * El Kernel intervalar depende de los valores límite de los intervalos de discretización
 * (tanto el menor como el mayor) pero en el caso de las discretizaciones los valores
 * mínimos y máximo de los rangos menor y mayor respectivamente son almacenados como 
 * -infinito y +infinito (o ni siquiera son almacenados).

 * Para obtener esos valores requeriría accceder al ExampleSet original y obtenerlos de
 * las estadísticas de los propios atributos. 
 *
 * En el Init se debe comprobar
 *     1) que el conjunto de ejemplos es una sóla serie nominal
 *     2) que en la entrada del operador hay 
 *     		Un modelo de discretización
 *     		Un conjunto de ejemplos que suponemos que es el que llega como principal pero no discretizado
 *     
 * Se obtienen los cortes desde el modelo y los límites desde el conjunto no discretizado.
 * 
 */
			
	
	SortedSet<Integer> in;
	double[] discre; 
	DiscretizationModel dm;

	public double calculateSimilarity(double[] e1, double[] e2) {
		return Kernel_Intervalar(e1, e2, discre, 0.7);
	}
	public double calculateDistance(double[] e1, double[] e2) {
		return Kernel_Intervalar(e1, e2, discre, 0.7);
	}
	
	public double similarity(double[] e1, double[] e2) {
		return Kernel_Intervalar(e1, e2, discre, 0.7);
	}

/*	public boolean isDistance() {
		return false;
	}*/
	
	

	public void init(ExampleSet exampleSet, ParameterHandler parameterHandler, IOContainer ioContainer) throws OperatorException {

		//check if the exampleSet has only a series
		/*
		 * Para todos los atributos 
		 *   El primero es un Series_start
		 *   Los demás serán Series
		 *   El último es un Series_end
		 *   
		 * Sino se cumple generar un warning - se aplicará el mismo esquema de discretización (el primero que se encuentre)
		 * 
		 * 
		 */
		
		DiscretizationModel dm=(DiscretizationModel) ioContainer.get(Model.class);
		SortedSet<Tupel<Double, String>> cortes = (SortedSet<Tupel<Double, String>>)((Map.Entry) dm.getRanges().entrySet().iterator().next().getValue() );
		ExampleSet eS = (ExampleSet) ioContainer.get(ExampleSet.class);
		
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
		// La distancia intervalar está definida para sólo una serie.
	
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
							+ Math.pow((discre[j + 1] - discre[j]) / 2 - (discre[i + 1] - discre[i]) / 2, 2);
					dist[j][i] = dist[i][j];
				}
	
		double coste = 0.0;
		int sizex = dVx.length;
		for (int i = 0; i < sizex; i++)
			coste += Math.pow(lambda, dist[(int) dVx[i]][(int) dVy[i]]);
	
		return coste;
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		// TODO Apéndice de método generado automáticamente
		
		//init(exampleSet,(DiscretizationModelSeries)getInput(Model.class));
	}
	
	
	public void init(ExampleSet exampleSet, ParameterHandler parameterHandler) {
	    // case parameter handler to Operator
	    Operator operator = (Operator)parameterHandler;
	    
	    String methodName="getInput";
		HelperOperatorConstructor hOp=new HelperOperatorConstructor();
		IOContainer ret= (IOContainer) hOp.invokePrivateMethodOperator(operator, methodName, new  Object[] {null});

	    // do whatever you want...
		try {
//			 retrieve the model
			dm = ret.get(Model.class);
		} catch (MissingIOObjectException e) {
			e.printStackTrace();
		}
	}
	


}
