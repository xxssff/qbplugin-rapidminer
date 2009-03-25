package qbts.distances;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import qbts.preprocessing.discretization.DiscretizationModelSeries;
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
			
	
	SortedSet<Integer> in;
	double[] discre; 
	GroupedModel dm;

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
	
	
	
	public void init(ExampleSet es, DiscretizationModelSeries dm)throws OperatorException {
		
		if (dm.getExtremLimits()==null)
			throw new OperatorException("Error. The distance IntervalKernel needs extrem limits of discretization.");
		
		//super.init(es);
		
		// Almacenar el esquema de discretización.
		// Como sólo se aplica a series sólo hay uno y no uno por atributo.
		
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
		Method metodo = hOp.findPrivateMethod(operator.getClass().toString(), methodName );
		IOContainer ret= (IOContainer) hOp.invokePrivateMethodOperator(operator, metodo, new  Object[] {null});

	    // do whatever you want...
		try {
//			 retrieve the model
			dm = ret.get(GroupedModel.class);
		} catch (MissingIOObjectException e) {
			e.printStackTrace();
		}
	}
	


}
