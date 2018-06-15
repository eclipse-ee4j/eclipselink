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
package org.eclipse.persistence.oxm.mappings;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.oxm.record.XMLRecord;
/**
 * INTERNAL
 * All mappings which can be added to org.eclipse.persistence.oxm.XMLDescriptor must
 * implement this interface.
 *
 *@see org.eclipse.persistence.oxm.mappings
 */
public interface XMLMapping extends Mapping<AbstractSession, AttributeAccessor, ContainerPolicy, ClassDescriptor, DatabaseField, XMLRecord> {

    @Override
    public void convertClassNamesToClasses(ClassLoader classLoader);

    /**
     * INTERNAL:
     * A method that marshals a single value to the provided Record based on this mapping's
     * XPath. Used for Sequenced marshalling.
     * @param value - The value to be marshalled
     * @param record - The Record the value is being marshalled too.
     */
    @Override
    public void writeSingleValue(Object value, Object parent, XMLRecord record, AbstractSession session);

    public boolean isWriteOnly();

    public void setIsWriteOnly(boolean b);
}
