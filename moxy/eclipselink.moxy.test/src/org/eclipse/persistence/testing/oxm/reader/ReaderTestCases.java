/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
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