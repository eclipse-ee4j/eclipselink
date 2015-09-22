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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSONB mappingContext. Created once per {@link javax.json.bind.Jsonb} instance. Represents a global scope.
 * Holds internal model.
 *
 * Thread safe
 *
 * @author Dmitry Kornilov
 */
public class MappingContext {
    private final Map<Class<?>, ClassModel> classes = new ConcurrentHashMap<>();
    private final ClassParser classParser = new ClassParser();

    /**
     * Search for class model.
     * Parse class and create one if not found.
     * @param clazz clazz to search by or parse, not null.
     * @return Model of a class
     */
    public ClassModel getOrCreateClassModel(Class<?> clazz) {
        ClassModel classModel = classes.get(clazz);
        if (classModel == null) {
            classModel = classParser.parse(clazz);
            classes.put(clazz, classModel);
        }
        return classModel;
    }

    /**
     * Search for class model, without parsing if not found.
     * @param clazz clazz to search by or parse, not null.
     * @return model of a class if found.
     */
    public ClassModel getClassModel(Class<?> clazz) {
        return classes.get(clazz);
    }

}
