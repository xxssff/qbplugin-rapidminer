package qbts.distances;

import java.util.List;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.InputDescription;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.discretization.DiscretizationModel;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeDouble;

public class DumbOp extends Operator {

	public static final String PARAMETER_P1= "PARAMETER_1";
	public static final String PARAMETER_P2= "PARAMETER_2";
	public static final String PARAMETER_P3= "PARAMETER_3";
	public static final String PARAMETER_P4= "PARAMETER_4";
	
	public DumbOp(OperatorDescription description) {
		super(description);
	}

	@Override
	public IOObject[] apply() throws OperatorException {
		DumbResult db=new DumbResult(getParameterAsDouble(PARAMETER_P1),getParameterAsDouble(PARAMETER_P2),
				getParameterAsDouble(PARAMETER_P3),getParameterAsDouble(PARAMETER_P4));
		
		return new IOObject[] { db };
	}

	@Override
	public Class<?>[] getInputClasses() {
		return null;
	}

	@Override
	public Class<?>[] getOutputClasses() {
		return new Class[] { DumbResult.class };
	}
	

	
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		
		types.add(new ParameterTypeDouble(PARAMETER_P1,"Value of parameter1", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.0));
		types.add(new ParameterTypeDouble(PARAMETER_P2,"Value of parameter2", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.0));
		types.add(new ParameterTypeDouble(PARAMETER_P3,"Value of parameter3", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.0));
		types.add(new ParameterTypeDouble(PARAMETER_P4,"Value of parameter4", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,0.0));
		return types;
	}
	
}
