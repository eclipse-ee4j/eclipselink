/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan & Denise Smith = 2.1 - Initial contribution
 ******************************************************************************/  
package org.eclipse.persistence.internal.jaxb.many;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.mappings.AttributeAccessor;

/**
* AttributeAccessor used in conjunction with an XMLCompositeDirectCollectionMapping to enable
* support for mapping to arrays
*/
public class JAXBArrayAttributeAccessor extends AttributeAccessor {

    private AttributeAccessor nestedAccessor;
    private ContainerPolicy containerPolicy;
    private String componentClassName;
    private Class componentClass;
    private String adaptedClassName;
    private Class<? extends ManyValue> adaptedClass;
    private ClassLoader classLoader;

    public JAXBArrayAttributeAccessor(AttributeAccessor nestedAccessor, ContainerPolicy containerPolicy, ClassLoader classLoader) {
        this.nestedAccessor = nestedAccessor;
        this.containerPolicy = containerPolicy;
        this.classLoader = classLoader;
    }

    @Override
    public Object getAttributeValueFromObject(Object object) throws DescriptorException {
        Object arrayValue = nestedAccessor.getAttributeValueFromObject(object);
        if(arrayValue == null){
            return null;
        }
        int length = Array.getLength(arrayValue);
        Object results = containerPolicy.containerInstance(length);
        if(null == adaptedClass) {
            for(int x=0; x<length; x++) {
                containerPolicy.addInto(Array.get(arrayValue, x), results, null);
            }
        } else {
            for(int x=0; x<length; x++) {
                try {
                    ManyValue manyValue = (ManyValue) PrivilegedAccessHelper.newInstanceFromClass(adaptedClass);
                    manyValue.setItem(Array.get(arrayValue, x));
                    containerPolicy.addInto(manyValue, results, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return results;
    }

    @Override
    public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
        List listValue = (List) value;
        if(null == listValue || listValue.isEmpty()) {
            nestedAccessor.setAttributeValueInObject(object, null);
        }
        List<Integer> dimensionsList = new ArrayList<Integer>();
        int size = listValue.size();
        dimensionsList.add(size);
        if(size > 0) {
            Object firstItem = listValue.get(0);
            if(firstItem instanceof ManyValue) {
                getDimensions(dimensionsList, ((ManyValue) firstItem).getItem());
            }
        }
        int[] dimensions = new int[dimensionsList.size()];
        for(int x=0; x<dimensions.length; x++) {
            dimensions[x] = dimensionsList.get(x);
        }

        Object arrayValue =  Array.newInstance(componentClass, dimensions);
        Iterator<Object> collectionValueIterator = listValue.iterator();
        int index=0;
        while(collectionValueIterator.hasNext()) {
            Object next = collectionValueIterator.next();
            if(next instanceof ManyValue) {
                next = ((ManyValue) next).getItem();
            }
            Array.set(arrayValue, index++, next);
        }
        nestedAccessor.setAttributeValueInObject(object, arrayValue);
    }

    @Override
    public void initializeAttributes(Class theJavaClass) throws DescriptorException {
        nestedAccessor.initializeAttributes(theJavaClass);
        if(adaptedClass == null && adaptedClassName != null){
            try {
                adaptedClass = PrivilegedAccessHelper.getClassForName(adaptedClassName, true, classLoader);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if(componentClass == null && componentClassName != null){
            try {
                componentClass = PrivilegedAccessHelper.getClassForName(componentClassName, true, classLoader);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setAdaptedClass(Class adaptedClass) {
        this.adaptedClass = componentClass;
    }

    public void setComponentClass(Class componentClass) {
        this.componentClass = componentClass;
    }

    public void setAdaptedClassName(String adaptedClassName) {
        this.adaptedClassName = adaptedClassName;
    }

    public void setComponentClassName(String componentClassName) {
        this.componentClassName = componentClassName;
    }

    private List<Integer> getDimensions(List<Integer> dimensions, Object array) {
        dimensions.add(Array.getLength(array));
        Object nestedArray = Array.get(array, 0);
        if(nestedArray.getClass().isArray()) {
            return getDimensions(dimensions, nestedArray);
        }
        return dimensions;
    }

}