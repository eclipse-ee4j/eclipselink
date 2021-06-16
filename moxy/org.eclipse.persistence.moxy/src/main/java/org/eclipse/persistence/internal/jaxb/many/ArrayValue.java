/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Blaise Doughan = 2.1 - Initial implementation
package org.eclipse.persistence.internal.jaxb.many;

import java.lang.reflect.Array;
import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlTransient;

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
