/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.core.descriptors;

import org.eclipse.persistence.core.mappings.CoreMapping;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractRecord;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;

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
     * Extract primary key attribute values from the domainObject.
     */
    public abstract Object extractPrimaryKeyFromObject(Object domainObject, ABSTRACT_SESSION session);

    /**
     * Return the mapping for the specified field.
     */
    public abstract MAPPING getMappingForField(FIELD field);

}