package qbts.preprocessing.discretization.test;

import java.io.File;
import java.util.HashMap;
import java.util.SortedSet;

import srctest.HelperOperatorConstructor;

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
	

	public void testBinDiscretization(){
		// quiero comprobar que la discretización es la misma usando
		// 1 attributo con 100 ejemplos con el operador estándard RM
		// 5 atributos en 20 ejemplos con el nuevo operador activando el procesar_todos_los attributos juntos
		// PROBADO MANUALMENTE EN RM 4.1
		RapidMiner.init();
		IOContainer sal1;
		try{
		  sal1 = runSampleTest("Discretization"+File.separator+"bin1.xml");
		//Obtengo los dos modelos obtenidos
		  DiscretizationModel model1 = (DiscretizationModel) sal1.get(Model.class);
		  DiscretizationModel model2 = (DiscretizationModel) sal1.get(Model.class);
		// Es correcto si  uno de ellos tiene sólo un rango y es igual a todos los del otro
		  HelperOperatorConstructor hOp=new HelperOperatorConstructor();
		  // tengo que obtener los rangesmap que son privados
		  HashMap<String, SortedSet<Tupel<Double, String>>> rango1 = 
			  (HashMap<String, SortedSet<Tupel<Double, String>>>) 
			  hOp.getPrivateFieldOperator("rangesMap","com.rapidminer.operator.preprocessing.discretization.DiscretizationModel",model1);
		  HashMap<String, SortedSet<Tupel<Double, String>>> rango2 = 
			  (HashMap<String, SortedSet<Tupel<Double, String>>>) 
			  hOp.getPrivateFieldOperator("rangesMap","com.rapidminer.operator.preprocessing.discretization.DiscretizationModel",model2);
			
		  // Compruebo cual tiene sólo un elemento
		  if (rango1.size()==1){
			  assertEquals(rango2.size(),6);
			  //TODO: ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO
			  //  ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO
			  //   ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO 
			  //  ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO ACABAR ESTO
		  }
		 // else{
			  
		 // }
			  
		}
		catch (Exception e){
			e.printStackTrace();
			assertFalse(true);
			
		}
		
		
	}
	
	
/*	public static Test suite() throws Exception{
		//initializes Rapidminer first before any srctest is run 
	
		TestSuite suite = new TestSuite("Sample srctest");
		suite.addTest(new DiscretizationExtensionTests("testBinDiscretization"));
		
		return suite;
	}*/
	

}
