package qbts.distances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.example.Tools;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.MissingIOObjectException;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.discretization.DiscretizationModel;
import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.container.Tupel;
import com.rapidminer.tools.math.similarity.SimilarityMeasure;

/**
 * <p>This class implements the IntervalKernel similarity measure defined in </p>
 * <p>A New Kernel to Use With Discretized Temporal Series. Computación y Sistemas. 
 * Vol. 11. Núm. 1. 2007. Pag. 5-13 
 * Luis González Abril, Francisco Velasco Morente, 
 * Juan Antonio Ortega Ramirez, Francisco Javier Cuberos García Baquero. </p>
 * <p>and</p>
 * <p>A New Approach to Qualitative Learning in Time Series. Expert Systems With Applications. Vol. 42. 2009
 * Luis González Abril, Francisco Velasco Morente, 
 * Francisco Javier Cuberos García Baquero, Juan Antonio Ortega Ramirez.</p>
 *
 * <p>The kernel is applied to discretized example sets, and the similarity is computed
 *  based on the limits from each discretized values.</p>
 * 
 *  <p>A discretization model and a example set (numerical attributes) are removed from input.
 *  The ranges included in the discretization model are expanded with the min and max 
 *  values from the example set.</p>
 *  
 *  <p>The values of attributes part of a series are processed together.</p>
 * 
 * @author Francisco J. Cuberos
 * @version v 1.0  2009/04/09 11:02:00 fjcuberos 
 *
 */


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
	
	//Luis González Abril, Francisco Velasco Morente, Francisco Javier Cuberos García Baquero, Juan Antonio Ortega Ramirez:
	//	A New Approach to Qualitative Learning in Time Series. Expert Systems With Applications. Vol. 42. 2009 
	
	private SortedSet<Integer> in;
	private DiscretizationModel dm;
	private double [][] limits;
	private LogService log;

	public double calculateSimilarity(double[] e1, double[] e2) {
		return Kernel_Intervalar(e1, e2,  0.7);
	}
	public double calculateDistance(double[] e1, double[] e2) {
		return Kernel_Intervalar(e1, e2,  0.7);
	}
	
	public double similarity(double[] e1, double[] e2) {
		return Kernel_Intervalar(e1, e2,  0.7);
	}


	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
	}

	public void init(ExampleSet exampleSet, ParameterHandler parameterHandler, IOContainer ioContainer) throws OperatorException {

		log = ((Operator) parameterHandler).getLog(); //Local log of calling operator
		
		DiscretizationModel dm=null;
		ExampleSet eSet=null;
		try {
			dm = (DiscretizationModel) ioContainer.remove(Model.class);
			eSet = (ExampleSet) ioContainer.remove(ExampleSet.class);
		} catch (MissingIOObjectException e1) {
			 
			throw new OperatorException("IntervalKernel Similarity needs an additional exampleSet and a DiscretizationModel as inputs.");
		}
		
		Tools.onlyNumericalAttributes(exampleSet, "IntervalKernel Similarity init.");
		
		Map<String, SortedSet<Tupel<Double, String>>> ranges = (Map<String, SortedSet<Tupel<Double, String>>> ) dm.getRanges();

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


		
		try {
			limits = new double[lLim.size()][];
			limits = lLim.toArray(limits);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.log("Extrem Limits: ", LogService.MINIMUM);
		
		for (double[] l: limits){
			String cad="";
			for (double d: l)
				cad = cad + " , " + (new Double(d)).toString();
			log.log(cad, LogService.MINIMUM);
		}		
		
	}


	private void computeExtremLimits(ExampleSet eSet, List<Attribute> lA, Map<String, SortedSet<Tupel<Double, String>>> rangesMap, 
			List<double[]> lLimits) throws OperatorException {
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
		int last = limits.length-1;
		limits[last]=max;

		//check errors in extrem limits. Must be less than / greater than actual limits
		if ((limits[0]>=limits[1]) || (limits[last]<=limits[last-1])){
			String cad = "";
			for (Attribute att: lA){
				cad = cad + " " + att.getName();
			}
			log.logError("Errors in limits for Attribute/s: " +cad );
			log.logError("Limits in discretizationModel / ExampleSet are (" + limits[1]+" / " + limits[last-1] +
						") and  (" + limits[0]+" / " + limits[last]+")");
			throw new OperatorException("Error in input ExampleSet. Max & Min values included in Model ranges.");		
		}
	
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
