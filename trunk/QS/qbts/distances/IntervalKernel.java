package qbts.distances;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import srctest.HelperOperatorConstructor;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.operator.GroupedModel;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.MissingIOObjectException;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.discretization.DiscretizationModel;
import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.tools.Ontology;
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
			
	
	private SortedSet<Integer> in;
	private DiscretizationModel dm;
	//Map<String, double[]> limits;
	private double [][] limits;

	public double calculateSimilarity(double[] e1, double[] e2) {
		return Kernel_Intervalar(e1, e2,  0.7);
	}
	public double calculateDistance(double[] e1, double[] e2) {
		return Kernel_Intervalar(e1, e2,  0.7);
	}
	
	public double similarity(double[] e1, double[] e2) {
		return Kernel_Intervalar(e1, e2,  0.7);
	}

/*	public boolean isDistance() {
		return false;
	}*/
	
	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		// TODO Apéndice de método generado automáticamente
	}

	public void init(ExampleSet exampleSet, ParameterHandler parameterHandler, IOContainer ioContainer) throws OperatorException {

		// TODO Hay que comprobar varias cosas
		/* 
		 * 1) que lo que se lee de la entrada se borra o sigue existiendo después del KNN
		 * 2) si hay dos discretizadores en la entrada ¿están agrupados?  
		 * 
		 */

		//NO es necesario porque como cada atributo tiene su conjunto de cortes se toma del Modelo que hay en la entrada

		DiscretizationModel dm=(DiscretizationModel) ioContainer.get(Model.class);
		Map<String, SortedSet<Tupel<Double, String>>> ranges = (Map<String, SortedSet<Tupel<Double, String>>> ) dm.getRanges();

		// Se debería comprobar que eSet y exampleSet tienen los mismos atributos y con idéntico nombre
		// pero uno será numérico y el otro nominal.

		ExampleSet eSet = (ExampleSet) ioContainer.get(ExampleSet.class);

		/*
		 * Pero en algún sitio hay que almacenar los extremos del conjunto de ejemplos 
		 * Repitiendo parte del código de BinDiscrerization donde se calculan los máximos y 
		 *   almacenándolos para cada atributo teniendo en cuenta los que son una serie   
		 *
		 * El valor mínimno hay que añadirlo al SortedSet
		 * Ya está cargado el valor +infinito en el objeto ranges y se puede sustituir 
		 *   por el límite máximo que se calcule, no tengo que hacer nada más porque 
		 *   los valores van a venir ya discretizados.
		 */
		List<Attribute> lAtt=new ArrayList<Attribute>();
		List<double[]> lLim=new ArrayList<double[]>();

		eSet.recalculateAllAttributeStatistics();

		for (Attribute attribute : eSet.getAttributes()) {
			if (attribute.isNumerical()) { // skip nominal and date attributes
				switch(attribute.getBlockType()){
				case Ontology.VALUE_SERIES_START:
					if (lAtt.isEmpty())
						lAtt.add(attribute);
					else
						throw new OperatorException("Bad series definition (expected END, START found). ExampleSet definition error.");
					break;
				case Ontology.VALUE_SERIES_END:
					if (lAtt.isEmpty())
						throw new OperatorException("Bad series definition (END without START). ExampleSet definition error.");
					else{
						lAtt.add(attribute);
						computeExtremLimits(eSet,lAtt,ranges,lLim);
						lAtt.clear();
					}
					break;
				case Ontology.VALUE_SERIES:
					if (lAtt.isEmpty())
						throw new OperatorException("Bad series definition (element without START). ExampleSet definition error.");							
					else
						lAtt.add(attribute);
					break;
				case Ontology.SINGLE_VALUE:
					if (lAtt.isEmpty()){
						lAtt.add(attribute);
						computeExtremLimits(eSet,lAtt,ranges,lLim);
						lAtt.clear();
					}
					break;
				default:	
					throw new OperatorException("VALUE_MATRIX attributes not supported.");
				}	
			}
		}
		limits = (double[][]) lLim.toArray();
	}


	private void computeExtremLimits(ExampleSet eSet, List<Attribute> lA, Map<String, SortedSet<Tupel<Double, String>>> rangesMap, 
			List<double[]> lLimits) {
		double min=Double.POSITIVE_INFINITY;
		double max=Double.NEGATIVE_INFINITY;

		for (Attribute attri : lA) {
			double mi = eSet.getStatistics(attri, Statistics.MINIMUM);
			double ma = eSet.getStatistics(attri, Statistics.MAXIMUM);
			if (mi<min) min=mi;
			if (ma>max) max=ma;
		}
		
		SortedSet<Tupel<Double, String>> ranges = rangesMap.get(lA.get(0).getName()); 
		
		double[] limits=new double[ranges.size()+1];
		int pos=0;
		limits[pos++]=min;
		for (Tupel<Double, String> et : ranges){
			limits[pos++] = et.getFirst(); 
		}
		limits[limits.length-1]=max;

		for (int i =0; i<lA.size();i++) {
			lLimits.add(limits);
		}
	}


	
	//********************************************************************************
	//********************************************************************************
	/*
	 * Computes the Intervalar Distance presented in ...(cite).
	 */
	private double Kernel_Intervalar(double[] dVx, double[] dVy,double paraSimil) {
		if ((dVx == null) || (dVy == null))
			 return Double.NaN;
		
		double lambda = paraSimil;

		double coste = 0.0;
		int sizex = dVx.length;
		for (int i = 0; i < sizex; i++){
			int x=(int) dVx[i];
			int y=(int) dVy[i];

			double dist;
				dist = Math.pow((limits[i][x] + limits[i][x+1]) / 2
						- (limits[i][y] + limits[i][y + 1]) / 2, 2)
						+ Math.pow((limits[i][y + 1] - limits[i][y]) / 2 - (limits[i][x + 1] - limits[i][x]) / 2, 2);
				coste += Math.pow(lambda, dist);
			}
	
		return coste;
	}

	
	
	//********************************************************************************
	//********************************************************************************
	/*
	 * Computes the Intervalar Distance presented in ...(cite).
	 */
	private double oldKernel_Intervalar(double[] dVx, double[] dVy, double[] discre,double paraSimil) {
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


}
