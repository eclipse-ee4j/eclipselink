/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.refresh;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

public class NonRefreshableMetadataTestCases extends TestCase {

    private static final String XML_METADATA = "org/eclipse/persistence/testing/jaxb/refresh/metadata.xml";

    public NonRefreshableMetadataTestCases(String name) {
        super(name);
    }

    public void testNonRefreshableMetadata() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();

        InputStream metadataStream = classLoader.getResourceAsStream(XML_METADATA);
        Map<String, Object> props = new HashMap<String, Object>(1);
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataStream);
        JAXBContext jc = JAXBContextFactory.createContext(new Class[] {Root.class}, props);

        try {
            JAXBHelper.getJAXBContext(jc).refeshMetadata();
        } catch(JAXBException e) {
            assertEquals(JAXBException.COULD_NOT_UNMARSHAL_METADATA, e.getErrorCode());
            return;
        }
        fail();
    }
}