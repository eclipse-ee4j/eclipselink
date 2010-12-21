/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2.2 - initial implementation
 ******************************************************************************/
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