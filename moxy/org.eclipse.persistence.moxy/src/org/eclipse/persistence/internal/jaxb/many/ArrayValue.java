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
*     bdoughan - April 22/2010 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.jaxb.many;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import org.eclipse.persistence.internal.jaxb.many.ManyValue;

@XmlTransient
public abstract class ArrayValue extends ManyValue<Object> {

    protected List<Object> adaptedValue;

    public ArrayValue() {
        adaptedValue = new ArrayList<Object>();
    }

    protected abstract Class<?> componentClass();

    @Override
    @XmlTransient
    public Object getItem() {
        int size = adaptedValue.size();
        Object array = Array.newInstance(componentClass(), size);
        for(int x=0; x<size; x++) {
            Array.set(array, x, adaptedValue.get(x));
        }
        return array;
    }

    @Override
    @XmlTransient
    public boolean isArray() {
        return true;
    }

    @Override
    public void setItem(Object array) {
        int size = Array.getLength(array);
        adaptedValue = new ArrayList<Object>(size);
        for(int x=0; x<size; x++) {
            adaptedValue.add(Array.get(array, x));
        }
    }

}
