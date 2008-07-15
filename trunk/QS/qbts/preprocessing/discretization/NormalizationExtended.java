package qbts.preprocessing.discretization;

import java.util.ArrayList;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;

public class NormalizationExtended extends Operator {

	/** The parameter name for &quot;Determines whether to perform a normalization (minimum 0 and maximum 1) or not to the values of the attributes for each example &quot; */
	public static final String	PARAMETER_NORMALIZATION_SERIES= "normalization";
	/** The parameter name for &quot;Determines whether to perform a z-transformation (mean 0 and variance 1) or not to the values of the attributes for each example&quot; */
	public static final String	PARAMETER_TYPIFICATION_SERIES= "typification";
	/** The parameter name for &quot;Determines whether to perform a difference between the values of consecutive attributes or not; if set the last attribute is remove&quot; */
	public static final String	PARAMETER_DIFFERENCE_SERIES= "normalization";
	
	private static final Class[] INPUT_CLASSES = { ExampleSet.class };

	private static final Class[] OUTPUT_CLASSES = { ExampleSet.class };


	public NormalizationExtended(OperatorDescription description) {
		super(description);
		// TODO Apéndice de constructor generado automáticamente
	}

	public IOObject[] apply() throws OperatorException {
		ExampleSet exampleSet = getInput(ExampleSet.class);

		List<Attribute> lAtt=new ArrayList<Attribute>();
		for (Attribute att: exampleSet.getAttributes()){
			lAtt.add(att);
		}
		if (getParameterAsBoolean(PARAMETER_TYPIFICATION_SERIES) || 
				getParameterAsBoolean(PARAMETER_NORMALIZATION_SERIES)||
				getParameterAsBoolean(PARAMETER_DIFFERENCE_SERIES)){
			for (Example ex : exampleSet ){

				double[] valores = new double[ex.getAttributes().size()];
				int i=0;
				for (Attribute att: ex.getAttributes()){
					valores[i++]=ex.getValue(att);
				}
				double[] val2 = Preprocess_Series(valores,true,
						getParameterAsBoolean(PARAMETER_NORMALIZATION_SERIES),
						getParameterAsBoolean(PARAMETER_TYPIFICATION_SERIES), 
						getParameterAsBoolean(PARAMETER_DIFFERENCE_SERIES));
				i=0;
				for (Attribute att: lAtt){
					if (i<(val2.length-1))
						ex.setValue(att, val2[i++]);
				}
			}
			if (getParameterAsBoolean(PARAMETER_DIFFERENCE_SERIES)){
				Attribute lastAtt=null;
				for (Attribute attribute : exampleSet.getAttributes())
					lastAtt = attribute;
				exampleSet.getAttributes().remove(lastAtt);	
			}
		}

		return new IOObject[] { exampleSet };
	}




	public static final double[] Preprocess_Series(double[] v,
			boolean seriesFixedLength, boolean Nor, boolean Tip, boolean Dif) {

		int longitud = v.length;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double total = 0;

		for (int pos = 0; pos < longitud; pos++) {
			if (v[pos] < min)
				min = v[pos];
			if (v[pos] > max)
				max = v[pos];
			total += v[pos];
		}
	
		double amplitud = max - min;
		double media = total / longitud;
		double suma = 0;
		if (Nor) { // Normalization
			for (int i = 0; i < longitud; i++)
				v[i] = (v[i] - min) / amplitud;
		} else if (Tip) {  // Tipification
			for (int i = 0; i < longitud; i++)
				suma += Math.pow(v[i] - media, 2);
			if (suma > 0) {
				double desv = Math.sqrt(suma / (longitud - 1));
				for (int i = 0; i < longitud; i++)
					v[i] = (v[i] - media) / desv;
			}
		}
	
		if (Dif)
			longitud--;
		double[] aux = new double[longitud];
	
		if (Dif)
			for (int i = 0; i < longitud; i++)
				aux[i] = v[i + 1] - v[i];
		else
			for (int i = 0; i < longitud; i++)
				aux[i] = v[i];
	
		return aux;
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
		type = new ParameterTypeBoolean("tipification","Preprocess Series. Typification.", true);
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
