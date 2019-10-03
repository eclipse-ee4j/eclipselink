/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.xml;

import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public interface XMLTransformer {
    String getEncoding();

    void setEncoding(String encoding);

    boolean isFormattedOutput();

    void setFormattedOutput(boolean shouldFormat);

    boolean isFragment();

    void setFragment(boolean fragment);

    String getVersion();

    void setVersion(String version);

    void transform(Node sourceNode, OutputStream resultOutputStream) throws XMLPlatformException;

    void transform(Node sourceNode, ContentHandler resultContentHandler) throws XMLPlatformException;

    void transform(Node sourceNode, Result result) throws XMLPlatformException;

    void transform(Node sourceNode, Writer resultWriter) throws XMLPlatformException;

    void transform(Source source, Result result) throws XMLPlatformException;

    void transform(Document sourceDocument, Node resultParentNode, URL stylesheet) throws XMLPlatformException;
}
