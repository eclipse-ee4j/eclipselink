/*******************************************************************************
* Copyright (c) 1998, 2008 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - June 09/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.internal.oxm.stax;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stax.StAXSource;

/**
 * INTERNAL
 * <p><b>Purpose:</b>Provide a StAXSource subclass which wraps the XMLStreamReader from 
 * another source in an XMLStreamReaderWrapper. Used to support transformations from StAX
 * sources.
 * @author mmacivor
 *
 */
public class XMLStreamSourceWrapper extends StAXSource {

	public XMLStreamSourceWrapper(StAXSource wrappedSource) throws XMLStreamException {
		super(new XMLStreamReaderWrapper(wrappedSource.getXMLStreamReader()));
	}
}
