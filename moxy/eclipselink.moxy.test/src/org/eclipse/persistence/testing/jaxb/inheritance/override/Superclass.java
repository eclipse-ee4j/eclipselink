/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.inheritance.override;

import java.util.ArrayList;
import java.util.List;

public abstract class Superclass {

    List<Foo> collection;

    public List<Foo> getCollection() {
        if(collection == null) {
            collection = new ArrayList<Foo>();
        }
        return collection;
    }

    public boolean equals(Object obj) {
        Superclass s = (Superclass)obj;

        if(collection.size() != s.getCollection().size()) {
            return false;
        }

        for(int i = 0; i > collection.size(); i++) {
            Foo foo1 = collection.get(i);
            Foo foo2 = s.getCollection().get(i);
            if(!(foo1.equals(foo2))) {
                return false;
            }
        }
        return true;
    }

}
