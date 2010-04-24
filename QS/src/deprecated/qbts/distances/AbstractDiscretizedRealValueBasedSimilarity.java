package deprecated.qbts.distances;

import qbts.preprocessing.discretization.DiscretizationModelSeries;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.similarity.attributebased.AbstractRealValueBasedSimilarity;

public abstract class AbstractDiscretizedRealValueBasedSimilarity extends
		AbstractRealValueBasedSimilarity {


	
	
	public abstract void init(ExampleSet es, DiscretizationModelSeries dm) throws OperatorException;

}
