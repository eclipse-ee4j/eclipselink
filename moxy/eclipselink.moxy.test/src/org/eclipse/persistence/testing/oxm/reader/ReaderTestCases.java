/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.2 - initial implementation
package org.eclipse.persistence.testing.oxm.reader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.oxm.OXTestCase;

public class ReaderTestCases extends OXTestCase {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/reader/input.xml";

    protected InputStream xmlInputStream;

    public ReaderTestCases(String name) {
        super(name);
    }

    protected List<Event> getControlEvents() {
        List<Event> controlEvents = new ArrayList<Event>();
        controlEvents.add(new StartDocumentEvent());
        controlEvents.add(new StartPrefixMappingEvent("", "default"));
        controlEvents.add(new StartElementEvent("default", "e1", "e1"));
        controlEvents.add(new StartPrefixMappingEvent("ns1", "urn:a"));
        controlEvents.add(new StartElementEvent("urn:a", "e2", "ns1:e2"));
        controlEvents.add(new CharactersEvent("A"));
        controlEvents.add(new EndElementEvent("urn:a", "e2", "ns1:e2"));
        controlEvents.add(new EndPrefixMappingEvent("ns1"));
        controlEvents.add(new EndElementEvent("default", "e1", "e1"));
        controlEvents.add(new EndPrefixMappingEvent(""));
        controlEvents.add(new EndDocumentEvent());
        return controlEvents;
    }

    @Override
    protected void setUp() throws Exception {
        xmlInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_RESOURCE);
    }

    @Override
    protected void tearDown() throws Exception {
        xmlInputStream.close();
    }

}
