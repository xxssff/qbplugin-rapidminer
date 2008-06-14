package qbts;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRowReader;
import com.rapidminer.example.table.DoubleArrayDataRow;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.ListDataRowReader;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.learner.SimplePredictionModel;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;

/**
 * <p>
 * SeriesDiscretizationModel. This class store the information of a model. This
 * includes the discretized learningSet and the discretization schema
 * 
 * </p>
 * 
 * @author F.J. Cuberos
 * @version $Id: DiscretizationModel.java,v 1.0 2004/08/23 Exp $
 */



public class DiscretizationModel extends SimplePredictionModel {

	private ExampleSet learningSet = null;

	private List lBlocks;
	
	private int discMethod;

	private double[][] esqDiscre = null;

	private int metSimil;

	private double paraSimil;

	private int kNN;
	
	private int NivelLog;

	boolean Nor;

	boolean Tip;

	boolean Dif;

	private boolean seriesFixedLength;

	private boolean OnlyAttributes = true;

	private int[] RelAttBlck = null;

	/** Empty constructor */
//	public DiscretizationModel() {
//		super();
//	}

	/** NO ACTION CONSTRUCTOR - DO NOT USE */
	public DiscretizationModel(ExampleSet learningSet) {
		super(learningSet);
	}

	//***************************************************************
    //***************************************************************
	// PASO A 3.4
	public void readPredictionModelData(ObjectInputStream kk){
		
	}
	
	//***************************************************************
    //***************************************************************
	// PASO A 3.4
	public void writePredictionModelData(ObjectOutputStream kk){
		
	}
	
	
	
	/**
	 * Normal Constructor. Input the learning ExampleSet and some parameters.
	 * Creates a list of normal attributes in exampleSet changing the non
	 * nominal attributes to nominal. This list is internally stored. Creates a
	 * version of the learning ExampleSet discretized.
	 *  
	 */
	public DiscretizationModel(ExampleSet learningSet, List Blocks,
			double[][] esqDisc,int discMet, int NivelLog, int metSimil, double paraSimil,
			int Neighbours, boolean Nor, boolean Tip, boolean Dif, boolean seriesFixedLength) {

		super(learningSet);
		LogService.logMessage("MD - Comienza Modelo Discretizar",
				LogService.STATUS);

		this.discMethod = discMet;
		this.metSimil = metSimil;
		this.paraSimil = paraSimil;
		this.kNN= Neighbours;
		this.NivelLog = NivelLog;
		this.lBlocks = Blocks;

		this.Nor = Nor;
		this.Tip = Tip;
		this.Dif = Dif;
		this.seriesFixedLength = seriesFixedLength;

		esqDiscre = esqDisc;
		
		// check if the blocks are series or attributes
		int numSeries=0;
		for (int currBlock = 0; currBlock < lBlocks.size(); currBlock++) {
			if (((BlockData) lBlocks.get(currBlock)).isSeries) 
				numSeries++;
		}
		OnlyAttributes=(numSeries==0);
		
    	this.learningSet = discretizeExampleSet( learningSet);

    	this.setLabel(learningSet.getLabel());
    
	}

	

	//***************************************************************
	//***************************************************************
    public void writeData(ObjectOutputStream out) throws IOException {
    	out.writeChars("Esto es una prueba");
        }
	

	//***************************************************************
	//***************************************************************
    public ExampleSet discretizeExampleSet(ExampleSet eSet ){
    	// y ahora que tengo el esquema de discretización
    	// creo el conjunto de test que lo haré discretizando el que entra.
    	
    	List lAtt = new LinkedList();
    	
    	if (this.discMethod  == DiscretizationMethods.NONE)
    	{
    		Attribute[] aAtt=eSet.getExampleTable().getAttributes();
        	
        	for (int i=0; i < aAtt.length ;i++){
        		lAtt.add( aAtt[i]);
        	}
        	// ¿hay algun atributo especial en la lista?
        	//¿esta el prediction?
    		ExampleTable table = new MemoryExampleTable(lAtt, eSet.getExampleTable().getDataReader());
    		return table.createCompleteExampleSet(
    				eSet.getLabel(), null, null, eSet.getId());
 
/*    		
    		
    		for (int i=0; i < eSet.getNumberOfAttributes() ;i++){
    			Attribute at = eSet.getAttribute(i);
    			Attribute aa = AttributeFactory.createAttribute(at.getName(),
    					at.getValueType() , at.getBlockType(), 
    					at.getBlockNumber(), null);
    			aa.setTableIndex(at.getTableIndex());
    			lAtt.add(aa);
    		}
    		
    		List ldataRows = new LinkedList();// List for DataRows
    		
    		ExampleReader reader = eSet.getExampleReader();
    		DataRowFactory dRF = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY);
    		
    		while (reader.hasNext()) {
    			Example example = reader.next();
    			
    			double[] ndatos=new double[example.getDataRow()];
    			ndatos[posId]=example.getValue(id);
    			DoubleArrayDataRow ddr2= dRF.create(example.getDataRow(),eSet.getExampleTable().getAttributes());
    			DoubleArrayDataRow ddr = new DoubleArrayDataRow((double[]) ndatos.clone());
    			ldataRows.add(ddr);
    		}
    		
    		//	DataRowReader dR = new ListDataRowReader(ldataRows.iterator());
    		LogService.logMessage("MD - Se han procesado " + ldataRows.size()
    				+ " elementos", LogService.STATUS );
    		
    		//Y tras almacenarlo todo en la lista dataRows creo el Example
    		DataRowReader dataR = new ListDataRowReader(ldataRows.iterator());
    		ExampleTable table = new MemoryExampleTable(lAtt, dataR);
    		
    				AttributeSet aSet = new AttributeSet();
    		 for (int v = 0; v < lAtt.size(); v++) {
    		 aSet.addAttribute((Attribute) lAtt.get(v));
    		 }
    		 
    		// ID Label test=table.createExampleSet(aSet);
    		LogService.logMessage("MD - Antes de crear test", LogService.MINIMUM);
    		return table
    		.createCompleteExampleSet(label, null, null, id);

    			
    		ExampleTable table = new MemoryExampleTable(lAtt, eSet.getExampleTable().getDataReader());
    		return table.createCompleteExampleSet(
    				eSet.getLabel(), null, null, eSet.getId());
*/    		
    	}
    	else
    	{
    		
    		int numAtributos = eSet.getNumberOfAttributes();
    		
    		int posId = -1;
    		int posLabel = -1;
    		
    		Attribute label = null;
    		if (eSet.getLabel() != null)
    			label = (Attribute) eSet.getLabel().clone();
    		
    		Attribute id = null;
    		if (eSet.getAttribute(ExampleSet.ID_NAME) != null) 
    			id = (Attribute) eSet.getAttribute(ExampleSet.ID_NAME).clone();
    		
    		// Creates a new Attribute list.
    		CrearListaAtributos(lAtt, eSet, lBlocks, numAtributos);
    		
    		// Relation Attribute -> belonging block
    		RelAttBlck = new int[numAtributos];
    		// Complete the labels mapping 
    		for (int i = 0; i < lBlocks.size(); i++) {
    			int inicio = ((BlockData) lBlocks.get(i)).inicio;
    			int fin = ((BlockData) lBlocks.get(i)).fin;
    			for (int j = inicio; j <= fin; j++) {
    				RelAttBlck[j] = i;
    				if (((BlockData) lBlocks.get(i)).discretizar) {
    					for (int es = 1; es < esqDiscre[i].length; es++) 
    						((Attribute) lAtt.get(j)).mapString(String
    								.valueOf((char) (96 + es)));
    					//	((Attribute) lAtt.get(j)).mapString(new String("*"));
    				}
    				else{
    					// es un bloque nominal por lo hay que buscar los valores
    					//  y mapearlos en los correspondientes nuevos atributos 
    					List valores= new LinkedList(eSet.getAttribute( ((Attribute)lAtt.get(j)).getTableIndex()).getValues()  );
    					Collections.sort(valores);
    					Iterator it=valores.iterator() ;
    					while (it.hasNext()) {
    						((Attribute) lAtt.get(j)).mapString((String)it.next());
    					}
    				}
    			}	}
    		
    		
    		if (label != null) 
    			for (int i = 0; i < lAtt.size(); i++) {
    				if (((Attribute) lAtt.get(i)).getTableIndex() == label.getTableIndex())
    					posLabel = i;
    			}
    		if (id != null) 
    			for (int i = 0; i < lAtt.size(); i++) 
    				if (((Attribute) lAtt.get(i)).getTableIndex() == id.getTableIndex())
    					posId = i;
    		
    		//	    se crea un array para almacenar los valores
    		double[] ndatos = new double[numAtributos + (posId == -1 ? 0 : 1)
    		                             + (posLabel == -1 ? 0 : 1)];
    		
    		int numero = 0;
    		
    		List ldataRows = new LinkedList();// List for DataRows
    		
    		Iterator<Example> reader = eSet.iterator();
    		while (reader.hasNext()) {
    			Example example = reader.next();
    			
    			numero++;		
    			for (int currBlock = 0; currBlock < lBlocks.size(); currBlock++) {
    				
    				int inicio = ((BlockData) lBlocks.get(currBlock)).inicio;
    				int ultimo = ((BlockData) lBlocks.get(currBlock)).fin;
    				Attribute Att;
    				
    				if (((BlockData) lBlocks.get(currBlock)).discretizar) {
    					// LEO TODOS los valores en un vector
    					double[] valores = new double[ultimo - inicio + 1];
    					for (int i = inicio; i <= ultimo; i++) {
    						valores[i - inicio] = example.getValue(i);
    					}
    					//		    		 los mando a diferenciar...
    					double[] val2 = DiscretizationMethods
    					.Preprocess_Series(valores, seriesFixedLength, Nor,
    							Tip, Dif);
    					// para cada atributo que pertenezca al bloque
    					if (Dif)
    						ultimo--;
    					for (int atriact = inicio; atriact <= ultimo; atriact++) {
    						Att = ((Attribute) lAtt.get(atriact));
    						ndatos[Att.getTableIndex()] = Att.mapString(Discretiza(
    								val2[atriact - inicio],
    								esqDiscre[RelAttBlck[atriact]]));
    					}
    					if (Dif) {
    						Att = ((Attribute) lAtt.get(++ultimo));
    						ndatos[ultimo] = Att.mapString("*");
    					}
    				} else { // TODO: si es una serie nominal no se controla 
    					Att = ((Attribute) lAtt.get(inicio));
    					if (Att.getName().equals("VLS_SIZE")) {
    						ndatos[inicio] = example.getValue(inicio);
    					} else {
    						ndatos[inicio] = Att.mapString(example
    								.getValueAsString(Att));
    					}
    				}
    			}
    			
    			if (label != null) {
    				ndatos[posLabel] = label.mapString(example.getValueAsString(label));
    			}
    			
    			if (id != null)
    				if (((Attribute) lAtt.get(posId)).getBlockType() == Ontology.NOMINAL)
    					ndatos[posId] = ((Attribute) lAtt.get(posId))
    					.mapString(example.getValueAsString((Attribute) lAtt.get(posId)));
    				else
    					ndatos[posId]=example.getValue(id);
    			
    			DoubleArrayDataRow ddr = new DoubleArrayDataRow((double[]) ndatos
    					.clone());
    			ldataRows.add(ddr);
    		}
    		
    		//	DataRowReader dR = new ListDataRowReader(ldataRows.iterator());
    		LogService.logMessage("MD - Se han procesado " + ldataRows.size()
    				+ " elementos", LogService.STATUS );
    		
    		//Y tras almacenarlo todo en la lista dataRows creo el Example
    		DataRowReader dataR = new ListDataRowReader(ldataRows.iterator());
    		ExampleTable table = new MemoryExampleTable(lAtt, dataR);
    		
    		/*		AttributeSet aSet = new AttributeSet();
    		 for (int v = 0; v < lAtt.size(); v++) {
    		 aSet.addAttribute((Attribute) lAtt.get(v));
    		 }
    		 */
    		// ID Label test=table.createExampleSet(aSet);
    		LogService.logMessage("MD - Antes de crear test", LogService.MINIMUM);
    		return table
    		.createCompleteExampleSet(label, null, null, id);
    	}
    }
	
	//	********************************************************************************
	//	********************************************************************************
	/**
	 * APPLY. Predicts the label of the input Test ExampleSet using the examples
	 * from the learning Set, previously created, as master patterns.
	 */
	public void apply(ExampleSet eSet) {

		Attribute att=eSet.getAttribute(ExampleSet.PREDICTION_NAME);
      if (att==null){
    	  // ¿porque no se ha creado supongo que en la versión 3.1 la creación 
    	  this.createPredictedLabel(eSet);
      }
		
		//int actTest = 0;
		Iterator<Example> readerTest = eSet.iterator();
		while (readerTest.hasNext()) {
			Example exampleTest = readerTest.next();
			//v3.0 exampleTest.setPredictedLabel(exampleTest.getAttribute(ExampleSet.LABEL_NAME ).mapIndex((int) predict(exampleTest))     );
			double valor=predict(exampleTest);
			exampleTest.setPredictedLabel(valor);
			//actTest++;
		}
	}

	//	********************************************************************************
	//	********************************************************************************
	/**
	 * Predict the corresponding label for an example. 
	 */
	public double predict(Example example) {
		double[] similitudes = new double[learningSet.size()];
		String[] etiquetas = new String[learningSet.size()];

		List orden = new LinkedList(); //contiene las posiciones de cada
		// serie en el orden  de bloques completo

		// convierte el ejemplo en un vector de vectores double
		double[][] vTest = DiscretizaExampleTest(example, orden);
		double indice = 0;

/*		if (OnlyAttributes )
			LogService.logMessage("No hay Series----------------------------"
					, LogService.MINIMUM);
*/
		int actLear = 0;
		Iterator<Example> readerLearning = learningSet.iterator();
		while (readerLearning.hasNext()) {
			Example exampleLearning = readerLearning.next();

			// convierte el ejemplo en un vector de double
			double[][] vLear = DiscretizaExampleLear(exampleLearning);
	
			if (OnlyAttributes )
			{ 
				double[] vl=new double[vLear.length];
				double[] vt=new double[vLear.length];
				
/*				String cadl= new String();
				String cadt= new String();
*/	
				for (int i=0;i<vLear.length ;i++){
					vl[i]=vLear[i][0];
					vt[i]=vTest[i][0];
					/*
					cadl = cadl +  ((Attribute) learningSet.getAttribute(
							((BlockData) lBlocks.get(i)).inicio)
							).getAsString( vl[i],2);
					cadt = cadt +  ((Attribute) learningSet.getAttribute(
							((BlockData) lBlocks.get(i)).inicio)
							).getAsString( vt[i],2);
					*/		
				}

				double[] l=new double[1];
				double[] t=new double[1];
			    double indpar=0;
				
				switch (metSimil) {
				case DiscretizationMethods.QSI: //QSI
					    indice = DiscretizationMethods.QSIndex(vt, vl);
   		/*		LogService.logMessage("Comparo vt[]=" + cadt + " <->  vl[]= "
						+ cadl + "   indice =" + indice, LogService.MINIMUM);*/
				 
					break;
				case DiscretizationMethods.IntervalKernel: //Kernel
					// Intervalar
					indice=0;
/*				    LogService.logMessage("Comparo vt[]=" + cadt + " <->  vl[]= "
						+ cadl + "   indice =" + indice, LogService.MINIMUM);
*/
					for (int i=0;i<vLear.length ;i++){
						l[0]=vLear[i][0];
						t[0]=vTest[i][0];
						indpar = DiscretizationMethods.Kernel_Intervalar(t,
							l, esqDiscre[((Integer) orden
									.get(i)).intValue()],paraSimil);
						indice=indice+indpar;					
					}
					break;
					
				case DiscretizationMethods.Euclidean :
					indice=DiscretizationMethods.EuclideanSeries(vt,vl);
					break;
				case DiscretizationMethods.DTW :
					indice=DiscretizationMethods.DTW(vt,vl);
					break;
				case DiscretizationMethods.IKMulti: //Kernel
					indice=1;

					for (int i=0;i<vLear.length ;i++){
						l[0]=vLear[i][0];
					    t[0]=vTest[i][0];
						indpar = DiscretizationMethods.Kernel_Intervalar(t,
								l, esqDiscre[((Integer) orden
									.get(i)).intValue()],paraSimil);
						indice=indice*indpar;					
					}
				   /* LogService.logMessage("IKM Comparo vt[]=" + cadt + " <->  vl[]= "
							+ cadl + "   indice =" + indice, LogService.MINIMUM);
							*/
					break;
				}
			}
			else{
				double[] parciales = new double[vLear.length];
				for (int currSeries = 0; currSeries < vLear.length; currSeries++) {
				// compara los dos
					switch (metSimil) {
					case DiscretizationMethods.QSI: //QSI
						indice = DiscretizationMethods.QSIndex(vTest[currSeries], vLear[currSeries]);
						break;
					case DiscretizationMethods.IntervalKernel: //Kernel
						// Intervalar
						indice = DiscretizationMethods.Kernel_Intervalar(vTest[currSeries],
								vLear[currSeries], esqDiscre[((Integer) orden
										.get(currSeries)).intValue()],paraSimil);
						break;
					case DiscretizationMethods.Euclidean :
						indice=DiscretizationMethods.EuclideanSeries(vTest[currSeries],vLear[currSeries]);
						break;
						//TODO cuando son series pueden ser 
						//univariables .-aplicación que está hecha
						//o multivariables y se puede aplicar DTW
						//     a cada variable y obtener la media (como hace JJRodriguez
						//            en TAMIDA2005
						//     o entendiendo que son puntos dimensionales los que se comparan
						//          aplicando la distancia euclídea
					case DiscretizationMethods.DTW :
						indice=DiscretizationMethods.DTW(vLear[currSeries],vTest[currSeries]);
						break;
				}
				parciales[currSeries] = indice;
				}
			}
			similitudes[actLear] = indice;
			//v3.0 etiquetas[actLear] = exampleLearning.getLabelAsString();
			etiquetas[actLear] = 
				exampleLearning.getValueAsString(exampleLearning.getAttribute( ExampleSet.LABEL_NAME));
			actLear++;
		}

		switch (metSimil) { 			// Sort vectors
		case DiscretizationMethods.DTW: //DTW
		case DiscretizationMethods.Euclidean:
			DiscretizationMethods.Ordena_Vectores(similitudes, "A", 0, similitudes.length - 1,
					etiquetas);
			break;
		default:
			DiscretizationMethods.Ordena_Vectores(similitudes, "D", 0, similitudes.length - 1,
					etiquetas);
		break;
		}

		//TODO Hay que ampliar la selección para elegir en función del número 
		//  de vecinos
		
		String cadena = Mayoria(etiquetas,kNN);
		String nombre = ExampleSet.LABEL_NAME;
		Attribute att1= example.getAttribute(nombre);
		double valor = att1.mapString(cadena);
		return valor;
		//return example.getAttribute(ExampleSet.LABEL_NAME).mapString(etiquetas[0]);
	}

	
	/**
 * 
 * @return
 */
	String Mayoria(String[] etiquetas, int vecinos){
		// tengo que devolver la cadena que tenga mayoría entre las "vecinos" primeras
		String[] eti= new String[vecinos];
		double[] ocu=new double[vecinos];
		boolean found;
		int pos;
		
		int posicion=0;
		for (int i=0; i<vecinos;i++){
			found=false;
			pos=0;
			for (int j=0; j< posicion;j++){
				if (etiquetas[i]==eti[j]){
					found=true;
					break;
				}
				pos++;
			}
			if (found){
				ocu[pos]++;
			}
			else {
				eti[posicion]=etiquetas[i];
				ocu[posicion]++;
				posicion++;
			}
		}
		
		DiscretizationMethods.Ordena_Vectores(ocu, "D", 0, ocu.length - 1,eti);
		return eti[0];
	}
	
	
	//	********************************************************************************
	//	********************************************************************************
	/** Return the discretized LearningSet created in the constructor. */
	public ExampleSet getExampleSet() {
		ExampleSet eset = (ExampleSet) this.learningSet.clone();
		return eset;
	}

	//******************************************************************************
	//******************************************************************************
    /** Creates a new list of Attributes for the discretized ExampleSet.
	 * The numerical attributes are changed to NOMINAL.
	 */
	private void CrearListaAtributos(List lAtri, ExampleSet eS, List lBlocks,
			int numAtributos) {

		for (int currBlock = 0; currBlock < lBlocks.size(); currBlock++) {
			int inicio = ((BlockData) lBlocks.get(currBlock)).inicio;
			int fin = ((BlockData) lBlocks.get(currBlock)).fin;
			if (((BlockData) lBlocks.get(currBlock)).discretizar) {
					for (int atriact = inicio; atriact <= fin; atriact++) {
						Attribute at = eS.getAttribute(atriact);
						Attribute aa = AttributeFactory.createAttribute(at.getName(),
								Ontology.NOMINAL, at.getBlockType(), 
								at.getBlockNumber(), null);
				           
						aa.setTableIndex(at.getTableIndex());
						lAtri.add(aa);
					}
			} else {  //NOMINAL
					for (int atriact = inicio; atriact <= fin; atriact++) {
						Attribute at = eS.getAttribute(atriact);
						Attribute aa = AttributeFactory.createAttribute(at.getName(),
								at.getValueType(), at.getBlockType(),
								at.getBlockNumber(), null);

								aa.setTableIndex(at.getTableIndex());
						lAtri.add(aa);
					}
			}
		}

		//label
		if (eS.getLabel() != null) {
			lAtri.add(eS.getLabel().clone());
			((Attribute) lAtri.get(lAtri.size() - 1)).setTableIndex(eS.getLabel()
					.getTableIndex());
		}

		//	 ID
    	if (eS.getAttribute(ExampleSet.ID_NAME) != null) { 
   		lAtri.add(eS.getAttribute(ExampleSet.ID_NAME).clone());
		((Attribute) lAtri.get(lAtri.size() - 1)).setTableIndex(eS
				.getAttribute(ExampleSet.ID_NAME).getTableIndex());
		
		}
	}

	//********************************************************************************
	//********************************************************************************
	/**
	 * Returns the character corresponding to the input value.
	 * 
	 * EXPLICAR que la comparación de los límites es <= < <= <...
	 */
	private String Discretiza(double valor, double[] cortes) {
		String cad = new String();
		//char[] kk1 = new char[1];

		if (Double.isNaN(valor))
			cad = "@";
		else if (cortes.length > 2) {
			boolean encontrado = false;
			for (int i = cortes.length - 2; i > 0; i--) {
				if (valor > cortes[i]) {
					cad = String.valueOf((char) (97 + i));
					encontrado = true;
					break;
				}
				if (!encontrado)
					cad = "a";
				else
					cad = "*";
				}
			} else if (cortes.length==2)
				        cad="a";
			     else
			     	 cad = "#";

		return cad;
	}

	//********************************************************************************
	//********************************************************************************
	private double[][] DiscretizaExampleLear(Example example) {

		int numBlocks = 0;

		//Miro si son fijas o variables
		boolean fixedLengthSeries = true;
		int posVLS_SIZE = 0;
		if (learningSet.getAttribute("VLS_SIZE") != null) {
			fixedLengthSeries = false;
			posVLS_SIZE = learningSet.getAttribute("VLS_SIZE").getTableIndex();
		}

		/* 
		 * COMENTO LA BUSQUEDA DE BLOQUES QUE SEAN SERIES PARA PERMITIR
		 * LA DISCRETIZACIÖN DE ATRIBUTOS 
		 * 
		 * 
		 for (int currBlock = 0; currBlock < lBlocks.size(); currBlock++) {
			if (((BlockData) lBlocks.get(currBlock)).isSeries)
				numBlocks++;
		}*/
		numBlocks=lBlocks.size(); 

		double[][] v = new double[numBlocks][];

		numBlocks = 0;
		for (int currBlock = 0; currBlock < lBlocks.size(); currBlock++) {
			if (((BlockData) lBlocks.get(currBlock)).isSeries) {
				int longitud = 0;
				int inicio = ((BlockData) lBlocks.get(currBlock)).inicio;
				int fin = 0;
				if (fixedLengthSeries) {
					fin = ((BlockData) lBlocks.get(currBlock)).fin;
					longitud = fin - inicio + 1;
				} else {
					longitud = (int) example.getValue(posVLS_SIZE);
					fin = inicio + longitud - 1;
				}
				if (Dif) {
					longitud--;
					fin--;
				}
				v[numBlocks] = new double[longitud];
				// Como sólo entran los ejemplos del LearningSet sólo hay que
				// copiarlos.
				int i = 0;
				for (int atriact = inicio; atriact <= fin; atriact++) {
					v[numBlocks][i++] = example.getValue(atriact);
				}
				numBlocks++;
			}
			else{
				int longitud = 1;
				int inicio = ((BlockData) lBlocks.get(currBlock)).inicio;
				v[numBlocks] = new double[longitud];
				// Como sólo entran los ejemplos del LearningSet sólo hay que
				// copiarlos.
				
					v[numBlocks][0] = example.getValue(inicio);
				numBlocks++;
			}
		}
		return v;
	}

	//********************************************************************************
	//********************************************************************************
	private double[][] DiscretizaExampleTest(Example example, List lorden) {
		/*
		 * La Discretización del ejemplo de test devuelve un array de vectores
		 * donde cada uno contiene un bloque (una serie) discretizado según el
		 * esquema almacenado. Los bloques que no sean series son omitidos.
		 */

		//  Miro si son fijas o variables
		int posVLS_SIZE = 0;
		if (!seriesFixedLength) {
			posVLS_SIZE = learningSet.getAttribute("VLS_SIZE").getTableIndex();
		}

		int numBlocks = 0;
		/* 
		 * COMENTO LA BUSQUEDA DE BLOQUES QUE SEAN SERIES PARA PERMITIR
		 * LA DISCRETIZACIÖN DE ATRIBUTOS 
		 * 
		 * 
		 for (int currBlock = 0; currBlock < lBlocks.size(); currBlock++) {
			if (((BlockData) lBlocks.get(currBlock)).isSeries)
				numBlocks++;
		}*/
		numBlocks=lBlocks.size(); 
		
		double[][] v = new double[numBlocks][];

		numBlocks = 0;
		for (int currBlock = 0; currBlock < lBlocks.size(); currBlock++) {
			if (((BlockData) lBlocks.get(currBlock)).isSeries) {
				int longitud = 0;
				int inicio = ((BlockData) lBlocks.get(currBlock)).inicio;
				int fin = 0;
				if (seriesFixedLength) {
					fin = ((BlockData) lBlocks.get(currBlock)).fin;
					longitud = fin - inicio + 1;
				} else {
					longitud = (int) example.getValue(posVLS_SIZE);
					fin = inicio + longitud - 1;
				}

				double[] valores = new double[longitud];
				for (int i = 0; i < longitud; i++) {
					valores[i] = example.getValue(i + inicio);
				}
				double[] val2 = DiscretizationMethods.Preprocess_Series(
						valores, seriesFixedLength, Nor, Tip, Dif);
				longitud = val2.length;
				v[numBlocks] = new double[longitud];
				for (int atriact = 0; atriact < longitud; atriact++) {
					double valor = val2[atriact];
					if (discMethod ==  DiscretizationMethods.NONE )
						v[numBlocks][atriact] = valor;
					else{
					String c = new String(Discretiza(valor,
							esqDiscre[RelAttBlck[atriact + inicio]]));
					int mapeo = ((Attribute) learningSet.getAttribute(atriact + inicio))
					.mapString(c);
					v[numBlocks][atriact] = (double) mapeo;
					}
				}
				lorden.add(new Integer(currBlock));
				numBlocks++;
			}
			//TODO ¿Y si no es serie? ¿ pero es un atributo que hay que discretizar?
			//TODO Se supone que es un atributo que hay que discretizar, no se cumprueba si es así.
			else{
				int inicio = ((BlockData) lBlocks.get(currBlock)).inicio;
				int longitud=1;
				
				double valor = example.getValue( inicio);
				v[numBlocks] = new double[longitud];
				
				if (discMethod ==  DiscretizationMethods.NONE )
					v[numBlocks][0] = valor;
				else{
					String c = new String(Discretiza(valor,
							esqDiscre[currBlock ]));
					int mapeo = ((Attribute) learningSet.getAttribute(inicio))
					.mapString(c);
					v[numBlocks][0] = (double) mapeo;
				}
				lorden.add(new Integer(currBlock));
				numBlocks++;
			}
		}
		
		return v;
	}

	// END CLASS
}

