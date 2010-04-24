package srctest.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import srctest.HelperExampleSet;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;

public class TestHelperExampleSet extends TestCase {

	

	
	public void setUp(){
		
	}
	// Comprobar que crea una lista de attributos en vacío
	public void testCreaLista(){
		/*		try{
			HelperExampleSet.addAttribute(lAtt, "eti", "NOMINAL");
			assertTrue("NO Controlada la posibilidad de que la lista enviada sea nula",false);
		}
		catch (UnknownError ue){
			assertTrue("Controlada la posibilidad de que la lista enviada sea nula",true);
		}
*/		
		List<Attribute> lAtt= new ArrayList<Attribute>();		
		
		int longitus=5;
		HelperExampleSet.addSeries(lAtt, "serie","REAL", longitus, 10);
		HelperExampleSet.addAttribute(lAtt, "eti", "NOMINAL");
		
		// Debo tener una lista con 6 atributos
		assertEquals("La longitud de la lista no es correcta",lAtt.size(),longitus+1);
		assertEquals("Nombre de Attributo incorrecto",lAtt.get(0).getName(),"serie1");
		assertEquals("Nombre de Attributo incorrecto",lAtt.get(1).getName(),"serie2");
		assertEquals("Nombre de Attributo incorrecto",lAtt.get(4).getName(),"serie5");
		assertEquals("Nombre de Attributo incorrecto",lAtt.get(5).getName(),"eti");
	}


	//tengo una lista con 6 Attributos
	public void testCrearEjemplos(){
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
		
		ExampleSet eSet= HelperExampleSet.createExampleSet(lAtt, "eti",valores);
		
		Iterator<Example> itEx= eSet.iterator();
		int ejemplo=-1;
		while(itEx.hasNext()){
			Example e=itEx.next();
			ejemplo++;
			int i=0;
			for (Attribute currAtt: e.getAttributes()){
				
				assertEquals("Ejemplo " + ejemplo+ " Atributo "+i+" tiene valor incorrecto",e.getValue(currAtt),valores[ejemplo][i]);
				i++;
			}
			 
			
			assertEquals("Ejemplo " + ejemplo+ " tiene clase incorrecta ",e.getLabel(),(double) eSet.getAttributes().getLabel().getMapping().mapString((String)valores[ejemplo][5]));
		}
		
		
		
	}
	
}
