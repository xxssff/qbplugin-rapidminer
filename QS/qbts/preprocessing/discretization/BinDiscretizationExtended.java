package qbts.preprocessing.discretization;

import java.util.HashMap;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.Statistics;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeInt;

public class BinDiscretizationExtended extends Discretizer {

	/** Indicates the number of used bins. */
	public static final String PARAMETER_NUMBER_OF_BINS = "number_of_bins";

	
	public BinDiscretizationExtended(OperatorDescription description) {
		super(description);
	}

	protected void computeValues(ExampleSet eSet, List<Attribute> lA, HashMap<Attribute, double[]> ranges ) throws OperatorException{
		int numberOfBins = getParameterAsInt(PARAMETER_NUMBER_OF_BINS);
		double[] binRange = new double[numberOfBins];
		double min=Double.POSITIVE_INFINITY;
		double max=Double.NEGATIVE_INFINITY;
		for (Attribute attribute : lA) {
			double mi = eSet.getStatistics(attribute, Statistics.MINIMUM);
			double ma = eSet.getStatistics(attribute, Statistics.MAXIMUM);
			if (mi<min) min=mi;
			if (ma>max) max=ma;
		}
		for (int b = 0; b < numberOfBins - 1; b++) {
			binRange[b] = min + (((double) (b + 1) / (double) numberOfBins) * (max - min));
		}
		binRange[numberOfBins - 1] = Double.POSITIVE_INFINITY;
		for (Attribute attribute : lA) {
			ranges.put(attribute, binRange);
		}
	}	
	
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		
		ParameterType type = new ParameterTypeInt(PARAMETER_NUMBER_OF_BINS, "Defines the number of bins which should be used for each attribute.", 2, Integer.MAX_VALUE, 2);
		type.setExpert(false);
		types.add(type);
		return types;
	}

}
