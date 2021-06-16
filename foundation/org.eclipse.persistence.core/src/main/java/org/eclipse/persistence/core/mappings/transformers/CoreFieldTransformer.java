/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
    String BUILD_FIELD_VALUE_METHOD = "buildFieldValue";

    /**
     * @param instance - an instance of the domain class which contains the attribute
     * @param session - the current session
     * @param fieldName - the name of the field being transformed. Used if the user wants to use this transformer for multiple fields.
     * @return - The value to be written for the field associated with this transformer
     */
    Object buildFieldValue(Object instance, String fieldName, SESSION session);

}
