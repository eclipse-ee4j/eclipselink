/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
//     EclipseLink contributors - initial API and implementation
package org.eclipse.persistence.testing.moxy.unit.jaxb;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRootElement;
import junit.framework.TestCase;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

/**
 * unmarshal(InputStream) uses the StAX factory. External entities must not be read.
 */
public class StaxInputFactorySecureTestCase extends TestCase {

    @XmlRootElement
    public static class Payload {
        public String value;
    }

    public void testUnmarshalInputStreamDoesNotResolveExternalEntity() throws Exception {
        Path secret = Files.createTempFile("eclipselink-stax", ".txt");
        Files.writeString(secret, "SECRET-MARKER");
        String xml = "<?xml version=\"1.0\"?>"
                + "<!DOCTYPE payload [<!ENTITY ext SYSTEM \"" + secret.toUri() + "\">]>"
                + "<payload><value>&ext;</value></payload>";
        jakarta.xml.bind.JAXBContext context = JAXBContextFactory.createContext(new Class<?>[] {Payload.class}, null);
        assertTrue(context instanceof JAXBContext);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        try {
            Payload payload = (Payload) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            assertFalse("external entity was resolved", payload != null && "SECRET-MARKER".equals(payload.value));
        } catch (JAXBException rejected) {
            // A factory that refuses the DTD is the expected outcome.
        } finally {
            Files.deleteIfExists(secret);
        }
    }
}
