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
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="root")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmptyElementObjectRoot {

    Object xsiString;

    Object xsiInteger;

    Object xsiBoolean;

    List<Object> items = new ArrayList<Object>();

    @Override
    public boolean equals(Object object) {
        EmptyElementObjectRoot root = (EmptyElementObjectRoot) object;

        if(!xsiString.equals(root.xsiString)) {
            return false;
        }
        if(!xsiInteger.equals(root.xsiInteger)) {
            return false;
        }
        if(!xsiBoolean.equals(root.xsiBoolean)) {
            return false;
        }
        if(items.size() != root.items.size()) {
            return false;
        }
        for(int x=0; x<items.size(); x++) {
            if(!items.get(x).equals(root.items.get(x))) {
                return false;
            }
        }
        return true;
    }

}
