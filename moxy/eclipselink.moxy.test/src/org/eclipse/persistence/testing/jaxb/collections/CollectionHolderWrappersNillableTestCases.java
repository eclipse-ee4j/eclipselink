/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 27 January 2012 - 2.3.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.collections;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CollectionHolderWrappersNillableTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderwrappersnillable.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderwrappersnillable.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/wrappersnillable.xsd";

    public CollectionHolderWrappersNillableTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[1];
        classes[0] = CollectionHolderWrappersNillable.class;
        setClasses(classes);
    }

    @Override
    protected Object getControlObject() {
        return new CollectionHolderWrappersNillable();
    }

    public void testSchemaGen() throws Exception {
        InputStream instream = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(instream);

        testSchemaGen(controlSchemas);
    }

}
