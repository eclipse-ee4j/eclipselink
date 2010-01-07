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
package org.eclipse.persistence.oxm.unmapped;

import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.xml.sax.ContentHandler;

/**
 * <p><b>Purpose:</b>Provide an interface that can be implemented for handling
 * unmapped content during unmarshal operations with SAXPlatform.
 */
public interface UnmappedContentHandler extends ContentHandler {

    /**
     * Set the UnmarshalRecord which gives access to mechanisms used during the
     * unmarshal process such as an XMLUnmarshaller and a Session.
     * @param unmarshalRecord
     */
    void setUnmarshalRecord(UnmarshalRecord unmarshalRecord);
}
