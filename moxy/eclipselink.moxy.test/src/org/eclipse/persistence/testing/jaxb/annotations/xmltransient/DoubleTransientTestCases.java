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
 *     Rick Barkhouse - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

/**
 * <p>Tests the following:</p>
 * 
 * <ul>
 * <li>Ensure that having both a field and method annotated with @XmlTransient does not throw a duplicate property error.</li>
 * <li>Ensure that the property that is maintained is the one that matches the XmlAccessType (in this case, the method).</li>
 * </ul>
 */
public class DoubleTransientTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmltransient/doubletransient.xml";
    private static final String BINDINGS = "org/eclipse/persistence/testing/jaxb/annotations/xmltransient/override-bindings.xml";

    public DoubleTransientTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { DoubleTransient.class });
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        return new DoubleTransient();
    }

    @Override
    protected Map getProperties() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(BINDINGS);
        HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
        metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.annotations.xmltransient", new StreamSource(inputStream));
        Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);

        return properties;
    }

}