package qbts.distances;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.PreprocessingModel;
import com.rapidminer.operator.similarity.attributebased.AbstractRealValueBasedSimilarity;
import com.rapidminer.operator.similarity.attributebased.AbstractValueBasedSimilarity;

public abstract class AbstractExtendedRealValueBasedSimilarity extends
		AbstractValueBasedSimilarity {

	private PreprocessingModel model;
////SI NO HACE NADA SE ¿PUEDE QUITAR EL METODO COMPLETO? SE SUPONE QUE SE LLAMARA AL PADRE.
    public void init(ExampleSet exampleSet) throws OperatorException {
         // Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
            super.init(exampleSet);
    }

}
