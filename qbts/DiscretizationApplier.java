/*
 * Created on 23-ago-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package qbts;

import java.util.List;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.tools.LogService;

/** <p>
 *  DiscretizationApplier computes the discretization scheme of the input exampleSet 
 *  following the input DiscretizationModel.  
 *  Returns the discretized ExampleSet.
 *  </p>
 *
 *  @author F.J. Cuberos
 *  @version $Id: DiscretizationApplier.java,v 1.0 2004/08/23 Exp $
 */
public class DiscretizationApplier extends Operator{

    /**
	 * @param description
	 */
	public DiscretizationApplier(OperatorDescription description) {
		super(description);
		// TODO Auto-generated constructor stub
	}
	private static final Class[] INPUT_CLASSES = { DiscretizationModel.class, ExampleSet.class };
    private static final Class[] OUTPUT_CLASSES = { ExampleSet.class };

    /** Applies the operator and creates a discretized {@link ExampleSet}. The example set in the input
     *  is consumed, a new ExampleSet is returned. */
    public IOObject[] apply() throws OperatorException {
	ExampleSet inputExampleSet = (ExampleSet)getInput(ExampleSet.class);
	//DiscretizationModel model = (DiscretizationModel)getInput(Model.class);
	DiscretizationModel model =(DiscretizationModel) getInput( DiscretizationModel.class);
	
	LogService.logMessage("Start DiscretizationApplier ", LogService.STATUS);
	

	ExampleSet outputExampleSet= model.discretizeExampleSet(inputExampleSet);

	

	/*if ((boolean) getParameterAsBoolean("DisableModelOutput"))
	    return new IOObject[] { outputExampleSet};
	else */
	return new IOObject[] {model, outputExampleSet };

	//return new IOObject[]{model,outputExampleSet};
    }

	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
	//	types.add(new ParameterTypeBoolean("DisableModelOutput",
	//			"Disable the output of the input Model.", false));
        return types;
	}

    public Class[] getInputClasses() { return INPUT_CLASSES; }
    public Class[] getOutputClasses() { return OUTPUT_CLASSES; }
}