package qbts.distances.test;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

import junit.framework.TestCase;

import org.junit.Test;

import qbts.distances.IntervalKernel;
import srctest.HelperOperatorConstructor;

public class TestIntervalKernel extends TestCase{

	public void testEntradasNulas() {
		IntervalKernel sim= new IntervalKernel();
		
		// Comprobación casos triviales de entradas nulas
		double[] e1=null;
		double[] e2=null;
		
		assertTrue("No devuelve NaN ante vectores nulos", Double.isNaN(sim.similarity(e1, e2)));
		double[] p1={ 1 ,  2 , 3};
		assertTrue("No devuelve NaN ante primer vector nulo", Double.isNaN(sim.similarity(e1, p1)));
		
		assertTrue("No devuelve NaN ante segundo vector nulo.", Double.isNaN(sim.similarity(p1, e1)));
	}

	
	public void testCasosBase() {
		IntervalKernel sim= new IntervalKernel();
		
//		Hay que introducir un conjunto de límites
		HelperOperatorConstructor hOp=new HelperOperatorConstructor();
		double [][] l= new double[3][];
		for (int i=0; i<3;i++)
			l[i] = new double[]{-1, 0 , 1};

		hOp.setPrivateArrayObject("limits", "qbts.distances.IntervalKernel", sim, l);
		
		// Los ejemplos ya están discretizados por lo que sus valores representan las cadenas en 
		// que se han convertido indexadas desde 0...
		// Identidad 
		double[] p1={ 1 ,  0 , 1};
		double[] p2={ 1 ,  0 , 1};
		assertEquals("Identidad no considerada",3.0,sim.similarity(p1, p2));
		// Desigualdad
		double[] p3={ 0 ,  1 , 0};
		assertTrue("Desigualdad total incorrectamente tratada",(sim.similarity(p1, p3)<3.0));
	}
	
	public void testValor(){
		IntervalKernel sim= new IntervalKernel();
		
		HelperOperatorConstructor hOp=new HelperOperatorConstructor();
		double [][] l= new double[30][];
		for (int i=0; i<30;i++)
			l[i] = new double[]{-4, 0 , 2, 4, 8};

		hOp.setPrivateArrayObject("limits", "qbts.distances.IntervalKernel", sim, l);
		
		double[] p1={
				2 ,1 ,3 ,0 ,2 ,0 ,1 ,3 ,2 ,0 ,1 ,3 ,2 ,2 ,0 ,0 ,2 ,3 ,1 ,0 ,1 ,3 ,0 ,1 ,2 ,2 ,1 ,1 ,2 ,3 
		};
		double[] p2={
				0 ,3 ,1 ,1 ,1 ,3 ,2 ,3 ,0 ,3 ,0 ,0 ,0 ,2 ,2 ,0 ,3 ,0 ,1 ,3 ,3 ,2 ,0 ,0 ,2 ,3 ,1 ,1 ,1 ,3 ,
		};
		
		DecimalFormat DForm = new DecimalFormat("#.######");
		assertEquals("Proporcionalidad incorrectamente tratada",DForm.format(9.890442274),DForm.format(sim.similarity(p1, p2)));
	}
	
}
