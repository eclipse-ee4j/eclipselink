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
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.typevariable;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExtendedMap5<KEY extends Foo, VALUE extends Bar> extends LinkedHashMap<KEY, VALUE> {

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        Map<KEY, VALUE >test = (Map<KEY, VALUE>) obj;
        if(size() != test.size()) {
            return false;
        }
        for(Map.Entry<KEY, VALUE> entry : entrySet()) {
            if(!entry.getValue().equals(test.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

}
