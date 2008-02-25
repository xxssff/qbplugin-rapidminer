package qbts.discretization.test;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import qbts.discretization.CumDiscretizerBlock;
import test.HelperOperatorConstructor;

import com.rapidminer.operator.Operator;


public class testAmevaDiscretizer extends TestCase {

	
	public void testNextMaxCAIM(){
		//creo una lista de bloques
/*		Cl
 * 		1	2	2	1	0	0	0	3	2	2
		0	0	1	3	2	2	3	0	0	0
	valor	1	2	3	4	5	6	8	10	12
*/
		double[][] matriz={
				{1.0,1.0,2.0},
				{1.0,1.0,2.0},
				{2.0,0.0,2.0},
				{2.0,1.0,2.0},
				{2.0,1.0,2.0},
				{3.0,1.0,2.0},
				{3.0,0.0,2.0},
				{3.0,0.0,2.0},
				{3.0,0.0,2.0},
				{4.0,0.0,2.0},
				{4.0,0.0,2.0},
				{5.0,0.0,2.0},
				{5.0,0.0,2.0},
				{6.0,0.0,2.0},
				{6.0,0.0,2.0},
				{6.0,0.0,2.0},
				{8.0,1.0,2.0},
				{8.0,1.0,2.0},
				{8.0,1.0,2.0},
				{10.0,1.0,2.0},
				{10.0,1.0,2.0},
				{12.0,1.0,2.0},
				{12.0,1.0,2.0},

		};

		List<CumDiscretizerBlock> lBCum=new ArrayList<CumDiscretizerBlock>();
		for (int f=0;f<matriz.length;f++){
			lBCum.add(new CumDiscretizerBlock(matriz[f][0],(int)matriz[f][1],(int)matriz[f][2]));
		}
		CumDiscretizerBlock.computeAcums(lBCum);
		CumDiscretizerBlock.joinValues(lBCum);

		// creo la lista con los cortes iniciales
		List<Integer> cortes=new ArrayList<Integer>();
		cortes.add(Integer.valueOf(0));
		cortes.add(Integer.valueOf(lBCum.size()-1));
		
		Object[] args={ cortes,lBCum};

		HelperOperatorConstructor hOp=new HelperOperatorConstructor();
		Operator op=hOp.createOperatorInstance("OPE1", "qbts.discretization.AmevaDiscretizer", "QBTS");
		Method metodo = hOp.findPrivateMethod("qbts.discretization.AmevaDiscretizer", "nextMaxAmeva");
		Object ret= hOp.invokePrivateMethodOperator(op, metodo,  args);
		
		//Object ret=hOp.invokePrivateMethodOperator("nextMaxCAIM", "qbts.discretization.CAIMDiscretizer", "OPE1", 
				// "Description", args);
	
		assertEquals(5,((Integer) ret).intValue()) ;
		
		cortes.add(Integer.valueOf(5));
		ret= hOp.invokePrivateMethodOperator(op, metodo,  args);

		assertEquals(1,((Integer) ret).intValue()) ;
		
	}
	
	
	
	
	

}


