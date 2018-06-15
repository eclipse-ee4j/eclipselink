/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlinlinebinary;

import java.util.ArrayList;

import javax.activation.DataHandler;

import org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InlineDataHandlerCollectionTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinlinebinary/inlinedatahandlercollection.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinlinebinary/inlinedatahandlercollection.json";

    public InlineDataHandlerCollectionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = Root.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        Root obj = new Root();
        obj.data = new ArrayList<DataHandler>();
        byte[] bytes1 = {1, 2, 3, 4, 5, 6, 7};
        byte[] bytes2 = {2, 3, 4, 5, 6, 7, 8};
        byte[] bytes3 = {3, 4, 5, 6, 7, 8, 9};

        XMLBinaryDataHelper helper = XMLBinaryDataHelper.getXMLBinaryDataHelper();

        obj.data.add(helper.convertObjectToDataHandler(bytes1, null));
        obj.data.add(helper.convertObjectToDataHandler(bytes2, null));
        obj.data.add(helper.convertObjectToDataHandler(bytes3, null));

        return obj;
    }
}
