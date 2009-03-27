package qbts.distances.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

import qbts.distances.QSISimilarity;

public class TestQSISimilarity extends TestCase{

	public void testEntradasNulas() {
		QSISimilarity sim= new QSISimilarity();
		
		// Comprobación casos triviales de entradas nulas
		double[] e1=null;
		double[] e2=null;
		
		assertTrue("No devuelve NaN ante vectores nulos", Double.isNaN(sim.similarity(e1, e2)));
		double[] p1={ 1 ,  2 , 3};
		assertTrue("No devuelve NaN ante primer vector nulo", Double.isNaN(sim.similarity(e1, p1)));
		
		assertTrue("No devuelve NaN ante segundo vector nulo.", Double.isNaN(sim.similarity(p1, e1)));
	}

	
	public void testCasosBase() {
		QSISimilarity sim= new QSISimilarity();
		
		// Identidad 
		double[] p1={ 1 ,  2 , 3};
		double[] p2={ 1 ,  2 , 3};
		assertEquals("Identidad no considerada",1.0,sim.similarity(p1, p2));
		// Desigualdad
		double[] p3={ 4 ,  5 , 6};
		assertEquals("Desigualdad total incorrectamente tratada",0.0,sim.similarity(p1, p3));
	}
	
	public void testProporcionales(){
		QSISimilarity sim= new QSISimilarity();
		
		double[] p1={
		1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
		1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 
		1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
		1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
		1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
		1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
		1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
		1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
		1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
		1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 };
		

		double[] p001={
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 };
		
		double[] p002={
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 };
		double[] p003={
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 };
		double[] p004={
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 };
		double[] p005={
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 ,
				1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 , 	1 };
		
		assertEquals("Proporcionalidad incorrectamente tratada",0.1,sim.similarity(p1, p001));
		assertEquals("Proporcionalidad incorrectamente tratada",0.2,sim.similarity(p1, p002));
		assertEquals("Proporcionalidad incorrectamente tratada",0.3,sim.similarity(p1, p003));
		assertEquals("Proporcionalidad incorrectamente tratada",0.4,sim.similarity(p1, p004));
		assertEquals("Proporcionalidad incorrectamente tratada",0.5,sim.similarity(p1, p005));
		
	}

	public void testCadenas(){
		QSISimilarity sim= new QSISimilarity();
		double[] c001={
				5, 	5, 	5, 	1, 	2, 	4, 	5, 	2, 	3, 	1, 	1, 	3, 	5, 	3, 	3, 	2, 	5, 	5, 	1, 	5, 	5, 	1, 	5, 	5, 	5, 	5, 	5, 	5, 	5, 	1
		};
		double[] c002={
				2, 	5, 	3, 	5, 	3, 	5, 	3, 	5, 	3, 	3, 	4, 	3, 	2, 	3, 	3, 	3, 	2, 	4, 	4, 	4, 	5, 	4, 	2, 	3, 	5, 	4, 	2, 	1, 	2, 	4
		};
		
		double res; 
		res = 12.0 / 30.0;

		assertEquals("Valoración incorrecta",res,sim.similarity(c002,c001));
		assertEquals("Valoración incorrecta",res,sim.similarity(c001,c002));

	}
}
