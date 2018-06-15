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
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

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
