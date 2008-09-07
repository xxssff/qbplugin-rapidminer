package deprecated;

import java.util.List;

import yale.operator.preprocessing.discretization.DiscretizationModel;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.learner.AbstractLearner;
import com.rapidminer.operator.learner.LearnerCapability;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeString;

public class ByExampleLearner extends AbstractLearner {
	
	/** Empty Constructor */
	 public ByExampleLearner(OperatorDescription description) {
		super(description);
	}
	
	 
	public Model learn(ExampleSet eSet) throws OperatorException{
		return  new ByExampleModel(
				eSet.getAttributes().getLabel(),eSet, 
				getParameterAsString("similarity_method_class"),
				getParameterAsDouble("similarity_method_parameter"),
				getParameterAsString("selection_method_class"),
				getParameterAsDouble("selection_method_parameter")
				);
		
	}
	
	public boolean supportsCapability(LearnerCapability capability){
		//TODO: Check the existing capabilities.
		return false;	
	}
	
	public IOObject[] apply() throws OperatorException {

		ExampleSet eSet = (ExampleSet) getInput(ExampleSet.class);
		ByExampleModel byModel = (ByExampleModel) learn(eSet);
		
		if ((boolean) getParameterAsBoolean("disable_exampleset_output"))
		    return new IOObject[] {byModel};
		else
			return new IOObject[] {byModel, eSet };
	}
	
	
	
	//**********************************************************************************************
	//**********************************************************************************************
	public List<ParameterType> getParameterTypes() {
		List<ParameterType>  types = super.getParameterTypes();
		
		types.add(new ParameterTypeBoolean("store_preprocesing_model",
				"Read and stor a preprocessing model.", false));
		
		types.add(new ParameterTypeString("similarity_method_class",
				"Similarity method for classification.", false));
		types.add(new ParameterTypeDouble("similarity_method_parameter",
				"Parameter for the Similarity method.", Double.MIN_VALUE,
				Double.MAX_VALUE, 1.0));

		types.add(new ParameterTypeString("selection_method_class",
				"Selection method of better label.", false));
		types.add(new ParameterTypeDouble("selection_method_parameter",
				"Parameter for the selection method.", Double.MIN_VALUE,
				Double.MAX_VALUE, 1.0));

		
		types.add(new ParameterTypeBoolean("disable_exampleset_output",
				"Disable the output of the input ExampleSet.", true));

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
		if (getParameterAsBoolean("disable_exampleset_output"))
			return new Class[] { ByExampleModel.class };
		else
			return new Class[] { ByExampleModel.class  ,ExampleSet.class };
	}

	
}
