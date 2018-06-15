/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.typevariable;

import java.util.ArrayList;

public class ExtendedList10<A extends Number, E extends ExtendedList8Interface, C extends Number > extends ArrayList<E> {

    public  ExtendedList10(){
    }

    @Override
    public boolean equals(Object o) {
        if(null == o || o.getClass() != this.getClass()) {
            return false;
        }
        ExtendedList10<Integer, E, Float> test = (ExtendedList10<Integer, E, Float>) o;
        if(size() != test.size()) {
            return false;
        }

        for(int x=0; x<size(); x++) {
            if(!get(x).equals(test.get(x))) {
                return false;
            }
        }
        return true;
    }
}
