package qbts.distances;

import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.math.similarity.DistanceMeasures;

public class QBSimilaritySetup extends Operator {

	/** The parameter name for &quot;similarity measure to apply&quot; */
//	public static final String PARAMETER_MEASURE = "measure";

	//private static ClassNameMapper SIM_CLASSES_MAP = new ClassNameMapper(DEFAULT_SIM_MEASURES);
	

	
	public QBSimilaritySetup(OperatorDescription description) {
		super(description);
	}
	
	
	public IOObject[] apply() throws OperatorException {
		// TODO Cuando sea Kernel tiene que tomar el DiscretizationModel, crear una Similitud y configurarle el Modelo
		// ContainerModel  model = (ContainerModel) getInput(AbstractModel.class); 
		//ExampleSet es = getInput(ExampleSet.class);

		
	
		DistanceMeasures.registerMeasure(DistanceMeasures.NOMINAL_MEASURES_TYPE, "QSI_Similarity", QSISimilarity.class);

/*		String simClassName = (String) this.getParameter(PARAMETER_MEASURE);
		ExampleBasedSimilarityMeasure sim = (ExampleBasedSimilarityMeasure) SIM_CLASSES_MAP.getInstantiation(simClassName);

		if (sim instanceof AbstractDiscretizedRealValueBasedSimilarity)
			((AbstractDiscretizedRealValueBasedSimilarity) sim).init(es,(DiscretizationModelSeries)getInput(Model.class));
		else
			sim.init(es);
		*/
		return new IOObject[] {};
	}
	
/*	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		ParameterType result = new ParameterTypeStringCategory(PARAMETER_MEASURE, "similarity measure to apply", SIM_CLASSES_MAP.getShortClassNames(),
				SIM_CLASSES_MAP.getShortClassNames()[0]);
		result.setExpert(false);
		types.add(result);
		return types;
	}

	public InputDescription getInputDescription (Class cls ) {
		if ((Model.class.isAssignableFrom( cls )) || ((ExampleSet.class.isAssignableFrom( cls )))) {
//			consume default: true, create parameter: true
			return new InputDescription( cls , true , true );
		} else {
			return super. getInputDescription ( cls );
		}
	}
*/
	public Class[] getInputClasses() {
		return new Class[] {};
	}

	public Class[] getOutputClasses() {
		return new Class[] { };
	}


}
