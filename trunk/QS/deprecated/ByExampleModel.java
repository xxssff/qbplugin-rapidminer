package deprecated;

import java.util.ArrayList;

import deprecated.yale.DiscretizationModel;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.learner.SimplePredictionModel;
import com.rapidminer.tools.att.AttributeSet;

public class ByExampleModel extends SimplePredictionModel {

	ExampleSet lSet;
	// DiscretizationModel discMod;
	String distClazz;
	Double distParam;
	String selectClazz;
	Double selectParam;

	ArrayList learningVector;  //store the matrix version of the learning Set
	// improve performance in prediction.
	
	public ByExampleModel(Attribute label, ExampleSet learnSet,// DiscretizationModel discModel,
			String dClazz, double dParam, String sClazz, double sParam){
		super(learnSet);
		ExampleTable nTable = MemoryExampleTable.createCompleteCopy(learnSet.getExampleTable());
		AttributeSet sAtt = new AttributeSet();
		for (Attribute attribute : learnSet.getAttributes()) {
			sAtt.addAttribute(attribute);
		}
		this.lSet = nTable.createExampleSet(sAtt);
				
		// this.discMod = discModel;
		this.distClazz = dClazz;
		this.distParam = dParam;
		this.selectClazz = sClazz;
		this.selectParam= sParam;
		this.learningVector=null;
	}
	

	public double predict(Example testSample)  {
		// Compute the distance to every example in lSet (learning Set)
			// if learningVector=null 
				// Create the learning vector structure 
			// Instantiate by reflection the distance class
		    // Convert the testSample to a matrix
			// Apply the distance to testSample and every element in learningVector
			// Generate a list of distance values
		// Order the distances
		
		// Select the label
		   // Instantiate by reflection the selection class (as K-nn)
		   // Set the parameter
		
		return 1.0;
	}

}
