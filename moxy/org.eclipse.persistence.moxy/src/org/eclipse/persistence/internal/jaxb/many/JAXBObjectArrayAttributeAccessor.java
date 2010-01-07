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

import java.lang.reflect.Array;

import java.util.Collection;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.mappings.AttributeAccessor;

/**
* AttributeAccessor used in conjunction with an XMLCompositeDirectCollectionMapping to enable
* support for mapping to non-primitive arrays  
*/
public class JAXBObjectArrayAttributeAccessor extends AttributeAccessor {
	private AttributeAccessor nestedAccessor;
	private ContainerPolicy containerPolicy;
	private String componentClassName;
	private Class componentClass;
	
	public JAXBObjectArrayAttributeAccessor(AttributeAccessor nestedAccessor, ContainerPolicy containerPolicy) {
		this.nestedAccessor = nestedAccessor;
		this.containerPolicy = containerPolicy;		
	}
	
	public Object getAttributeValueFromObject(Object object) throws DescriptorException {
		Object value = nestedAccessor.getAttributeValueFromObject(object);
		if(value == null){
            return null;
		}
		Object[] objects =(Object[])value;
		int length = objects.length;
		Object results = containerPolicy.containerInstance(length);
		
	    for(int i=0;i < length; i++){
			containerPolicy.addInto(objects[i], results, null);
		}
		return results;
	}

	public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
		Collection collectionValue = (Collection)value;
		Object[] myArray=  (Object[])Array.newInstance(getComponentClass(), collectionValue.size());		
		nestedAccessor.setAttributeValueInObject(object, collectionValue.toArray(myArray));
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
	

}
