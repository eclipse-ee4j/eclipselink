/*******************************************************************************
* Copyright (c) 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     Blaise Doughan = 2.1 - Initial contribution
******************************************************************************/
package org.eclipse.persistence.internal.jaxb.many;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.jaxb.many.ManyValue;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

@XmlTransient
public abstract class MultiDimensionalArrayValue extends ManyValue<Object> {

    protected List<ManyValue<Object>> adaptedValue;

    public MultiDimensionalArrayValue() {
        adaptedValue = new ArrayList<ManyValue<Object>>();
    }

    public abstract Class<?> componentClass();

    public abstract Class<? extends ManyValue<Object>> adaptedClass();

    @Override
    @XmlTransient
    public Object getItem() {
        Object nestedArray = adaptedValue.get(0).getItem();
        List<Integer> dimensionsList = new ArrayList<Integer>();
        dimensionsList.add(adaptedValue.size());
        getDimensions(dimensionsList, nestedArray);
        int[] dimensions = new int[dimensionsList.size()];
        for(int x=0; x<dimensions.length; x++) {
            dimensions[x] = dimensionsList.get(x);
        }
        Object array = Array.newInstance(componentClass(), dimensions);
        Array.set(array, 0, nestedArray);
        for(int x=1,size=adaptedValue.size(); x<size; x++) {
            Array.set(array, x, ((ManyValue<Object>)adaptedValue.get(x)).getItem());
        }
        return array;
    }

    @Override
    @XmlTransient
    public boolean isArray() {
        return true;
    }

    @Override
    @XmlTransient
    public void setItem(Object array) {
        try {
            int size = Array.getLength(array);
            adaptedValue = new ArrayList<ManyValue<Object>>(size);
            for(int x=0; x<size; x++) {
                ManyValue<Object> nestedItem = (ManyValue<Object>) PrivilegedAccessHelper.newInstanceFromClass(adaptedClass());
                nestedItem.setItem(Array.get(array, x));
                adaptedValue.add(nestedItem);
            }
        } catch(Exception e) {
            XMLMarshalException.unmarshalException(e);
        }
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