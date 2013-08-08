/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.core.mappings.transformers;

import java.io.Serializable;

import org.eclipse.persistence.core.sessions.CoreSession;

/**
 * This interface is used by the Transformation Mapping to build the value for a
 * specific field. The user must provide implementations of this interface to the
 * Transformation Mapping.
 */
public interface CoreFieldTransformer<SESSION extends CoreSession> extends Serializable {

    /**
     * @param instance - an instance of the domain class which contains the attribute
     * @param session - the current session
     * @param fieldName - the name of the field being transformed. Used if the user wants to use this transformer for multiple fields.
     * @return - The value to be written for the field associated with this transformer
     */
    public Object buildFieldValue(Object instance, String fieldName, SESSION session);

}
