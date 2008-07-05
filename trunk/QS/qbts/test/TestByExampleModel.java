package qbts.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import qbts.ByExampleModel;
import srctest.HelperExampleSet;
import srctest.HelperOperatorConstructor;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;

public class TestByExampleModel extends TestCase {

	/**
	 * Probar que la clonación es correcta, es decir que se crea un nuevo conjunto de datos
	 * completamente independiente del original. 
	 * Creo un conjunto, lo almaceno dentro  un ByExampleModel, lo modifico y compruebo 
	 * que el del ByExampleModel no ha cambiado.
	 */
	
	public void testClonacion(){
		ExampleSet eSet = creaEj1();
		ByExampleModel bEM=new ByExampleModel(eSet.getAttributes().getLabel(),
				//eSet,null,"dis",1.0,"sel",2.0)
				eSet,"dis",1.0,"sel",2.0);
		HelperOperatorConstructor hOp=new HelperOperatorConstructor();
		ExampleSet cSet = (ExampleSet) hOp.getPrivateFieldOperator("lSet", "qbts.ByExampleModel", bEM);
		
		
		assertEquals(eSet.getExample(0).getValue(eSet.getAttributes().get("serie1")), 
				cSet.getExample(0).getValue(eSet.getAttributes().get("serie1")));
		
		eSet.getExample(0).setValue(eSet.getAttributes().get("serie1"), 200);
		
		
		double ori=eSet.getExample(0).getValue(eSet.getAttributes().get("serie1"));
		double nue= cSet.getExample(0).getValue(eSet.getAttributes().get("serie1"));
		assertTrue("NO Son iguales ",ori!=nue);
		
		
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
	
	
}
