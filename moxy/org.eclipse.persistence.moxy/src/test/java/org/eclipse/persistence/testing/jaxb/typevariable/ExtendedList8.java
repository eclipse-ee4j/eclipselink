/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5.2 - initial implementation
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
