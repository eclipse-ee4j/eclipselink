/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     rbarkhouse - 2.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.dynamic.util;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.mappings.transformers.FieldTransformerAdapter;
import org.eclipse.persistence.sessions.Session;

public class FirstFieldTransformer extends FieldTransformerAdapter {

    public Object buildFieldValue(Object instance, String fieldName, Session session) {
        DynamicEntity entity = (DynamicEntity) instance;
        String[] objectValue = entity.get("transform");
        return objectValue[0];
    }

}
