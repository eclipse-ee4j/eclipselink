/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.FieldModel;

import java.lang.reflect.Field;

/**
 * Created a class internal model.
 *
 * @author Dmitry Kornilov
 */
class ClassParser {
    public ClassModel parse(Class clazz) {
        final ClassModel classModel = new ClassModel(clazz);

        // Fields
        for (Field field : clazz.getDeclaredFields()) {
            final FieldModel fieldModel = parseField(classModel, field);
            if (fieldModel != null) {
                classModel.getFieldModels().add(fieldModel);
            }
        }

        return classModel;
    }

    private FieldModel parseField(ClassModel classModel, Field field) {
        if (field.getName().startsWith("this$")) {
            return null;
        }
        return new FieldModel(classModel, field.getName(), field.getType());
    }
}
