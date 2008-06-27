package rm.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.set.SimpleExampleSet;
import com.rapidminer.example.set.SplittedExampleSet;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.tools.att.AttributeSet;

public class CopyExampleSet extends Operator {
	
	public CopyExampleSet(OperatorDescription description){
		super(description);
	}
	
	public IOObject[] apply() throws OperatorException {

		ExampleSet sSet= (ExampleSet) getInput(ExampleSet.class);
		
		List<Attribute> lAtt = new ArrayList<Attribute>();
		AttributeSet sAtt = new AttributeSet();
		Attribute[] listaAtt = sSet.getExampleTable().getAttributes();
		
		for (int i=0;i<listaAtt.length;i++){
			lAtt.add(listaAtt[i]);
			sAtt.addAttribute(listaAtt[i]);
		}
			
		MemoryExampleTable met=new MemoryExampleTable(lAtt);
		Iterator<Example> itEx = sSet.iterator();
		while(itEx.hasNext()){
			Example ex=itEx.next();
			met.addDataRow(ex.getDataRow());
		}
		ExampleSet nSet = met.createExampleSet(sAtt);
		nSet.getAttributes().setLabel(sSet.getAttributes().getLabel());


		
		return new IOObject[] { nSet };
	}
	
	
	

	//**********************************************************************************************
	//**********************************************************************************************
	public Class[] getInputClasses() {
		return new Class[] { ExampleSet.class };
			
	}

	//**********************************************************************************************
	//**********************************************************************************************
	public Class[] getOutputClasses() {
		return new Class[] { ExampleSet.class };
	}

}
