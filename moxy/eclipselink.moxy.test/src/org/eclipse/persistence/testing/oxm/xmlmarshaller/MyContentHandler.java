/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
