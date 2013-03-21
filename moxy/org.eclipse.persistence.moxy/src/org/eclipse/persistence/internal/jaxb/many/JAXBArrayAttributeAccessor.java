/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.mappings.AttributeAccessor;

/**
* AttributeAccessor used in conjunction with an XMLCompositeDirectCollectionMapping to enable
* support for mapping to arrays
*/
public class JAXBArrayAttributeAccessor extends AttributeAccessor {

    private CoreAttributeAccessor nestedAccessor;
    private CoreContainerPolicy containerPolicy;
    private String componentClassName;
    private Class componentClass;
    private String adaptedClassName;
    private Class<? extends ManyValue> adaptedClass;
    private ClassLoader classLoader;

    public JAXBArrayAttributeAccessor(CoreAttributeAccessor nestedAccessor, CoreContainerPolicy containerPolicy, ClassLoader classLoader) {
        this.nestedAccessor = nestedAccessor;
        this.containerPolicy = containerPolicy;
        this.classLoader = classLoader;
    }

    @Override
    public Object getAttributeValueFromObject(Object object) throws DescriptorException {
        Object arrayValue = nestedAccessor.getAttributeValueFromObject(object);
        if (arrayValue == null) {
            return null;
        }
        int length = Array.getLength(arrayValue);
        Object results = containerPolicy.containerInstance(length);
        if (null == adaptedClass) {
            for (int x = 0; x < length; x++) {
                containerPolicy.addInto(Array.get(arrayValue, x), results, null);
            }
        } else {
            for (int x = 0; x < length; x++) {
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
        if (null == listValue || listValue.isEmpty()) {
            nestedAccessor.setAttributeValueInObject(object, null);
            return;
        }

        int[] dims = new int[1];
        int listValueSize = listValue.size();
        dims[0] = listValueSize;

        if (listValueSize > 0) {
            Object firstItem = listValue.get(0);
            if (firstItem instanceof ManyValue) {
                dims = getDimensions(dims, ((ManyValue) firstItem).getItem());
            }
        }

        Object arrayValue = Array.newInstance(componentClass, dims);
        for (int i = 0; i < listValueSize; i++) {
            Object next = listValue.get(i);
            if (next instanceof ManyValue) {
                next = ((ManyValue) next).getItem();
            }
            Array.set(arrayValue, i, next);
        }

        nestedAccessor.setAttributeValueInObject(object, arrayValue);
    }

    @Override
    public void initializeAttributes(Class theJavaClass) throws DescriptorException {
        nestedAccessor.initializeAttributes(theJavaClass);
        if (adaptedClass == null && adaptedClassName != null) {
            try {
                adaptedClass = PrivilegedAccessHelper.getClassForName(adaptedClassName, true, classLoader);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if (componentClass == null && componentClassName != null) {
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

    private int[] getDimensions(int[] dimensions, Object array) {
        int[] newDimensions = Arrays.copyOf(dimensions, dimensions.length + 1);
        newDimensions[newDimensions.length - 1] = Array.getLength(array);
        Object nestedArray = Array.get(array, 0);
        if (nestedArray.getClass().isArray()) {
            return getDimensions(newDimensions, nestedArray);
        }
        return newDimensions;
    }

    public void setNestedAccessor(AttributeAccessor a) {
        this.nestedAccessor = a;
    }

    public void setIsWriteOnly(boolean aBoolean) {
        super.setIsWriteOnly(aBoolean);
        this.nestedAccessor.setIsWriteOnly(aBoolean);
    }

    public void setIsReadOnly(boolean aBoolean) {
        super.setIsReadOnly(aBoolean);
        this.nestedAccessor.setIsReadOnly(aBoolean);
    }

}