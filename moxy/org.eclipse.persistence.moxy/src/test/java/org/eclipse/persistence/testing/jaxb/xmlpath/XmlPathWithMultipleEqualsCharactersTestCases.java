/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 05 November 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlpath;

import java.io.InputStream;
import java.util.HashMap;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;

import junit.framework.TestCase;

public class XmlPathWithMultipleEqualsCharactersTestCases extends TestCase {

    private final static String INSTANCE_DOC = "org/eclipse/persistence/testing/jaxb/xmlpath/atom.xml";
    private final static String BINDINGS_DOC = "org/eclipse/persistence/testing/jaxb/xmlpath/atom-bindings.xml";

    public void testOneEntry() throws Exception {
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { AtomEntriesOne.class }, getProperties());
        Unmarshaller u = ctx.createUnmarshaller();
        InputStream is = ClassLoader.getSystemResourceAsStream(INSTANCE_DOC);
        AtomEntriesOne o = (AtomEntriesOne) u.unmarshal(is);
        assertEquals("Incorrect number of entries returned according to XPath.", 1, o.entries.size());
    }

    public void testTwoEntries() throws Exception {
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { AtomEntriesTwo.class }, getProperties());
        Unmarshaller u = ctx.createUnmarshaller();
        InputStream is = ClassLoader.getSystemResourceAsStream(INSTANCE_DOC);
        AtomEntriesTwo o = (AtomEntriesTwo) u.unmarshal(is);
        assertEquals("Incorrect number of entries returned according to XPath.", 2, o.entries.size());
    }

    public void testThreeEntries() throws Exception {
        JAXBContext ctx = JAXBContextFactory.createContext(new Class[] { AtomEntriesThree.class }, getProperties());
        Unmarshaller u = ctx.createUnmarshaller();
        InputStream is = ClassLoader.getSystemResourceAsStream(INSTANCE_DOC);
        AtomEntriesThree o = (AtomEntriesThree) u.unmarshal(is);
        assertEquals("Incorrect number of entries returned according to XPath.", 3, o.entries.size());
    }

    public XmlPathWithMultipleEqualsCharactersTestCases(String name) throws Exception {
        super(name);
    }

    public String getName() {
        return "XmlPathWithMultipleEqualsCharactersTestCases: " + super.getName();
    }

    private HashMap<String, Object> getProperties() {
        InputStream iStream = ClassLoader.getSystemResourceAsStream(BINDINGS_DOC);

        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, iStream);

        return properties;
    }

}
