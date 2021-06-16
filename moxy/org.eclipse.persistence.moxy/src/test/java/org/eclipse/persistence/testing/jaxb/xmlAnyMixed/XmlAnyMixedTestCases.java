/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.jaxb.xmlAnyMixed;

import junit.framework.TestCase;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringWriter;

public class XmlAnyMixedTestCases extends TestCase {
    private static final String EXPECTED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><main><RootAnyMixed>\n" +
            "        <SomeTag>\n" +
            "            <AChildTag>\n" +
            "                <AnotherChildTag/>\n" +
            "                <AnotherChildTag/>\n" +
            "            </AChildTag>\n" +
            "        </SomeTag>\n" +
            "    </RootAnyMixed><RootAny><VeryGood><TheBest><MegaSuper/><MegaSuper/>\n" +
            "            </TheBest>\n" +
            "        </VeryGood></RootAny></main>";

    public static void main(String[] args) {
        String[] arguments = {"-c", "org.eclipse.persistence.testing.jaxb.xmlAnyMixed.XmlAnyMixedTestCases"};
        junit.textui.TestRunner.main(arguments);
    }

    public void testRoundTrip() {
        try {
            JAXBContext jc = JAXBContextFactory.createContext(new Class[]{Main.class}, null);
            Unmarshaller um = jc.createUnmarshaller();
            Object o = um.unmarshal(Thread.currentThread().getContextClassLoader().getResource("org/eclipse/persistence/testing/jaxb/xmlAnyMixed/rootAnyMixed.xml"));
            Marshaller m = jc.createMarshaller();
            StringWriter sw = new StringWriter();
            m.marshal(o, sw);
            assertEquals(EXPECTED, sw.toString());
        } catch (JAXBException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
