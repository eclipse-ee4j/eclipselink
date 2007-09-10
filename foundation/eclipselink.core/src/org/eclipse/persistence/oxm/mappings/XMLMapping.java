/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.mappings;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.record.XMLRecord;
/**
 * INTERNAL
 * All mappings which can be added to org.eclipse.persistence.oxm.XMLDescriptor must
 * implement this interface.
 *
 *@see org.eclipse.persistence.oxm.mappings
 */
public interface XMLMapping {
    
    /**
     * INTERNAL:
     * A method that marshals a single value to the provided Record based on this mapping's
     * XPath. Used for Sequenced marshalling.
     * @param value - The value to be marshalled
     * @param record - The Record the value is being marshalled too. 
     */
    public void writeSingleValue(Object value, Object parent, XMLRecord record, AbstractSession session);
}