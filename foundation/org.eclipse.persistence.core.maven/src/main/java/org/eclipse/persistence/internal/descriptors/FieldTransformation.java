/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.mappings.transformers.*;
import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * INTERNAL:
 * This class is used to store the field transformations on the TransformationMapping.
 * The transformation can be done either using a field transformer object or a method
 * on the domain object. These cases are reflected by the subclasses
 * @author  mmacivor
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public abstract class FieldTransformation implements java.io.Serializable {
    public DatabaseField field;

    public DatabaseField getField() {
        return field;
    }

    public void setField(DatabaseField newField) {
        field = newField;
    }

    public String getFieldName() {
        return field.getQualifiedName();
    }

    public void setFieldName(String name) {
        field = new DatabaseField(name);
    }

    public abstract FieldTransformer buildTransformer() throws Exception;
}
