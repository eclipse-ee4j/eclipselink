/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.proporder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class OverrideSequenceTestCases extends JAXBWithJSONTestCases {

    private static final String OXM_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/proporder/overrideSequence-oxm.xml";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/proporder/all.xml";
    private static final String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/proporder/all.xsd";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/proporder/all.json";

    public OverrideSequenceTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {OverrideSequenceRoot.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Map getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>(1);
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, OXM_RESOURCE);
        return properties;
    }

    @Override
    protected OverrideSequenceRoot getControlObject() {
        OverrideSequenceRoot root = new OverrideSequenceRoot();
        root.a = "A";
        root.b = "B";
        root.c = "C";
        return root;
    }

    public void testSchemaGen() throws Exception{
        List controlSchemas = new ArrayList(1);
        InputStream is = ClassLoader.getSystemResourceAsStream(XSD_RESOURCE);
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }


}
