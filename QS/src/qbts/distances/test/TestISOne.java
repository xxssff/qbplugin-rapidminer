package qbts.distances.test;

import java.text.DecimalFormat;

import qbts.distances.ISone;
import junit.framework.TestCase;

public class TestISOne extends TestCase {

	public void testTriviales(){
		ISone sim= new ISone();
		
		// Comprobación casos triviales de entradas nulas
		double[] e1=null;
		double[] e2=null;
		
		assertTrue("No devuelve NaN ante vectores nulos", Double.isNaN(sim.calculateSimilarity(e1, e2)));

		// Comprobación casos triviales de tamaños distintos
		double[] p1={ 1 ,  2 , 3};
		double[] p2={   2 , 3};
		assertTrue("No devuelve NaN ante tamaños diferentes", Double.isNaN(sim.calculateSimilarity(p1, p2)));
	}
	
	public void testPuntos(){
		ISone sim= new ISone();
		// Comprobación casos base con un elemento puntual
		double[] p1={ 1 ,  2 };
		double[] p2={ 2 , 2};
		assertTrue("1- No devuelve 0 con un punto en la entrada", sim.calculateSimilarity(p1, p2)==0);
		
		assertTrue("2- No devuelve 0 con un punto en la entrada", sim.calculateSimilarity(p2, p1)==0);

		// Comprobación casos base con un elemento puntual
		double[] p3={ 2 , 2};
		assertTrue("3- No devuelve 0 con dos puntos en la entrada", sim.calculateSimilarity(p2, p3)==0);		
	}
	
	public void testIguales(){
		ISone sim= new ISone();
		// Comprobación casos base con un elemento puntual
		double[] p1={ 2 ,  3 };
		double[] p2={ 2 , 3};
		assertEquals("No devuelve 1 con intervalos iguales",1.0, sim.calculateSimilarity(p1, p2));	
	}
	
	public void testCompleto(){
		ISone sim= new ISone();

		double[] p1={1,	2,	3,	4,	5,	6,	1,	2,	3,	4,	5,	6 };
		double[] p2={2,	3,	4,	2,	1,	2,	5,	6,	3,	2,	1,	2 };

		DecimalFormat DForm = new DecimalFormat("#.######");
		assertEquals("No devuelve valor correcto ",DForm.format(3.454348181)
				, DForm.format(sim.calculateSimilarity(p1, p2)));
	}
}
