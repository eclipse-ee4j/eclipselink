/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.refresh;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
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
            JAXBHelper.getJAXBContext(jc).refreshMetadata();
        } catch(JAXBException e) {
            assertEquals(JAXBException.COULD_NOT_UNMARSHAL_METADATA, e.getErrorCode());
            return;
        }
        fail();
    }
}
