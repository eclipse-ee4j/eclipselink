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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.core.descriptors;

import org.eclipse.persistence.core.mappings.CoreMapping;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractRecord;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.oxm.XMLContext;

public abstract class CoreObjectBuilder<
    ABSTRACT_RECORD extends CoreAbstractRecord,
    ABSTRACT_SESSION extends CoreAbstractSession,
    FIELD extends CoreField,
    MAPPING extends CoreMapping> {

    /**
     * Return a new instance of the receiver's javaClass.
     */
    public abstract Object buildNewInstance();

    /**
     * Create a new row/record for the object builder.
     * This allows subclasses to define different record types.
     */
    public abstract ABSTRACT_RECORD createRecord(ABSTRACT_SESSION session);

    /**
     * Create a new row/record from XMLContext.
     */
    public abstract ABSTRACT_RECORD createRecordFromXMLContext(XMLContext context);

    /**
     * Extract primary key attribute values from the domainObject.
     */
    public abstract Object extractPrimaryKeyFromObject(Object domainObject, ABSTRACT_SESSION session);

    /**
     * Return the mapping for the specified field.
     */
    public abstract MAPPING getMappingForField(FIELD field);

}
