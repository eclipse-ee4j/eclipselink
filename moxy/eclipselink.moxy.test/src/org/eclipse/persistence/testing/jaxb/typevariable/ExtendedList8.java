/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.typevariable;

import java.util.ArrayList;

public class ExtendedList8<E extends ExtendedList8Interface> extends ArrayList<E> {

    @Override
    public boolean equals(Object o) {
        if(null == o || o.getClass() != this.getClass()) {
            return false;
        }
        ExtendedList8<E> test = (ExtendedList8<E>) o;
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