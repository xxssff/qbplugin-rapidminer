package qbts.preprocessing.discretization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.PreprocessingOperator;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;

public abstract class AbstractCAIMAmevaDiscretizer extends PreprocessingOperator {
	/** Indicates if long range names should be used. */
	public static final String PARAMETER_USE_LONG_RANGE_NAMES = "use_long_range_names";
	/** Indicates if attributes must be grouped for discretization. */
	public static final String PARAMETER_ALL_ATTRIBUTES = "discretize_all_together";
//	 FJ Begin
	public static final String PARAMETER_INCLUDE_LIMITS = "include_extrem_limits";
	// FJ End
	
	protected String method ="";
	
	public AbstractCAIMAmevaDiscretizer(OperatorDescription description) {
		super(description);
	}
	
	
	public Model createPreprocessingModel(ExampleSet exampleSet)
	throws OperatorException {

		Attribute labelAtt=exampleSet.getAttributes().getLabel();
		if (!labelAtt.isNominal()){
			throw new UnsupportedOperationException("The "+ method + " discretization method need a nominal label attribute!");			
		}
		int numClasses = labelAtt.getMapping().getValues().size();

		exampleSet.recalculateAllAttributeStatistics();
		HashMap<Attribute, double[]> ranges = new HashMap<Attribute, double[]>();

		List<CumDiscretizerBlock> lVal = new ArrayList<CumDiscretizerBlock>();
		for (Attribute attribute : exampleSet.getAttributes()) {
			if (attribute.isNumerical()) { // skip nominal and date attributes
				for (Example example : exampleSet) {
						double value = example.getValue(attribute);
						if (!Double.isNaN(value)) {
							lVal.add(new CumDiscretizerBlock(example.getValue(attribute),
									(int) example.getLabel(),numClasses));
					}
				}
				if (!getParameterAsBoolean(PARAMETER_ALL_ATTRIBUTES)){
					Collections.sort(lVal);  //sort the values
					//compute the acumulators
					CumDiscretizerBlock.computeAcums(lVal);
					//delete duplicated values updating the occurences and acumulators
					CumDiscretizerBlock.joinValues(lVal);

					//Apply CAIM method
					List<Integer> cortes=compute_Values( lVal);
					
					double[] attributeRanges = new double[cortes.size()-1];
					for (int i=1;i<cortes.size()-1;i++){
						attributeRanges[i-1]=lVal.get(cortes.get(i)).getValue();
						}
					attributeRanges[cortes.size() - 2] = Double.POSITIVE_INFINITY;
					ranges.put(attribute, attributeRanges);

					lVal = new ArrayList<CumDiscretizerBlock>();
				}
			}
		}

		if (getParameterAsBoolean(PARAMETER_ALL_ATTRIBUTES)){
			Collections.sort(lVal);  //sort the values
			//compute the acumulators
			CumDiscretizerBlock.computeAcums(lVal);
			//delete duplicated values updating the occurences and acumulators
			CumDiscretizerBlock.joinValues(lVal);

			//Apply CAIM method
			List<Integer> cortes=compute_Values( lVal);
			
			double[] attributeRanges = new double[cortes.size()-1];
			for (int i=1;i<cortes.size()-1;i++){
				attributeRanges[i-1]=lVal.get(cortes.get(i)).getValue();
				}
			attributeRanges[cortes.size() - 2] = Double.POSITIVE_INFINITY;
			
			for (Attribute currentAttribute : exampleSet.getAttributes()) {
				ranges.put(currentAttribute, attributeRanges);
			}
		}

		
		DiscretizationModelSeries model = new DiscretizationModelSeries(exampleSet);
		model.setRanges(ranges, "range", getParameterAsBoolean(PARAMETER_USE_LONG_RANGE_NAMES));
		
		if (getParameterAsBoolean(PARAMETER_INCLUDE_LIMITS)){
			model.setLimitsIncluded(true);
			/*			CASO GENERAL
			 * 				double[][] values = new double[exampleSet.getAttributes().size()][2];
							int index = 0;
							for (Attribute attribute : exampleSet.getAttributes()){
								values[index][0] = valores.get(0);
								values[index++][1] = valores.get(valores.size()-1);
							}*/

			double[][] values = new double[1][2];
			values[0][0] = lVal.get(0).getValue();
			values[0][1] = lVal.get(lVal.size()-1).getValue();
			model.setExtremLimits(values);
		}

		
		return model;
	} 

	protected abstract List<Integer> compute_Values( List<CumDiscretizerBlock> lCumB );
	
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		ParameterType type = new ParameterTypeBoolean(PARAMETER_ALL_ATTRIBUTES , "Indicates if ALL the attributes are discretized together.", false);
		type.setExpert(false);
		types.add(type);
		types.add(new ParameterTypeBoolean(PARAMETER_USE_LONG_RANGE_NAMES, "Indicates if long range names including the limits should be used.", true));
		types.add(new ParameterTypeBoolean(PARAMETER_INCLUDE_LIMITS, "Indicates if extrem limits must be included in the model.", false));
		return types;
	}

}
