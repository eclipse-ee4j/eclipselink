/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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

    @Override
    protected Type getTypeToUnmarshalTo() throws Exception {
        if(typeToUnmarshalTo == null){

        Type listOfInts = new ParameterizedType() {
              Type[] typeArgs = {int.class};
              @Override
              public Type[] getActualTypeArguments() { return typeArgs;}
              @Override
              public Type getOwnerType() { return null; }
              @Override
              public Type getRawType() { return List.class; }
            };
            typeToUnmarshalTo = listOfInts;
        }
        return typeToUnmarshalTo;
    }

    @Override
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
