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
//     Blaise Doughan = 2.1 - Initial contribution
package org.eclipse.persistence.internal.jaxb.many;

import java.lang.reflect.Array;
import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlTransient;

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
                ManyValue<?, Object> manyValue = PrivilegedAccessHelper.newInstanceFromClass(componentClass());
                manyValue.setItem(Array.get(array, x));
                adaptedValue.add((T) manyValue);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
