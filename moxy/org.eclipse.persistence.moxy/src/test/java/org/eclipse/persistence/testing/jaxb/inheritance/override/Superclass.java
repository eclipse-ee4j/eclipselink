/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
