
package qbts;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.Value;
import com.rapidminer.operator.learner.AbstractLearner;
import com.rapidminer.operator.learner.LearnerCapability;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;

/**
 * <p>
 * DiscretizationLearner computes the discretization scheme of the input
 * exampleSet. Returns a DiscretizationModel.
 * 
 * </p>
 * 
 * @author F.J. Cuberos
 * @version $Id: DiscretizationLearner.java,v 1.0 2004/09/19 Exp $
 */
public class DiscretizationLearner extends AbstractLearner {

//	private SeriesDiscretizationModel mod;


private int totalInt = 0;

private int metSimil = -1;

private double paraSimil = 0.0;

private int NNpoints = 0;

private boolean seriesFixedLength;

private int VLS_SIZE_POS;

private boolean Nor;

private boolean Tip;

private boolean Dif;

private int NivelLog = 0;
private int IntervalosBuscados=0;

/** Empty Constructor */
public DiscretizationLearner(OperatorDescription description) {
	super(description);
	addValue(new Value("num_intervals",
			"The number of discretization intervals obtained.") {
		public double getValue() {
			return totalInt;
		}
	});
}

//**********************************************************************************************
//	********************************************************************************************
/**
 * Apply. Get a ExampleSet from input. Call the learn method. Return a
 * DiscretizationModel and the input exampleSet discretized.
 */
public IOObject[] apply() throws OperatorException {

	LogService.logMessage("Start SeriesDiscretizationLearner.apply",
			LogService.STATUS			);
	ExampleSet eSet = (ExampleSet) getInput(ExampleSet.class);
	DiscretizationModel mod2 = (DiscretizationModel) learn(eSet);
    
	
	if ((boolean) getParameterAsBoolean("disable_exampleset_output"))
	    return new IOObject[] { mod2};
	else
		return new IOObject[] {mod2, mod2.getExampleSet() };
	
}



//**********************************************************************************************
//**********************************************************************************************
/**
 * Learn. Computes the discretization schema. Creates a model calling the
 * SeriesDiscretizationModel.Constructor .
 */
public Model learn(ExampleSet eSet) throws OperatorException {

	//@SuppressWarnings("unused") double[][] esquema = null;

	
	String smetodo = getParameterAsString("method");
	
	IntervalosBuscados =  getParameterAsInt("number_of_intervals");

	int discMet = -1;
	NivelLog = this.getExperiment().getRootOperator().getParameterAsInt(
			"logverbosity");
	LogService.logMessage(
			"START DiscretizationLearner.learn().",
			LogService.STATUS);

	for (int i = 0; i < DiscretizationMethods.KNOWN_METHODS_NAMES.length; i++) {
		if (smetodo.equals(DiscretizationMethods.KNOWN_METHODS_NAMES[i]))
			discMet = i;
	}

	String sMetSimil = getParameterAsString("similarity_method");
	for (int i = 0; i < DiscretizationMethods.KNOWN_SIMILARITY_METHODS.length; i++) {
		if (sMetSimil.equals(DiscretizationMethods.KNOWN_SIMILARITY_METHODS[i]))
			metSimil = i;
	}
	paraSimil = getParameterAsDouble("similarity_parameter");
	NNpoints= getParameterAsInt("neighbour_number");	
	Nor = getParameterAsBoolean("normalization");
	Tip = getParameterAsBoolean("tipification");
	Dif = getParameterAsBoolean("difference");

	List lBlocks=null;
	lBlocks = FindAndCheckBlocks(eSet);

	
	double[][] vcortes=null ;
/*	
	if (discMet == DiscretizationMethods.NONE)
	{
		ExampleTable table = new MemoryExampleTable(lAtt, iSet.getExampleTable().getDataReader());
		eSet = table.createCompleteExampleSet(
				iSet.getAttributes().getLabel(), null, null, iSet.getId());
					}
	else{ */
	
	if (discMet != DiscretizationMethods.NONE){
		//eSet = (ExampleSet) iSet.clone();		
		LinkedList lcortes = null; //stores the set of candidates limits
		// Find the number of Classes
		LinkedList letiquetas = new LinkedList(eSet.getAttributes().getLabel().getValues());
		int numClasses = eSet.getAttributes().getLabel().getValues().size();
		Iterator<Example> reader;
		
		LogService.logMessage(numClasses + " etiquetas", LogService.STATUS );
		

		
		if ((!seriesFixedLength ) && (metSimil==DiscretizationMethods.IntervalKernel)){
			throw new OperatorException("ERROR: OPERATOR DON'T SUPPORT IntervalKernel "+
			"Similarity in Variable Length Series.");
		}
		
		int[][] acumulados;
		int numCortes = 0;
		
		int numAtributos = eSet.getNumberOfAttributes();
		vcortes = new double[numAtributos][];
		
		int currAtri = -1;
		int numBlocks = lBlocks.size();
		for (int currBlock = 0; currBlock < numBlocks; currBlock++) {
			if (((BlockData) lBlocks.get(currBlock)).discretizar) {
				Set scortes = new TreeSet();

				reader=eSet.iterator();
				while (reader.hasNext()) {
					Example example = reader.next();
					if (((BlockData) lBlocks.get(currBlock)).isSeries == true) {
						double[] values;
						values=getBlockValues(lBlocks,currBlock,example);
						for (int i =0; i <values.length; i++)
							scortes.add(new Double(values[i]));
					} else   // is a numerical single Attribute 
						scortes.add(new Double(example.getValue(currBlock)));
				}
				
				lcortes = new LinkedList((Collection) scortes);
				LogService.logMessage(lcortes.size() + " Cortes Candidatos", LogService.MINIMUM);
				numCortes = ((Collection) scortes).size();
				//creo un array para almacenar los acumulados
				acumulados = new int[numClasses][scortes.size()];
				reader=eSet.iterator();
				while (reader.hasNext()) {
					Example example = reader.next();
					if (((BlockData) lBlocks.get(currBlock)).isSeries == true) {
						double[] values;
						values=getBlockValues(lBlocks,currBlock,example);
						for (int i = 0; i < values.length; i++) {
							acumulados[letiquetas.indexOf(example
//									//v3.0 .getLabelAsString())][lcortes
									.getValueAsString(example.getAttribute(ExampleSet.LABEL_NAME)))][lcortes
									                                                                 .indexOf(new Double(values[i]))]++;
						}
					} else {  
						acumulados[letiquetas.indexOf(example
								//v3.0 .getLabelAsString())][lcortes
								.getValueAsString(example.getAttribute(ExampleSet.LABEL_NAME)))][lcortes
								                                                                 .indexOf(new Double(example.getValue(currBlock)))]++;
					}
				}
				numCortes++;			
				vcortes[currBlock] = computeLimits(discMet,acumulados, numCortes, 
						numClasses, lcortes,currBlock);
			} else {
				// may be a Nominal series o Nominal single Atribute
				vcortes[currBlock] = new double[1];
				vcortes[currBlock][0] = 0;
			}
		}
		
		totalInt = 0;
		for (int i = 0; i < lBlocks.size(); i++) {
			totalInt += (vcortes[i].length - 1);
		}
		LogService.logMessage(" Total number of Intervals " + totalInt,
				LogService.STATUS);
//		SimpleModel mod = new DiscretizationModel(eSet, lBlocks, vcortes,
//		NivelLog, metSimil, paraSimil, Nor, Tip, Dif, seriesFixedLength);

	}

	DiscretizationModel mod = new DiscretizationModel(eSet, lBlocks, vcortes,
			discMet,NivelLog, metSimil, paraSimil,NNpoints, Nor, Tip, Dif, seriesFixedLength);


	return mod;
}
	
	
	
	

	//**********************************************************************************************
	//**********************************************************************************************
	public List<ParameterType> getParameterTypes() {
		List<ParameterType>  types = super.getParameterTypes();
		types.add(new ParameterTypeStringCategory("method",
				"Discretization method.", DiscretizationMethods.KNOWN_METHODS_NAMES,
				DiscretizationMethods.KNOWN_METHODS_NAMES[0]));
		types.add(new ParameterTypeInt(
				"number_of_intervals",
				"Number of Discretization intervals. Only for EWI and EFI methods",
				2, Integer.MAX_VALUE, 2));
		
		types.add(new ParameterTypeStringCategory("similarity_method",
				"Similarity method for classification.", DiscretizationMethods.KNOWN_SIMILARITY_METHODS,
				DiscretizationMethods.KNOWN_SIMILARITY_METHODS[0]));
		types.add(new ParameterTypeDouble("similarity_parameter",
				"Parameter for the Similarity method.", Double.MIN_VALUE,
				Double.MAX_VALUE, 0.7));

		types.add(new ParameterTypeInt("neighbour_number",
				"Parameter for identification process.", 1,
				Integer.MAX_VALUE, 1));
		

		types.add(new ParameterTypeBoolean("normalization",
				"Preprocess Series. [0,1] Normalization", false));
		types.add(new ParameterTypeBoolean("tipification",
				"Preprocess Series. Typification.", false));
		types.add(new ParameterTypeBoolean("difference",
				"Preprocess Series. Difference of adjacent values", false));
		types.add(new ParameterTypeBoolean("disable_exampleset_output",
				"Disable the output of a Discretized version of the input ExampleSet.", true));

		return types;
	}

	//**********************************************************************************************
	//**********************************************************************************************
	public Class[] getInputClasses() {
		return new Class[] { ExampleSet.class };
	}

	//**********************************************************************************************
	//**********************************************************************************************
	public Class[] getOutputClasses() {
		return new Class[] { DiscretizationModel.class ,ExampleSet.class };
	}

	//**********************************************************************************************
	//**********************************************************************************************
	private List FindAndCheckBlocks(ExampleSet eSet) throws OperatorException {
		int numBlocks = 0;
		int numSeriesAttributes = 0;
		int numAttributes = 0;
		List lBlocks = new LinkedList();

		int[] SeriesIndex = new int[50];
		seriesFixedLength = true;
		for (int atriact = 0; atriact < eSet.getNumberOfAttributes(); atriact++) {
			BlockData datos = new BlockData();
			
// Problema: El resultWriter no genera comienzos y fin de serie
// si se quiere leer salidas de ExampleWriter no se puede buscar Ontology.VALUE_SERIES_START 				
// 				if (eSet.getAttribute(atriact).getBlockType() == Ontology.VALUE_SERIES_START) {
				if ((eSet.getAttribute(atriact).getBlockType() == Ontology.VALUE_SERIES) ||
				(eSet.getAttribute(atriact).getBlockType() == Ontology.VALUE_SERIES_START)) {
				datos.inicio = atriact;
				datos.fin = eSet.getExampleTable().getBlockEndIndex(atriact);
				datos.isSeries = true;
				LogService.logMessage("-SERIE " + numBlocks + ": de "
						+ datos.inicio + " a " + datos.fin, LogService.MINIMUM);
				atriact = datos.fin;
				numSeriesAttributes += 1;
				if (eSet.getAttribute(datos.inicio).getValueType() != Ontology.NOMINAL)
					datos.discretizar = true;
				else
					datos.discretizar = false;
			} else {
				datos.inicio = atriact;
				datos.fin = atriact;
				datos.isSeries = false;
				if (eSet.getAttribute(atriact).getName().equals("VLS_SIZE")) {
					seriesFixedLength = false;
					datos.discretizar = false;
				} else {
					numAttributes += 1;
					if (eSet.getAttribute(datos.inicio).getValueType() != Ontology.NOMINAL)
						datos.discretizar = true;
					else
						datos.discretizar = false;
				}
			}
			lBlocks.add(datos);
			numBlocks++;
		}

		

		
		LogService.logMessage("Bloques " + numBlocks + ", Normales "
				+ numAttributes + ", Series " + numSeriesAttributes,
				LogService.STATUS);

		// Check input Set attributes
		if ((numAttributes > 0) && (numSeriesAttributes > 0)) {
			throw new OperatorException("ERROR: OPERATOR DON'T SUPPORT MIXED INPUT. Found " + numAttributes
					+ " normal Attributes" + "and " + numSeriesAttributes
					+ " Series Attributes in input Set.");
		}
		else
		{
			//if there are series, the operator only accept 1(ONE) series 
			if (numSeriesAttributes>1){
				throw new OperatorException("ERROR: OPERATOR ACCEPT ONLY 1 (ONE) Series. Found " +
						numSeriesAttributes
						+ " Series in input Set.");
			}
		}

		return lBlocks;
	}

	
	private double[] getBlockValues(List lBlocks,int currBlock, Example example){
		int ultimo = 0;
		int inicio = ((BlockData) lBlocks.get(currBlock)).inicio;
		if (seriesFixedLength == true) {
			ultimo = ((BlockData) lBlocks.get(currBlock)).fin;
		} else {
			ultimo = (int) example.getValue(VLS_SIZE_POS)
					+ ((BlockData) lBlocks.get(currBlock)).inicio- 1;
		}
		double[] val2;
		if (Dif || Nor || Tip) {
			double[] valores = new double[ultimo - inicio + 1];
			for (int i = inicio; i <= ultimo; i++) {
				valores[i - inicio] = example.getValue(i);
			}
			val2 = DiscretizationMethods.Preprocess_Series(valores,
					seriesFixedLength, Nor, Tip, Dif);
		} else {
			val2 = new double[ultimo-inicio+1];
			for (int i = inicio; i <= ultimo; i++) {
				val2[i]=example.getValue(i);
			}
		}
		return val2;
	}
	

private double[] computeLimits(int metodo,int[][] acumulados, int numCortes, 
		int numClasses, LinkedList lcortes, int currBlock){
	

	
	double[] limitesD;
	double[] cortes = new double[numCortes];
	cortes[0] = ((Double) lcortes.getFirst()).doubleValue();
	cortes[numCortes - 1] = ((Double) lcortes.getLast())
			.doubleValue();
	for (int c = 1; c < numCortes - 1; c++) {
		cortes[c] = (((Double) lcortes.get(c)).doubleValue() + 
				((Double) lcortes.get(c - 1)).doubleValue()) / 2;
	}
	
	
	
	//TODO Comprobar si se pueden hacer los cortes, es posible que
	// el número de cortes sea muy pequeño.Contrastar con el número de clases.
	LinkedList D = new LinkedList();
	D.add(new Integer(0));
	D.add(new Integer(numCortes - 1));

	switch (metodo) {
	case DiscretizationMethods.CAIM: //CAIM
	case DiscretizationMethods.Ameva: // Ameva
		DiscretizationMethods.compute_CAIM_Ameva(D, metodo, numClasses, numCortes,
				acumulados);
		break;
	case DiscretizationMethods.CUM:
		DiscretizationMethods.Compute_CUM(D,IntervalosBuscados,numClasses,numCortes,acumulados );
		break;
	case DiscretizationMethods.EFI:
		DiscretizationMethods.Compute_EFI(D, IntervalosBuscados, numClasses, numCortes,
				acumulados);
		break;
	case DiscretizationMethods.EWI:
		D.remove(1);
		D.remove(0);
		D.add(new Double(cortes[0]));
		D.add(new Double(cortes[numCortes - 1]));
		DiscretizationMethods.Compute_EWI(D, IntervalosBuscados);
		break;
	}

	if (metodo != DiscretizationMethods.EWI) {
		Collections.sort(D);
		if (NivelLog <= LogService.INIT) {
			String cad = new String();
			for (int i = 0; i < D.size(); i++) {
				cad = cad + cortes[((Integer) D.get(i)).intValue()]
						+ " : ";
			}
			LogService.logMessage(" Discretization of Block " + currBlock
					+ " -> " + cad, LogService.STATUS);
		}

		limitesD = new double[D.size()];
		for (int i = 0; i < D.size(); i++) {
			limitesD[i] = cortes[((Integer) D.get(i)).intValue()];
		}
	} else { //metodo=EWI
		Collections.sort(D);
		if (NivelLog <= LogService.INIT) {
			String cad = new String();
			for (int i = 0; i < D.size(); i++) {
				cad = cad + ((Double) D.get(i)).doubleValue()
						+ " : ";
			}
			LogService.logMessage(" Discretization of Block  " + currBlock
					+ " -> " + cad, LogService.INIT);
		}
		//Ya tenemos los cortes de este atributo
		limitesD = new double[D.size()];
		for (int i = 0; i < D.size(); i++) {
			limitesD[i] = ((Double) D.get(i)).doubleValue();
		}
	}
	return limitesD;
}

/* (non-Javadoc)
 * @see edu.udo.cs.yale.operator.learner.Learner#supportsCapability(edu.udo.cs.yale.operator.learner.LearnerCapability)
 */
public boolean supportsCapability(LearnerCapability capability) {
	// TODO Auto-generated method stub
	return false;
}




	//CLASS END
}