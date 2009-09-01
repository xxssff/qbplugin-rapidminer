package qbts.distances;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.MissingIOObjectException;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.preprocessing.discretization.DiscretizationModel;
import com.rapidminer.parameter.ParameterHandler;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.container.Tupel;
import com.rapidminer.tools.math.similarity.SimilarityMeasure;


public class ISone extends AbstractIS {  // SimilarityMeasure {

	
	private double cp = 1;  // punto de cambio que limita las dos funciones cuando d=cp
	
	private double MaxValueV1 = 1;
	private double MinValueV1 = 0;
	private double fx=0.0; 
	
	
	public void init(ExampleSet exampleSet, ParameterHandler parameterHandler, IOContainer ioContainer) throws OperatorException {

		LogService log = ((Operator) parameterHandler).getLog(); //Local log of calling operator
		
		DumbResult dr=null;
		try {
			dr = (DumbResult) ioContainer.remove(DumbResult.class);
		} catch (MissingIOObjectException e1) {
			log.log("ISOne has not found DumbResult parameters. Using default values factor=0.5", LogService.MAXIMUM);
			fx = 0.5;
			return;
		}
		
		fx = dr.getP1();
		log.log("ISOne has found DumbResult parameters. Using values factor=" + (new Double(fx)).toString(), LogService.MAXIMUM);
	}
	
	public double IS(double c1, double r1, double c2, double r2){
		double d, R;
		
		R=Math.min(r1,r2)/Math.max(r1,r2);
		if (Double.isNaN(R)){
			return 0;
		}
		
		d= Math.abs(c1-c2)/(r1+r2);
		
		double v1= (MaxValueV1 - MinValueV1) * 0.5 * ( Math.cos(Math.PI * (1-R))+1) + MinValueV1;
		
		if (d==0){
			return v1;
		}
		else if (d < cp) {
			return (v1-fx*v1) * Math.cos(d / cp * Math.PI / 2) + fx*v1;
		}else{
			return (fx*v1*cp)/d ; // == fx/(d/cp);
		}
	}
	
}
