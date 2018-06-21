/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.deferred;

import org.eclipse.persistence.internal.oxm.record.deferred.DeferredContentHandler;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class TestDeferredContentHandler extends DeferredContentHandler {
    private ContentHandler originalHandler;
    private XMLReader reader;
    public int PROCESS_EMPTY = 0;
    public int PROCESS_EMPTY_WITH_ATTRIBUTES = 0;
    public int PROCESS_SIMPLE_ELEMENT = 0;
    public int PROCESS_COMPLEX_ELEMENT = 0;

    public TestDeferredContentHandler(UnmarshalRecord parentRecord, XMLReader theReader, ContentHandler handler) {
        super(parentRecord);
        originalHandler = handler;
        reader = theReader;
    }

    protected void processEmptyElement() throws SAXException {
        PROCESS_EMPTY++;
        endProcessing();
    }

    @Override
    protected void processEmptyElementWithAttributes() throws SAXException {
        PROCESS_EMPTY_WITH_ATTRIBUTES++;
        endProcessing();
    }

    protected void processComplexElement() throws SAXException {
        PROCESS_COMPLEX_ELEMENT++;
        endProcessing();
    }

    protected void processSimpleElement() throws SAXException {
        PROCESS_SIMPLE_ELEMENT++;
        endProcessing();
    }

    private void endProcessing() {
        reader.setContentHandler(originalHandler);
    }
}
