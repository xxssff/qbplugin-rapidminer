package qbts.distances;

import java.util.List;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.AbstractModel;
import com.rapidminer.operator.GroupedModel;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.similarity.SimilarityMeasure;
import com.rapidminer.operator.similarity.SimilarityUtil;
import com.rapidminer.operator.similarity.attributebased.ExampleBasedSimilarityMeasure;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeList;
import com.rapidminer.parameter.ParameterTypeStringCategory;
import com.rapidminer.tools.ClassNameMapper;

public class QBSimilaritySetup extends Operator {

	/** The parameter name for &quot;similarity measure to apply&quot; */
	public static final String PARAMETER_MEASURE = "measure";
	private static final String[] DEFAULT_SIM_MEASURES = {
		    "qbts.distances.QSISimilarity",
			"com.rapidminer.operator.similarity.attributebased.NominalDistance"
	};

	private static ClassNameMapper SIM_CLASSES_MAP = new ClassNameMapper(DEFAULT_SIM_MEASURES);


	
	public QBSimilaritySetup(OperatorDescription description) {
		super(description);
	}

	public IOObject[] apply() throws OperatorException {
		// TODO Cuando sea Kernel tiene que tomar el DiscretizationModel, crear una Similitud y configurarle el Modelo
		// ContainerModel  model = (ContainerModel) getInput(AbstractModel.class); 
		ExampleSet es = getInput(ExampleSet.class);
		
		String simClassName = (String) this.getParameter(PARAMETER_MEASURE);
		ExampleBasedSimilarityMeasure sim = (ExampleBasedSimilarityMeasure) SIM_CLASSES_MAP.getInstantiation(simClassName);
		sim.init(es);

		
		return new IOObject[] {es, sim };
		// TODO Cuando sea Kernel habrá que pasarle el Container para que lo procese.
		// simil.setModel(model);
		//return new IOObject[] {es, model, sim };

	}
	
// TODO: Incluir la lista de parámetros que serán el nombre de la similitud a crear. 
	// Se puede seguir el mecanismo usado en SimilarityUtil
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		ParameterType result = new ParameterTypeStringCategory(PARAMETER_MEASURE, "similarity measure to apply", SIM_CLASSES_MAP.getShortClassNames(),
				SIM_CLASSES_MAP.getShortClassNames()[0]);
		result.setExpert(false);
		types.add(result);
		return types;
	}

	
	public Class[] getInputClasses() {
		return new Class[] {ExampleSet.class };
	}

	public Class[] getOutputClasses() {
		// CUANDO SEA KERNEL INTERVALAR YA VEREMOS COMO SE PONE
		// return new Class[] {ExampleSet.class, ContainerModel.class , SimilarityMeasure.class };
		return new Class[] {ExampleSet.class,  SimilarityMeasure.class };
	}


}
