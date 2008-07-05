package qbts.test;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.MissingIOObjectException;
import com.rapidminer.test.ExampleSetDataSampleTest;
import com.rapidminer.test.OperatorDataSampleTest;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.ParameterService;

public class DiscretizationExtensionTests extends TestCase {


	public DiscretizationExtensionTests(String arg0) {
		super(arg0);
	}




	public IOContainer runSampleTest(String file) throws Exception {
		File processFile = new File(ParameterService.getRapidMinerHome(), "srctest" + File.separator + file);
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
		try{
		  IOContainer sal1 = runSampleTest(".."+File.separator+".."+File.separator+"srctest"+File.separator+"Discretization"+File.separator+"bin1.xml");
		}
		catch (Exception e){
			e.printStackTrace();
		}
		//Obtengo los dos modelos obtenidos
		
		
	}
	
	
	public static Test suite() throws Exception{
		//initializes Rapidminer first before any srctest is run 
		RapidMiner.init();
		LogService.getGlobal().setVerbosityLevel(LogService.OFF);
		
		TestSuite suite = new TestSuite("Sample srctest");
		suite.addTest(new DiscretizationExtensionTests("testBinDiscretization"));
		
		return suite;
	}
	

}
