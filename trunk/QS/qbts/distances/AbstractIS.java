package qbts.distances;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.math.similarity.SimilarityMeasure;

public abstract  class AbstractIS extends SimilarityMeasure {

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		return value1.length-calculateSimilarity(value1, value2);
	}

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		// Comprobar que no son nulos
		if ((value1 == null) || (value2 == null))
			 return Double.NaN;
		
		// comprobar que son de igual tamaño
		if (value1.length != value2.length)
			return Double.NaN;
		
		
		double similitud=0;
		for (int i=0;i<value1.length-1;i++){
			double c1,r1,c2,r2;
			c1=(value1[i]+value1[i+1])/2;
			r1=Math.abs(value1[i]-value1[i+1])/2;
			c2=(value2[i]+value2[i+1])/2;
			r2=Math.abs(value2[i]-value2[i+1])/2;
			
			similitud = similitud + IS(c1,r1,c2,r2);
		}
		return similitud;
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
	}
	
	

	public abstract double IS(double c1, double r1, double c2, double r2);
}
