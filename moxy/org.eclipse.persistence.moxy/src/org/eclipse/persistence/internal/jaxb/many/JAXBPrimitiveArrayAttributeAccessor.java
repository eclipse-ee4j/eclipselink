/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - Initial implementation June 12, 2009
 ******************************************************************************/  
package org.eclipse.persistence.internal.jaxb.many;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.mappings.AttributeAccessor;

/**
* AttributeAccessor used in conjunction with an XMLCompositeDirectCollectionMapping to enable
* support for mapping to primitive arrays  
*/
public class JAXBPrimitiveArrayAttributeAccessor extends AttributeAccessor {
	private AttributeAccessor nestedAccessor;
	private ContainerPolicy containerPolicy;
	private String componentClassName;
	private Class componentClass;
	
	public JAXBPrimitiveArrayAttributeAccessor(AttributeAccessor nestedAccessor, ContainerPolicy containerPolicy) {
		this.nestedAccessor = nestedAccessor;
		this.containerPolicy = containerPolicy;		
	}
	
	public Object getAttributeValueFromObject(Object object) throws DescriptorException {
		Object value = nestedAccessor.getAttributeValueFromObject(object);
		if(value == null){
            return null;
		}
		return buildCollectionFromArray(value);			
	}

	public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
		Object convertedValue = getConvertedValue(value);
		nestedAccessor.setAttributeValueInObject(object, convertedValue);	
	}
	
	public void initializeAttributes(Class theJavaClass) throws DescriptorException {
	    nestedAccessor.initializeAttributes(theJavaClass);
	    if(componentClass == null && componentClassName != null){
			componentClass = ConversionManager.getDefaultManager().convertClassNameToClass(getComponentClassName());
		}
    }
	
	public Class getComponentClass() {		
		return componentClass;
	}

	public void setComponentClass(Class componentClass) {
		this.componentClass = componentClass;
	}

	public String getComponentClassName() {
		return componentClassName;
	}

	public void setComponentClassName(String componentClassName) {
		this.componentClassName = componentClassName;
	}
	
	private Object getConvertedValue(Object collectionValue){	
		Object iterator = containerPolicy.iteratorFor(collectionValue);
		int size = containerPolicy.sizeFor(collectionValue);
		int i=0;		
		if(getComponentClass().equals(ClassConstants.PBOOLEAN)){
			boolean[] newArray = new boolean[size];
			while(containerPolicy.hasNext(iterator)){
				newArray[i++] = ((Boolean)containerPolicy.next(iterator, null)).booleanValue();
			}
            return newArray;
		}else if(getComponentClass().equals(ClassConstants.PCHAR)){
			char[] newArray = new char[size];
			while(containerPolicy.hasNext(iterator)){
				newArray[i++] = ((Character)containerPolicy.next(iterator, null)).charValue();
			}
			return newArray;
		}else if(getComponentClass().equals(ClassConstants.PFLOAT)){
			float[] newArray = new float[size];
			while(containerPolicy.hasNext(iterator)){
				newArray[i++] = ((Float)containerPolicy.next(iterator, null)).floatValue();
			}
			return newArray;			
		}else if(getComponentClass().equals(ClassConstants.PINT)){			
			int[] newArray = new int[size];
			while(containerPolicy.hasNext(iterator)){
				newArray[i++] = ((Integer)containerPolicy.next(iterator, null)).intValue();
			}
			return newArray;	
			
		}else if(getComponentClass().equals(ClassConstants.PDOUBLE)){
			double[] newArray = new double[size];
			while(containerPolicy.hasNext(iterator)){
				newArray[i++] = ((Double)containerPolicy.next(iterator, null)).doubleValue();
			}
			return newArray;				
			
		}else if(getComponentClass().equals(ClassConstants.PLONG)){
			long[] newArray = new long[size];
			while(containerPolicy.hasNext(iterator)){
				newArray[i++] = ((Long)containerPolicy.next(iterator, null)).longValue();
			}
			return newArray;			
			
		}else if(getComponentClass().equals(ClassConstants.PSHORT)){
			short[] newArray = new short[size];
			while(containerPolicy.hasNext(iterator)){
				newArray[i++] = ((Short)containerPolicy.next(iterator, null)).shortValue();
			}
			return newArray;	
			
		}
		return null;
	}
	
	private Object buildCollectionFromArray(Object arrayValue){
		if(ClassConstants.PBOOLEAN.equals(componentClass)){
			boolean[] booleanArray = (boolean[])arrayValue;
			Object results = containerPolicy.containerInstance(booleanArray.length);
			for(boolean i:booleanArray){
				containerPolicy.addInto(i, results, null);
			}
			return results;
		}else if(ClassConstants.PCHAR.equals(componentClass)){ 
			char[] charArray = (char[])arrayValue;
			Object results = containerPolicy.containerInstance(charArray.length);
			for(char i:charArray){
				containerPolicy.addInto(i, results, null);
			}
			return results;
		}else if(ClassConstants.PFLOAT.equals(componentClass)){
			float[] floatArray = (float[])arrayValue;
			Object results = containerPolicy.containerInstance(floatArray.length);
			for(float i:floatArray){
				containerPolicy.addInto(i, results, null);
			}
			return results;
		}else if(ClassConstants.PINT.equals(componentClass)){
			int[] intArray = (int[])arrayValue;
			Object results = containerPolicy.containerInstance(intArray.length);
			for(int i:intArray){
				containerPolicy.addInto(i, results, null);
			}
			return results;
		}else if(ClassConstants.PDOUBLE.equals(componentClass)){
			double[] doubleArray = (double[])arrayValue;
			Object results = containerPolicy.containerInstance(doubleArray.length);
			for(double i:doubleArray){
				containerPolicy.addInto(i, results, null);			
			}
			return results;
		}else if(ClassConstants.PLONG.equals(componentClass)){
			long[] longArray = (long[])arrayValue;
			Object results = containerPolicy.containerInstance(longArray.length);
			for(long i:longArray){
				containerPolicy.addInto(i, results, null);
			}
			return results;
		}else if(ClassConstants.PSHORT.equals(componentClass)){
			short[] shortArray = (short[])arrayValue;
			Object results = containerPolicy.containerInstance(shortArray.length);
			for(short i:shortArray){
				containerPolicy.addInto(i, results, null);
			}
			return results;
		}
		return null;
	}
	
}

