package qbts.discretization.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import qbts.discretization.CumDiscretizerBlock;

public class TestCumDiscretizerBlock extends TestCase{
	
	
	
	public void testCreacionBloques(){
		 List<CumDiscretizerBlock> lBlock = new ArrayList<CumDiscretizerBlock>();
		
		//añado tres bloques
		//double value, Example example, int attributeNumber, int clase, int numClases) 
		CumDiscretizerBlock b1=
			new CumDiscretizerBlock(0.01,null,0,0,2);
		assertEquals("Comprueba que se crea con la clase correcta",b1.getClas(), 0);
		assertEquals("Comprueba que cuenta la aparición ",b1.getOccu()[0], 1);
		assertEquals("Comprueba que no cuenta donde no es",b1.getOccu()[1], 0);
		lBlock.add(b1);
		b1=new CumDiscretizerBlock(0.1,null,1,0,2);
		lBlock.add(b1);
		b1=new CumDiscretizerBlock(1.0,null,2,0,2);
		lBlock.add(b1);
		b1=new CumDiscretizerBlock(10.0,null,3,1,2);
		assertEquals("Comprueba que se crea con la clase correcta",b1.getClas(), 1);
		assertEquals("Comprueba que cuenta la aparición ",b1.getOccu()[1], 1);
		assertEquals("Comprueba que no cuenta donde no es",b1.getOccu()[0], 0);
	
		lBlock.add(b1);
		
		Iterator<CumDiscretizerBlock> it = lBlock.iterator();
		while(it.hasNext()){
			b1=it.next();
			System.out.println(">"+b1.toString());
		}
		assertTrue("Creados y exportados a salida estandard",true);
		System.out.println("Creados y exportados a salida estandard");
		
/*		lBlock.get(0).setAcum(lBlock.get(0).getOccu());
		for (int i=0;i<lBlock.size()-1;i++){
			lBlock.get(i+1).Acumula(lBlock.get(i));
		}		*/
		
		CumDiscretizerBlock.computeAcums(lBlock);
		
		it = lBlock.iterator();
		while(it.hasNext()){
			b1=it.next();
			System.out.println(">"+b1.toString());
		}

		// los acumulados deben ser {3,1};
		assertEquals(" Repasa los acumulados",3, lBlock.get(3).getAcum()[0]);
		assertEquals(" Repasa los acumulados",1, lBlock.get(3).getAcum()[1]);
		
	}
	
	

	public void testValoresRepetidos(){
		 List<CumDiscretizerBlock> lBlock = new ArrayList<CumDiscretizerBlock>();
			
			//añado tres bloques
			//double value, Example example, int attributeNumber, int clase, int numClases) 
			CumDiscretizerBlock b1=
				new CumDiscretizerBlock(0.1,null,0,0,2);
			lBlock.add(b1);
			b1=new CumDiscretizerBlock(0.1,null,3,1,2);
			lBlock.add(b1);
			b1=new CumDiscretizerBlock(0.1,null,0,0,2);
			lBlock.add(b1);
			
			CumDiscretizerBlock.computeAcums(lBlock);
			assertEquals("No coinciden los acumulados",2,lBlock.get(2).getAcum()[0]);
			assertEquals("No coinciden los acumulados",1,lBlock.get(2).getAcum()[1]);
			
			
			assertEquals("El número de elementos no es correcto ",lBlock.size(),3);
			CumDiscretizerBlock.joinValues(lBlock);
			assertEquals("No se eliminan los elementos con mismo valor ",1,lBlock.size());
			
			assertEquals("No coinciden los acumulados",2,lBlock.get(0).getAcum()[0]);
			assertEquals("No coinciden los acumulados",1,lBlock.get(0).getAcum()[1]);
			assertEquals("No coinciden los acumulados",2,lBlock.get(0).getOccu()[0]);
			assertEquals("No coinciden los acumulados",1,lBlock.get(0).getOccu()[1]);
		
	}
	
}
