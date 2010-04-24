package qbts.preprocessing.discretization.rm.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import srctest.HelperExampleSet;
import srctest.HelperOperatorConstructor;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;

import deprecated.ByExampleLearner;


public class TestNormalizationExtended extends TestCase {

	/**
	 * Probar que la clonación es correcta, es decir que se crea un nuevo conjunto de datos
	 * completamente independiente del original. 
	 * Creo un conjunto, lo almaceno dentro  un ByExampleModel, lo modifico y compruebo 
	 * que el del ByExampleModel no ha cambiado.
	 */
	
/*	public void testClonacion(){
		ExampleSet eSet = creaEj1();
		ByExampleLearner bEM=new ByExampleLearner(eSet.getAttributes().getLabel(),
				eSet,"dis",1.0,"sel",2.0);
		HelperOperatorConstructor hOp=new HelperOperatorConstructor();

		Operator op=hOp.createOperatorInstance("OPE1", "qbts.preprocessing.discretization.NormalizationExtended", "QBTS");
		Method metodo = hOp.findPrivateMethod("qbts.preprocessing.discretization.NormalizationExtended", "apply");
		
		IOContainer result = new IOContainer();
	    result.append(new IOObject[] { eSet });
	    Object[] args={new IOObject[] { eSet }};
		Object ret= hOp.invokePrivateMethodOperator(op, metodo,  args);

	}
	
	private ExampleSet creaEj1(){
		List<Attribute> lAtt=new ArrayList<Attribute>();		

		int longitus=5;
		HelperExampleSet.addSeries(lAtt, "serie","REAL", longitus, 10);
		HelperExampleSet.addAttribute(lAtt, "eti", "NOMINAL");

		// Debo tener una lista con 6 atributos
		Object[][] valores=new Object[][] {
				{1.0,2.0,3.0,4.0,5.0,"C1"},
				{2.0,3.0,4.0,5.0,6.0,"C1"},
				{.3,.4,.5,.6,.1,"C2"},
				{.1,.1,.1,.1,.1,"C2"}
		};
		
		return  HelperExampleSet.createExampleSet(lAtt, "eti",valores);
	}
	*/
	
}
