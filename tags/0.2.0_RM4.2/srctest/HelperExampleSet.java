package srctest;

import java.util.Iterator;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DoubleArrayDataRow;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.tools.Ontology;


public class HelperExampleSet {

	
	/**
	 * Añade un atributo sencillo a la lista indicada. Como no es una serie se ignora el número de bloque.
	 * @param lAtt		Lista de atributos
	 * @param nombre	Nombre del atributo a añadir
	 * @param tipo		Tipo del atributo
	 */
	public static void addAttribute(List<Attribute> lAtt,String nombre,String tipo){
		Attribute att=null;
		if (tipo.equalsIgnoreCase("REAL"))
		   att=AttributeFactory.createAttribute(nombre,Ontology.REAL);
		if (tipo.equalsIgnoreCase("ENTERO"))
		   att=AttributeFactory.createAttribute(nombre,Ontology.INTEGER);
		if (tipo.equalsIgnoreCase("NOMINAL"))
			att=AttributeFactory.createAttribute(nombre,Ontology.NOMINAL);
		lAtt.add(att);
	}
	
	/**
	 * Crea los atributos necesarios para representar una serie en un conjunto de datos
	 * indicando
	 * @param lAtt			Lista de atributos existente sobre la que se añadirán los nuevos
	 * @param nombre		nombre base de los atributos de la serie, para cada atributo se añadirá un número incremental 
	 * @param tipo			Tipo del attributo REAL, ENTERO y NOMINAL
	 * @param longitud		Número de elementos en la serie
	 * @param num_bloque  	Número de bloque que identifica la serie.
	 */
	public static void addSeries(List<Attribute> lAtt,String nombre,String tipo,int longitud, int num_bloque) throws UnknownError{

		if (lAtt==null){
			throw new UnknownError("La lista de Attributos es nula");
		}
			
		Attribute att=null;
		//TODO: Control de los valores desconocidos tanto en el caso de atributos numéricos como nominales
		for (int i=0; i<longitud;i++){
			if (tipo.equalsIgnoreCase("REAL"))
				att=AttributeFactory.createAttribute(nombre+(i+1),Ontology.REAL);
			if (tipo.equalsIgnoreCase("ENTERO"))
				att=AttributeFactory.createAttribute(nombre+(i+1),Ontology.INTEGER);
			if (tipo.equalsIgnoreCase("NOMINAL"))
				att=AttributeFactory.createAttribute(nombre+(i+1),Ontology.NOMINAL);

			
			// TODO: Revisar blockNumber 
			// att.setBlockNumber(num_bloque);
			
			
			
			if (i==0)
				att.setBlockType(Ontology.VALUE_SERIES_START);
			else if (i==longitud-1)
				att.setBlockType(Ontology.VALUE_SERIES_END);
			else
				att.setBlockType(Ontology.VALUE_SERIES);
			
			lAtt.add(att);
		}
		
	}
	
	

	/**
	 * Crea un ExampleSet con los parámetros indicados
	 * @param lAtt 		Lista de atributos (Attibute)
	 * @param label		Nombre del campo que es la etiqueta de cada ejemplo
	 * @param valores	Matriz con los valores a cargar en cada ejemplo
	 * @return ExampleSet
	 */
	public static  ExampleSet createExampleSet(List<Attribute> lAtt, String label, Object[][] valores){
		
		MemoryExampleTable tabla=new MemoryExampleTable(lAtt);

		int posLabel=-1;
		Iterator itAtt;
		for (int fila=0;fila<valores.length; fila++){
			itAtt =  lAtt.iterator();

			double[] data=new double[lAtt.size()];
			
			while (itAtt.hasNext()){
				Attribute att=(Attribute) itAtt.next();
				// ya tengo el attributo actual
				if ((att.getValueType()==Ontology.REAL)||(att.getValueType()==Ontology.INTEGER)) {
					int pos=att.getTableIndex();
					data[pos]=((Double)valores[fila][att.getTableIndex()]).doubleValue();
				}
				if (att.getValueType()==Ontology.NOMINAL){
					data[att.getTableIndex()]=att.getMapping().mapString((String)valores[fila][att.getTableIndex()]);
				}
			}
			tabla.addDataRow(new DoubleArrayDataRow(data));
		}
		
		// buscar la etiqueta 
		itAtt =  lAtt.iterator();
		
		while (itAtt.hasNext()){
			Attribute att=(Attribute) itAtt.next();
			if (att.getName().equalsIgnoreCase(label)){ // No sé como se comprueba si por tipo o por tipo de bloque
				posLabel=att.getTableIndex();
				break;
			}
		}
		System.out.println("Etiqueta de clase: "+label+" Valor de posLabel: "+posLabel);
		
		return tabla.createExampleSet(lAtt.get(posLabel));
		// return tabla.createCompleteExampleSet((Attribute)lAtt.get(posLabel),null,null,null);
	}
}
