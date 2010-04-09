package qbts.preprocessing.discretization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.PreprocessingOperator;
import com.rapidminer.operator.preprocessing.discretization.DiscretizationModel;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.tools.Ontology;

abstract public class Discretizer extends PreprocessingOperator {
	

	/** Indicates if long range names should be used. */
	public static final String PARAMETER_RANGE_NAME_TYPE = "range_name_type";


	public Discretizer(OperatorDescription description) {
		super(description);
	}

	public Model createPreprocessingModel(ExampleSet exampleSet)
	throws OperatorException {
		DiscretizationModel model = new DiscretizationModel(exampleSet);
		List<Attribute> lAtt=new ArrayList<Attribute>();

		exampleSet.recalculateAllAttributeStatistics();
		HashMap<Attribute, double[]> ranges = new HashMap<Attribute, double[]>();

		for (Attribute attribute : exampleSet.getAttributes()) {
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
						computeValues(exampleSet,lAtt,ranges);
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
						computeValues(exampleSet,lAtt,ranges);
						lAtt.clear();
					}
					break;
				default:	
					throw new OperatorException("VALUE_MATRIX attributes not supported.");
				}	
			}
		}
		if (!lAtt.isEmpty())
			throw new OperatorException("Values Series without value_series_end attribute.");
		model.setRanges(ranges, "range", getParameterAsInt(PARAMETER_RANGE_NAME_TYPE));
		return (model);
	}

	abstract public void computeValues(ExampleSet eSet, List<Attribute> lA, HashMap<Attribute, double[]> ranges ) throws OperatorException;
	

	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		
		types.add(new ParameterTypeCategory(PARAMETER_RANGE_NAME_TYPE, "Indicates if long range names including the limits should be used.", DiscretizationModel.RANGE_NAME_TYPES, DiscretizationModel.RANGE_NAME_LONG));
		return types;
	}
	
	
}
