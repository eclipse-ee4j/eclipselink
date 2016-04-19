/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p>
 * Contributors:
 * Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.model.ClassModel;

import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JSONB mappingContext. Created once per {@link javax.json.bind.Jsonb} instance. Represents a global scope.
 * Holds internal model.
 *
 * TODO make mapping context be shared cache between threads working with same payload classes
 *
 * Thread safe
 *
 * @author Dmitry Kornilov
 * @author Roman Grigoriadi
 */
public class MappingContext {
    private final ConcurrentHashMap<Class<?>, ClassModel> classes = new ConcurrentHashMap<>();
    private final ClassParser classParser = new ClassParser();

    /**
     * Search for class model.
     * Parse class and create one if not found.
     * @param clazz clazz to search by or parse, not null.
     */
    public void parseClassModel(Class<?> clazz) {
        final Stack<Class> newClassModels = new Stack<>();
        for (Class classToParse = clazz; classToParse != Object.class; classToParse = classToParse.getSuperclass()) {
            newClassModels.push(classToParse);
        }

        while (!newClassModels.empty()) {
            Class toParse = newClassModels.pop();
            classes.computeIfAbsent(toParse, aClass -> {
                final ClassModel classModel = new ClassModel(aClass);
                classParser.parseProperties(classModel);
                return  classModel;
            });
        }
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
