/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan = 2.1 - Initial implementation
package org.eclipse.persistence.internal.jaxb.many;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public abstract class ArrayValue<T> extends ManyValue<T, Object> {

    @Override
    public Object getItem() {
        if(null == adaptedValue) {
            return null;
        }

        int len = adaptedValue.size();
        Object array = Array.newInstance(containerClass(),len);
        for( int i=0; i<len; i++ ){
            Array.set(array,i,((ArrayList)adaptedValue).get(i));
        }
        return array;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public void setItem(Object array) {
        int arraySize = Array.getLength(array);
        adaptedValue = new ArrayList<T>(arraySize);
        for(int x=0; x<arraySize; x++) {
            adaptedValue.add((T) Array.get(array, x));
        }
    }

}
