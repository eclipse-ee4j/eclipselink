/*******************************************************************************
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.oxm.record.deferred;

import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.xml.sax.SAXException;

/**
 * <p><b>Purpose</b>: Class to represent the skippedEntity event
 * <p><b>Responsibilities</b>:<ul>
 * <li> Execute the skippedEntity event on the given unmarshalRecord with the specified arguments
 * </ul>
 */
public class SkippedEntityEvent extends SAXEvent {
    private String name;

    public SkippedEntityEvent(String theName) {
        name = theName;
    }

    @Override
    public void processEvent(UnmarshalRecord unmarshalRecord) throws SAXException {
        unmarshalRecord.skippedEntity(name);
    }
}
