package qbts.preprocessing.discretization.test;

import junit.framework.TestCase;
import static org.junit.Assert.*;

public class TestBinDiscretizationSeries extends TestCase {

	// Los srctest que hay que hacer son:
	// Los triviales 
			// Si el conjunto está vacío dar un error
	// Los básicos
			// Si el parámetro de tratar como series no está activado pasarlo al normal
			// Si el conjunto sólo tiene un atributo normal pasarlo al normal
			// ExampleSet con varios atributos que no forman serie
			//		verificar que el tratamiento es igual que el del normal 
			// Si el conjunto forma una única serie ver que el tratamiento es correcto
			// Si se activa el parámetro tratar todo como una serie
}
