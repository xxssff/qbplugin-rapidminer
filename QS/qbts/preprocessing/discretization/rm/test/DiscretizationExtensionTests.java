package qbts.preprocessing.discretization.rm.test;

import java.io.File;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import srctest.HelperOperatorConstructor;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.MissingIOObjectException;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.performance.PerformanceVector;
import com.rapidminer.operator.preprocessing.discretization.DiscretizationModel;
import com.rapidminer.test.ExampleSetDataSampleTest;
import com.rapidminer.test.OperatorDataSampleTest;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.Tupel;

public class DiscretizationExtensionTests extends TestCase {


	public DiscretizationExtensionTests(String arg0) {
		super(arg0);
	}




	public IOContainer runSampleTest(String file) throws Exception {
		String caminoact = System.getProperty("user.dir");
		File processFile = new File(caminoact, "test" + File.separator + file);
		//File processFile = new File(ParameterService.getRapidMinerHome(), "srctest" + File.separator + file);
		
		if (!processFile.exists())
			throw new Exception("File '" + processFile.getAbsolutePath() + "' does not exist!");
		LogService.getGlobal().setVerbosityLevel(LogService.OFF);
		Process process = RapidMiner.readProcessFile(processFile);
		IOContainer output = process.run(new IOContainer(), LogService.OFF);
		return output;
	}
	
	public void testCAIMDiscretization(){
		// quiero comprobar que la discretización es la misma usando
		// 1 attributo con 100 ejemplos con el operador estándard RM
		// 5 atributos en 20 ejemplos con el nuevo operador activando el procesar_todos_los attributos juntos
		// Es correcto si  uno de ellos tiene sólo un rango y es igual a todos los del otro
		RapidMiner.init();
		
		IOContainer sal1;
		try{
		  sal1 = runSampleTest("Discretization"+File.separator+"CAIM1.xml");
		//Obtengo los dos modelos obtenidos
		  DiscretizationModel model1 = (DiscretizationModel) sal1.remove(Model.class);
		  DiscretizationModel model2 = (DiscretizationModel) sal1.remove(Model.class);
		  // tengo que obtener los rangesmap que son privados
		  HelperOperatorConstructor hOp=new HelperOperatorConstructor();
		  HashMap<String, SortedSet<Tupel<Double, String>>> rango1 = 
			  (HashMap<String, SortedSet<Tupel<Double, String>>>) 
			  hOp.getPrivateFieldOperator("rangesMap","com.rapidminer.operator.preprocessing.discretization.DiscretizationModel",model1);
		  HashMap<String, SortedSet<Tupel<Double, String>>> rango2 = 
			  (HashMap<String, SortedSet<Tupel<Double, String>>>) 
			  hOp.getPrivateFieldOperator("rangesMap","com.rapidminer.operator.preprocessing.discretization.DiscretizationModel",model2);
		  
		  String[] k1 = (String[])rango1.keySet().toArray(new String[rango1.size()]);
		  String[] k2 = (String[])rango2.keySet().toArray(new String[rango2.size()]);
		  
		  // Compruebo cual tiene sólo un elemento y ese lo comparo con todos los del otro 
		  // tienen que ser iguales
		  if (k1.length==1){
			  assertEquals(k2.length,5);
			  for (int i =0; i< k2.length; i++)
			      assertTrue(rango2.get(k1[0]).equals(rango1.get(k2[i])));
		  }
		 else{ 
			  assertEquals(k2.length,1);
			  assertEquals(k1.length,5);
			  for (int i =0; i< k1.length; i++)
			      assertTrue(rango2.get(k2[0]).equals(rango1.get(k1[i])));
		  }
		}
		catch (Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
	}


	
	public void testBinDiscretization(){
		// quiero comprobar que la discretización es la misma usando
		// 1 attributo con 100 ejemplos con el operador estándard RM
		// 5 atributos en 20 ejemplos con el nuevo operador activando el procesar_todos_los attributos juntos
		// Es correcto si  uno de ellos tiene sólo un rango y es igual a todos los del otro

		RapidMiner.init();
		IOContainer sal1;
		try{
		  sal1 = runSampleTest("Discretization"+File.separator+"bin1.xml");
		//Obtengo los dos modelos obtenidos
		  DiscretizationModel model1 = (DiscretizationModel) sal1.remove(Model.class);
		  DiscretizationModel model2 = (DiscretizationModel) sal1.remove(Model.class);
		  // tengo que obtener los rangesmap que son privados
		  HelperOperatorConstructor hOp=new HelperOperatorConstructor();
		  HashMap<String, SortedSet<Tupel<Double, String>>> rango1 = 
			  (HashMap<String, SortedSet<Tupel<Double, String>>>) 
			  hOp.getPrivateFieldOperator("rangesMap","com.rapidminer.operator.preprocessing.discretization.DiscretizationModel",model1);
		  HashMap<String, SortedSet<Tupel<Double, String>>> rango2 = 
			  (HashMap<String, SortedSet<Tupel<Double, String>>>) 
			  hOp.getPrivateFieldOperator("rangesMap","com.rapidminer.operator.preprocessing.discretization.DiscretizationModel",model2);
		  
		  String[] k1 = (String[])rango1.keySet().toArray(new String[rango1.size()]);
		  String[] k2 = (String[])rango2.keySet().toArray(new String[rango2.size()]);
		  
		  // Compruebo cual tiene sólo un elemento y ese lo comparo con todos los del otro 
		  // tienen que ser iguales
		  if (k1.length==1){
			  assertEquals(k2.length,5);
			  for (int i =0; i< k2.length; i++)
			      assertTrue(rango2.get(k1[0]).equals(rango1.get(k2[i])));
		  }
		 else{ 
			  assertEquals(k2.length,1);
			  assertEquals(k1.length,5);
			  for (int i =0; i< k1.length; i++)
			      assertTrue(rango2.get(k2[0]).equals(rango1.get(k1[i])));
		  }
		}
		catch (Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	public void testFreqDiscretization(){
		// quiero comprobar que la discretización es la misma usando
		// 1 attributo con 100 ejemplos con el operador estándard RM
		// 5 atributos en 20 ejemplos con el nuevo operador activando el procesar_todos_los attributos juntos
		// Es correcto si  uno de ellos tiene sólo un rango y es igual a todos los del otro

		RapidMiner.init();
		IOContainer sal1;
		try{
		  sal1 = runSampleTest("Discretization"+File.separator+"freq1.xml");
		//Obtengo los dos modelos obtenidos
		  DiscretizationModel model1 = (DiscretizationModel) sal1.remove(Model.class);
		  DiscretizationModel model2 = (DiscretizationModel) sal1.remove(Model.class);
		  // tengo que obtener los rangesmap que son privados
		  HelperOperatorConstructor hOp=new HelperOperatorConstructor();
		  HashMap<String, SortedSet<Tupel<Double, String>>> rango1 = 
			  (HashMap<String, SortedSet<Tupel<Double, String>>>) 
			  hOp.getPrivateFieldOperator("rangesMap","com.rapidminer.operator.preprocessing.discretization.DiscretizationModel",model1);
		  HashMap<String, SortedSet<Tupel<Double, String>>> rango2 = 
			  (HashMap<String, SortedSet<Tupel<Double, String>>>) 
			  hOp.getPrivateFieldOperator("rangesMap","com.rapidminer.operator.preprocessing.discretization.DiscretizationModel",model2);
		  
		  String[] k1 = (String[])rango1.keySet().toArray(new String[rango1.size()]);
		  String[] k2 = (String[])rango2.keySet().toArray(new String[rango2.size()]);
		  
		  // Compruebo cual tiene sólo un elemento y ese lo comparo con todos los del otro 
		  // tienen que ser iguales
		  if (k1.length==1){
			  assertEquals(k2.length,5);
			  for (int i =0; i< k2.length; i++)
			      assertTrue(rango2.get(k1[0]).equals(rango1.get(k2[i])));
		  }
		 else{ 
			  assertEquals(k2.length,1);
			  assertEquals(k1.length,5);
			  for (int i =0; i< k1.length; i++)
			      assertTrue(rango2.get(k2[0]).equals(rango1.get(k1[i])));
		  }
		}
		catch (Exception e){
			e.printStackTrace();
			assertFalse(true);
		}
	}

	
	public static void oneTimeSetUp(){
		RapidMiner.init();		
	}
	
	

/*	NO FUNCIONA

	public static Test suite() {
		TestSuite suite = new TestSuite();
	
		suite.addTest(new DiscretizationExtensionTests("testCAIMDiscretization"));
		suite.addTest(new DiscretizationExtensionTests(	"testBinDiscretization"));
		suite.addTest(new DiscretizationExtensionTests(	"testFreqDiscretization"));
		TestSetup wrapper = new TestSetup(suite) {
			public void setUp() { oneTimeSetUp();
			}
		};
		return wrapper;
	}
	
*/	

}
