package test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.tools.OperatorService;

public  class HelperOperatorConstructor {

	public Method findPrivateMethod(String className,String methodName){
		
		Class toRun=null;
		
		try {
			toRun = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Method metodo=null;
		try {
			Method[] metodos=toRun.getDeclaredMethods();
			for (int i=0;i<metodos.length;i++){
				if (methodName.equals(metodos[i].getName())){
					metodo=metodos[i];
				}
			}
		}  catch (SecurityException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} 
		return metodo;
	}
	
	public Operator createOperatorInstance(String name,String className,String group){
		Operator op=null;
		try {
			OperatorDescription oD = new OperatorDescription(getClass().getClassLoader(),name,className,
					group,"Description",null,null,null);
			op = OperatorService.createOperator(oD);
		} catch (OperatorCreationException e1) {
			e1.printStackTrace();
		}
		catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return op;
	}
	
	
	public Object invokePrivateMethodOperator(String methodName,String className, String opeName, String opeDesc,
			Object[] args){
		Method metodoInvocar=this.findPrivateMethod(className, methodName);
		Operator op=this.createOperatorInstance(opeDesc, className,null);
		try {
			metodoInvocar.setAccessible(true);
			Object ret=metodoInvocar.invoke(op,args);
			return ret;
		}  catch (SecurityException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public Object invokePrivateMethodOperator(Operator op,String methodName,String className, String opeName, String opeDesc,
			Object[] args){
		Method metodoInvocar=this.findPrivateMethod(className, methodName);
		
		try {
			metodoInvocar.setAccessible(true);
			Object ret=metodoInvocar.invoke(op,args);
			return ret;
		}  catch (SecurityException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public Object invokePrivateMethodOperator(Operator op,Method metodoInvocar,Object[] args){
		try {
			metodoInvocar.setAccessible(true);
			Object ret=metodoInvocar.invoke(op,args);
			return ret;
		}  catch (SecurityException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		
		
		return null;
	}
	

	public Field findPrivateField(String className,String fieldName){
		
		Class toRun=null;
		
		try {
			toRun = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Field campo=null;
		try {
			Field[] campos=toRun.getDeclaredFields();
			for (int i=0;i<campos.length;i++){
				if (fieldName.equals(campos[i].getName())){
					campo=campos[i];
				}
			}
		}  catch (SecurityException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} 
		return campo;
	}
	
	public Object getPrivateFieldOperator(String fieldName,String className,Object obj){
		Field campo=this.findPrivateField(className, fieldName);
		try{
			campo.setAccessible(true);
			return campo.get(obj);

		}  catch (SecurityException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		return null;
	}
	
}
