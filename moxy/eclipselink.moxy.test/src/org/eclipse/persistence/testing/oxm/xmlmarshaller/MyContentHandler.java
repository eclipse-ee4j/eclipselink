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
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.xml.sax.SAXException;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;

public class MyContentHandler extends SAXDocumentBuilder {
    boolean startTriggered = false;
    boolean endTriggered = false;

    public MyContentHandler() {
        super();
    }

    public void startDocument() throws SAXException {
        startTriggered = true;
        super.startDocument();
    }

    public void endDocument() throws SAXException {
        endTriggered = true;
        super.endDocument();
    }

    public void setStartTriggered(boolean startTriggered) {
        this.startTriggered = startTriggered;
    }

    public boolean isStartTriggered() {
        return startTriggered;
    }

    public void setEndTriggered(boolean endTriggered) {
        this.endTriggered = endTriggered;
    }

    public boolean isEndTriggered() {
        return endTriggered;
    }
}
