/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2012-03-27 - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * <p>Tests the following:</p>
 *
 * <ul>
 * <li>Ensure that a transient class will not throw a 'transient in propOrder' error.</li>
 * </ul>
 */
public class TransientClassTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmltransient/transientclass.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmltransient/transientclass.json";
    private static final String TRANSIENT_CLASS_BINDINGS = "org/eclipse/persistence/testing/jaxb/annotations/xmltransient/transientclass-bindings.xml";

    public TransientClassTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { TransientClass.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        TransientClass c = new TransientClass();
        c.setB001(true);
        c.setS001("test-string");

        return c;
    }

    @Override
    protected Map getProperties() {
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream(TRANSIENT_CLASS_BINDINGS);
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.annotations.xmltransient", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        return properties;
    }

    public void testTransientClassNoOverride() throws JAXBException {
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { TransientClass.class }, null);
        // With no overrides, no errors should be thrown.

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Exception caughtEx = null;
        try {
            // Transient class, no descriptor should be generated.
            // Should get No Descriptor exception
            ctx.createMarshaller().marshal(getControlObject(), out);
        } catch (Exception e) {
            caughtEx = e;
        }

        assertNotNull("Did not catch an exception as expected.", caughtEx);

        XMLMarshalException elEx = (XMLMarshalException) caughtEx.getCause().getCause();
        assertEquals("Unexpected error code.", elEx.getErrorCode(), XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT);
    }

}
