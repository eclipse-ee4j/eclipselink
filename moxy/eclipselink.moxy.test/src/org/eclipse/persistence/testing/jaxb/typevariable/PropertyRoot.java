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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class PropertyRoot<FOO extends Foo, BAR extends Bar> {

    public FOO foo;
    public List<BAR> bar = new ArrayList<BAR>();

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }

        PropertyRoot test = (PropertyRoot) obj;
        if(null == foo) {
            if(null != test.foo) {
                return false;
            }
        } else {
            if(!foo.equals(test.foo)) {
                return false;
            }
        }
        return equals(bar, test.bar);
    }

    private boolean equals(List<BAR> control, List<BAR> test) {
        if(control == test) {
            return true;
        }
        if(null == control || null == test) {
            return false;
        }
        if(control.size() != test.size()) {
            return false;
        }
        for(int x=0; x<control.size(); x++) {
            if(!control.get(x).equals(test.get(x))) {
                return false;
            }
        }
        return true;
    }

}
