package qbts.preprocessing.discretization;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import qbts.BlockData;
import qbts.DiscretizationMethods;
import qbts.DiscretizationModel;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.set.ConditionedExampleSet;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.ParameterTypeStringCategory;

import edu.udo.cs.yale.example.Example;
import edu.udo.cs.yale.example.ExampleReader;
import edu.udo.cs.yale.operator.Model;
import edu.udo.cs.yale.operator.parameter.ParameterTypeDouble;
import edu.udo.cs.yale.operator.parameter.ParameterTypeInt;
import edu.udo.cs.yale.tools.LogService;

public class SeriesNormalization extends Operator {

	/** The parameter name for &quot;Determines whether to perform a normalization (minimum 0 and maximum 1) or not to the values of the attributes for each example &quot; */
	public static final String	PARAMETER_NORMALIZATION_SERIES= "normalization"
	/** The parameter name for &quot;Determines whether to perform a z-transformation (mean 0 and variance 1) or not to the values of the attributes for each example&quot; */
	public static final String	PARAMETER_TYPIFICATION_SERIES= "typification"
	/** The parameter name for &quot;Determines whether to perform a difference between the values of consecutive attributes or not; if set the last attribute is remove&quot; */
	public static final String	PARAMETER_DIFFERENCE_SERIES= "normalization"
	
	private static final Class[] INPUT_CLASSES = { ExampleSet.class };

	private static final Class[] OUTPUT_CLASSES = { ExampleSet.class };


	public SeriesNormalization(OperatorDescription description) {
		super(description);
		// TODO Apéndice de constructor generado automáticamente
	}

	@Override
	public IOObject[] apply() throws OperatorException {
		ExampleSet exampleSet = getInput(ExampleSet.class);
		
		for (Example ex : exampleSet ){
if

			if (getParameterAsBoolean(PARAMETER_DIFFERENCE_SERIES)){
				Attribute lastAtt;
				for (Attribute attribute : exampleSet.getAttributes())
					lastAtt = attribute;
				exampleSet.getAttributes().remove(lastAtt);	
			}

		}

		return new IOObject[] { exampleSet };
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
	
	
	

//	**********************************************************************************************
//	**********************************************************************************************
	/**
	 * Learn. Computes the discretization schema. Creates a model calling the
	 * SeriesDiscretizationModel.Constructor .
	 */
/*	public Model learn(ExampleSet eSet) throws OperatorException {

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
		
		if (discMet == DiscretizationMethods.NONE)
		{
			ExampleTable table = new MemoryExampleTable(lAtt, iSet.getExampleTable().getDataReader());
			eSet = table.createCompleteExampleSet(
					iSet.getLabel(), null, null, iSet.getId());
						}
		else{ 
		
		if (discMet != DiscretizationMethods.NONE){
			//eSet = (ExampleSet) iSet.clone();		
			LinkedList lcortes = null; //stores the set of candidates limits
			// Find the number of Classes
			LinkedList letiquetas = new LinkedList(eSet.getLabel().getValues());
			int numClasses = eSet.getLabel().getValues().size();
			ExampleReader reader;
			
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
					reader = eSet.getExampleReader();
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
					reader = eSet.getExampleReader();
					while (reader.hasNext()) {
						Example example = reader.next();
						if (((BlockData) lBlocks.get(currBlock)).isSeries == true) {
							double[] values;
							values=getBlockValues(lBlocks,currBlock,example);
							for (int i = 0; i < values.length; i++) {
								acumulados[letiquetas.indexOf(example
//										//v3.0 .getLabelAsString())][lcortes
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
//			SimpleModel mod = new DiscretizationModel(eSet, lBlocks, vcortes,
//			NivelLog, metSimil, paraSimil, Nor, Tip, Dif, seriesFixedLength);

		}

		DiscretizationModel mod = new DiscretizationModel(eSet, lBlocks, vcortes,
				discMet,NivelLog, metSimil, paraSimil,NNpoints, Nor, Tip, Dif, seriesFixedLength);


		return mod;
	}
		
		
*/		
		

	
	public Class<?>[] getInputClasses() {
		return INPUT_CLASSES;
	}

	public Class<?>[] getOutputClasses() {
		return OUTPUT_CLASSES;
	}

	
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		ParameterType type = new ParameterTypeBoolean(PARAMETER_NORMALIZATION_SERIES,
				"Preprocess Series. [0,1] Normalization", false);
		type.setExpert(false);
		types.add(type);
		type = new ParameterTypeBoolean("tipification","Preprocess Series. Typification.", true));
		type.setExpert(false);
		types.add(type);

		type = new ParameterTypeBoolean("difference","Preprocess Series. Difference of adjacent values", false);
		type.setExpert(false);
		types.add(type);

		type = new ParameterTypeBoolean("disable_exampleset_output",
				"Disable the output of a Discretized version of the input ExampleSet.", true);
		type.setExpert(false);
		types.add(type);
		return types;

	}

}
