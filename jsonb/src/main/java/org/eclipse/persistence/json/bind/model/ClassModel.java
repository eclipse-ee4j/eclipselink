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
package org.eclipse.persistence.json.bind.model;

import org.eclipse.persistence.json.bind.internal.MappingContext;

import java.util.HashMap;
import java.util.Map;

/**
 * A model for Java class.
 *
 * @author Dmitry Kornilov
 */
public class ClassModel {

    private final Class<?> clazz;

    /**
     * Indicates that this class is nillable.
     */
    private boolean nillable;

    /**
     * A list of class fields.
     */
    private final Map<String, PropertyModel> fields = new HashMap<>();

    public PropertyModel getPropertyModel(String name) {
        return fields.get(name);
    }

    /**
     * Search for field in this class model and superclasses of its class.
     * @param fieldName name of field to find, not null.
     * @param mappingContext mapping context to search for superclasses in, not null.
     * @return PropertyModel if found.
     */
    public PropertyModel findPropertyModel(String fieldName, MappingContext mappingContext) {
        PropertyModel result = fields.get(fieldName);
        if (result != null) {
            return result;
        }
        return searchParents(fieldName, mappingContext);
    }

    private PropertyModel searchParents(String fieldName, MappingContext mappingContext) {
        Class superclass;
        for (superclass = clazz.getSuperclass(); superclass != null; superclass = superclass.getSuperclass()) {
            ClassModel classModel = mappingContext.getClassModel(superclass);
            if (classModel == null) {
                return null;
            }
            PropertyModel propertyModel = classModel.getPropertyModel(fieldName);
            if (propertyModel != null) {
                return propertyModel;
            }
        }
        return null;
    }

    public ClassModel(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getRawType() {
        return clazz;
    }

    public Map<String, PropertyModel> getFields() {
        return fields;
    }

}
