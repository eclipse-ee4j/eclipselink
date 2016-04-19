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
import org.eclipse.persistence.json.bind.model.Property;
import org.eclipse.persistence.json.bind.model.PropertyModel;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created a class internal model.
 *
 * @author Dmitry Kornilov
 */
class ClassParser {

    public static final String IS_PREFIX = "is";

    public static final String GET_PREFIX = "get";

    public static final String SET_PREFIX = "set";

    public static final String GENERATED_PREFIX = "this$";

    /**
     * Parse class fields and getters setters. Merge to java bean like properties.
     *
     * @return model of a class
     */
    public void parseProperties(ClassModel classModel) {

        final Map<String, Property> classProperties = new HashMap<>();

        parseFields(classModel, classProperties);

        parseMethods(classModel, classProperties);

        classProperties.values().stream().forEach((property)->{
            PropertyModel propertyModel = new PropertyModel(classModel, property);
            classModel.addProperty(propertyModel);
        });

    }

    private void parseMethods(ClassModel classModel, Map<String, Property> classProperties) {
        for (Method method : classModel.getRawType().getDeclaredMethods()) {
            String name = method.getName();
            if (!name.startsWith(GET_PREFIX) && !name.startsWith(SET_PREFIX) && !name.startsWith(IS_PREFIX)) {
                continue;
            }
            final String propertyName = Introspector.decapitalize(name.substring(name.startsWith(IS_PREFIX) ? 2 : 3, name.length()));

            Property property = classProperties.get(propertyName);
            if (property == null) {
                property= new Property(propertyName, classModel);
                classProperties.put(propertyName, property);
            }

            if (name.startsWith(SET_PREFIX)) {
                property.setSetter(method);
            } else {
                property.setGetter(method);
            }
        }
    }

    private void parseFields(ClassModel classModel, Map<String, Property> classProperties) {
        for (Field field : classModel.getRawType().getDeclaredFields()) {
            final String name = field.getName();
            if (field.getName().startsWith(GENERATED_PREFIX)) {
                continue;
            }
            final Property property = new Property(name, classModel);
            property.setField(field);
            classProperties.put(name, property);
        }
    }

}
