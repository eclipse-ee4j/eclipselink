/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-06-09 14:54:00 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.stax;

import javax.xml.transform.stax.StAXResult;

import org.eclipse.persistence.oxm.XMLMarshaller;

/**
 * <p><b>INTERNAL</b></p>
 * 
 * <p><b>Purpose:</b> Provide a StAXResult subclass which wraps the XMLStreamWriter, from
 * the provided StAXResult, in an XMLStreamWriterWrapper. Used to support transformations to StAX results.</p>
 * 
 * @author rbarkhouse
 * @since EclipseLink 2.0
 */
public class XMLStreamResultWrapper extends StAXResult {

    public XMLStreamResultWrapper(StAXResult result, XMLMarshaller xmlMarshaller) {
        super(new XMLStreamWriterWrapper(result.getXMLStreamWriter(), xmlMarshaller));
    }

}