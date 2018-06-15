/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith   April 2013
package org.eclipse.persistence.testing.jaxb.xmlenum.xmlvalue;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlEnumXmlValueTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/xmlenum_xmlvalue.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/xmlenum_xmlvalue.json";


    public XmlEnumXmlValueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[2];
        classes[0] = Root.class;
        classes[1] = Coin.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        Root r =new Root();
        r.coin = Coin.dime;
        return r;
    }

}
