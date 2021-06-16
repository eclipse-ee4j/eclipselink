/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.platform.xml.xdk;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLDocumentFragment;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XMLOutputStream;
import oracle.xml.parser.v2.XMLPrintDriver;

class XDKPrintDriver extends XMLPrintDriver {
    public XDKPrintDriver(OutputStream outputStream) {
        super(outputStream);
    }

    public XDKPrintDriver(PrintWriter printWriter) {
        super(printWriter);
    }

    public boolean isFormattedOutput() {
        return out.getOutputStyle() == XMLOutputStream.PRETTY;
    }

    public void setFormattedOutput(boolean shouldFormat) {
        if (shouldFormat) {
            out.setOutputStyle(XMLOutputStream.PRETTY);
            return;
        }
        out.setOutputStyle(XMLOutputStream.COMPACT);
    }

    public void print(XMLNode xmlNode) throws IOException {
        switch (xmlNode.getNodeType()) {
        case XMLNode.DOCUMENT_NODE: {
            ((XMLDocument)xmlNode).print(this);
            break;
        }
        case XMLNode.ELEMENT_NODE: {
            this.printElement((XMLElement)xmlNode);
            break;
        }
        case XMLNode.DOCUMENT_FRAGMENT_NODE: {
            this.printDocumentFragment((XMLDocumentFragment)xmlNode);
            break;
        }
        }

    }
}
