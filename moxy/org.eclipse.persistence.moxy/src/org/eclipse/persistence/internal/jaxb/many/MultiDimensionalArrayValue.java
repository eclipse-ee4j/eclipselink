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
*     Blaise Doughan = 2.1 - Initial contribution
******************************************************************************/
package org.eclipse.persistence.internal.jaxb.many;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

@XmlTransient
public abstract class MultiDimensionalArrayValue<T extends ManyValue<?, Object>> extends MultiDimensionalManyValue<T> {

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public Object getItem() {
        Object array = Array.newInstance(containerClass(), adaptedValue.size());
        int x=0;
        for(ManyValue<?, Object> value : adaptedValue) {
            Array.set(array, x, value.getItem());
            x++;
        }
        return array;
    }

    @Override
    public void setItem(Object array) {
        try {
            int arraySize = Array.getLength(array);
            adaptedValue = new ArrayList<T>(arraySize);
            for(int x=0; x<arraySize; x++) {
                ManyValue<?, Object> manyValue = (ManyValue<?, Object>) PrivilegedAccessHelper.newInstanceFromClass(componentClass());
                manyValue.setItem(Array.get(array, x));
                adaptedValue.add((T) manyValue);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}