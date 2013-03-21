/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
    public String getEncoding();

    public void setEncoding(String encoding);

    public boolean isFormattedOutput();

    public void setFormattedOutput(boolean shouldFormat);

    public boolean isFragment();

    public void setFragment(boolean fragment);

    public String getVersion();

    public void setVersion(String version);

    public void transform(Node sourceNode, OutputStream resultOutputStream) throws XMLPlatformException;

    public void transform(Node sourceNode, ContentHandler resultContentHandler) throws XMLPlatformException;

    public void transform(Node sourceNode, Result result) throws XMLPlatformException;

    public void transform(Node sourceNode, Writer resultWriter) throws XMLPlatformException;

    public void transform(Source source, Result result) throws XMLPlatformException;

    public void transform(Document sourceDocument, Node resultParentNode, URL stylesheet) throws XMLPlatformException;
}
