/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.6 - initial implementation
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
     * Method name should be same as the value of this field. This field is used to find method name in reflection call.
     */
    public static final String BUILD_FIELD_VALUE_METHOD = "buildFieldValue";

    /**
     * @param instance - an instance of the domain class which contains the attribute
     * @param session - the current session
     * @param fieldName - the name of the field being transformed. Used if the user wants to use this transformer for multiple fields.
     * @return - The value to be written for the field associated with this transformer
     */
    public Object buildFieldValue(Object instance, String fieldName, SESSION session);

}
