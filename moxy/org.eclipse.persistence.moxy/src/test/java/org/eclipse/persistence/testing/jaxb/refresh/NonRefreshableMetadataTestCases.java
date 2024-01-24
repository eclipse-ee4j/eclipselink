/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.refresh;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBHelper;

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
        props.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataStream);
        JAXBContext jc = JAXBContextFactory.createContext(new Class<?>[] {Root.class}, props);

        try {
            JAXBHelper.getJAXBContext(jc).refreshMetadata();
        } catch(JAXBException e) {
            assertEquals(JAXBException.COULD_NOT_UNMARSHAL_METADATA, e.getErrorCode());
            return;
        }
        fail();
    }
}
