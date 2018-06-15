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
package org.eclipse.persistence.mappings.transformers;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.core.mappings.transformers.CoreFieldTransformer;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;

/**
 * PUBLIC:
 * This interface is used by the Transformation Mapping to build the value for a
 * specific field. The user must provide implementations of this interface to the
 * Transformation Mapping.
 * @author  mmacivor
 * @since   10.1.3
 */
public interface FieldTransformer extends CoreFieldTransformer<Session> {

    /**
     * Initialize this transformer. Only required if the user needs some special
     * information from the mapping in order to do the transformation
     * @param mapping - the mapping this transformer is associated with.
     */
    public void initialize(AbstractTransformationMapping mapping);

    /**
     * @param instance - an instance of the domain class which contains the attribute
     * @param session - the current session
     * @param fieldName - the name of the field being transformed. Used if the user wants to use this transformer for multiple fields.
     * @return - The value to be written for the field associated with this transformer
     */
    @Override
    public Object buildFieldValue(Object instance, String fieldName, Session session);
}
