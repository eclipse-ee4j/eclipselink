/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm.unmapped;

import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.xml.sax.ContentHandler;

/**
 * <p><b>Purpose:</b>Provide an interface that can be implemented for handling
 * unmapped content during unmarshal operations with SAXPlatform.
 */
public interface UnmappedContentHandler
 <UNMARSHAL_RECORD extends UnmarshalRecord>
extends ContentHandler {

    /**
     * Set the UnmarshalRecord which gives access to mechanisms used during the
     * unmarshal process such as an Unmarshaller and a Session.
     * @param unmarshalRecord
     */
    void setUnmarshalRecord(UNMARSHAL_RECORD unmarshalRecord);
}
