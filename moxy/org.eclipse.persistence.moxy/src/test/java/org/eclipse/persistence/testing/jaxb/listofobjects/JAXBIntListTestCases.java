/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith  June 05, 2009 - Initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class JAXBIntListTestCases extends JAXBIntegerListTestCases {

    private Type typeToUnmarshalTo;

    public JAXBIntListTestCases(String name) throws Exception {
        super(name);
    }

    protected Type getTypeToUnmarshalTo() throws Exception {
        if(typeToUnmarshalTo == null){

        Type listOfInts = new ParameterizedType() {
              Type[] typeArgs = {int.class};
              public Type[] getActualTypeArguments() { return typeArgs;}
              public Type getOwnerType() { return null; }
              public Type getRawType() { return List.class; }
            };
            typeToUnmarshalTo = listOfInts;
        }
        return typeToUnmarshalTo;
    }

    protected Object getControlObject() {
        ArrayList<Integer> integers = new ArrayList<Integer>();
         integers.add(10);
        integers.add(20);
        integers.add(30);
        integers.add(40);

        QName qname = new QName("examplenamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class, null);
        jaxbElement.setValue(integers);

        return jaxbElement;
    }

}
