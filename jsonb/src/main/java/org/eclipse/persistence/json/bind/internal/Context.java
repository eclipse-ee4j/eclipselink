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
 * JSONB context. Created once per {@link javax.json.bind.Jsonb} instance. Represents a global scope.
 * Holds internal model.
 *
 * @author Dmitry Kornilov
 */
public class Context {
    private final Map<Class, ClassModel> classes = new ConcurrentHashMap<>();
    private final ClassParser classParser = new ClassParser();

    public ClassModel getClassModel(Class clazz) {
        if (!classes.containsKey(clazz)) {
            final ClassModel classModel = classParser.parse(clazz);
            classes.put(classModel.getType(), classModel);
        }
        return classes.get(clazz);
    }
}
