/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
