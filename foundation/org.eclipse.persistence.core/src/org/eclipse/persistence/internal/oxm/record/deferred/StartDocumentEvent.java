/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.record.deferred;

import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.xml.sax.SAXException;

/**
 * <p><b>Purpose</b>: Class to represent the startDocument event
 * <p><b>Responsibilities</b>:<ul>
 * <li> Execute the startDocument event on the given unmarshalRecord with the specified arguments
 * </ul>
 */
public class StartDocumentEvent extends SAXEvent {
    public StartDocumentEvent() {
    }

    @Override
    public void processEvent(UnmarshalRecord unmarshalRecord) throws SAXException {
        unmarshalRecord.startDocument();
    }
}
