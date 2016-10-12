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
package org.eclipse.persistence.json.bind.model;

import org.eclipse.persistence.json.bind.internal.naming.CaseInsensitiveStrategy;

import javax.json.bind.config.PropertyNamingStrategy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A model for Java class.
 *
 * @author Dmitry Kornilov
 */
public class ClassModel {

    private final Class<?> clazz;

    private final ClassCustomization classCustomization;

    private final ClassModel parentClassModel;

    /**
     * A map of all class properties, including properties from superclasses. Used to access by name.
     */
    private Map<String, PropertyModel> properties;

    /**
     * Sorted properties according to sorting strategy. Used for serialization property ordering.
     */
    private PropertyModel[] sortedProperties;

    private final PropertyNamingStrategy propertyNamingStrategy;

    /**
     * Gets a property model by default (non customized) name
     * @param name a name as parsed from field / getter / setter without annotation customizing
     * @return property model
     */
    public PropertyModel getPropertyModel(String name) {
        return properties.get(name);
    }

    /**
     * Create instance of class model.
     * @param clazz class to model onto
     * @param customization customization of the class parsed from annotations
     * @param parentClassModel class model of parent class
     * @param propertyNamingStrategy property naming strategy
     */
    public ClassModel(Class<?> clazz, ClassCustomization customization, ClassModel parentClassModel, PropertyNamingStrategy propertyNamingStrategy) {
        this.clazz = clazz;
        this.classCustomization = customization;
        this.parentClassModel = parentClassModel;
        this.propertyNamingStrategy = propertyNamingStrategy;
    }

    /**
     * Search for field in this class model and superclasses of its class.
     * @param jsonReadName name as it appears in JSON during reading.
     * @return PropertyModel if found.
     */
    public PropertyModel findPropertyModelByJsonReadName(String jsonReadName) {
        Objects.requireNonNull(jsonReadName);
        return searchProperty(this, jsonReadName);
    }

    private PropertyModel searchProperty(ClassModel classModel, String jsonReadName) {
        //Standard javabean properties without overridden name (most of the cases)
        final PropertyModel result = classModel.getPropertyModel(jsonReadName);
        if (result != null && result.getPropertyName().equals(result.getCustomization().getJsonReadName())) {
            return result;
        }
        //Search for overridden name on setter with @JsonbProperty annotation
        for (PropertyModel propertyModel : properties.values()) {
            if (equalsReadName(jsonReadName, propertyModel)) {
                return propertyModel;
            }
        }
        //property not found
        return null;
    }

    /**
     * Check if name is equal according to property strategy. In case of {@link CaseInsensitiveStrategy} ignore case.
     * User can provide own strategy implementation, cast to custom interface is not an option.
     */
    private boolean equalsReadName(String jsonName, PropertyModel propertyModel) {
        final String propertyReadName = propertyModel.getReadName();
        if (propertyNamingStrategy instanceof CaseInsensitiveStrategy) {
            return jsonName.equalsIgnoreCase(propertyReadName);
        }
        return jsonName.equalsIgnoreCase(propertyReadName);
    }

    public ClassCustomization getCustomization() {
        return classCustomization;
    }

    public Class<?> getType() {
        return clazz;
    }

    /**
     * Introspected customization for a class.
     * @return immutable class customization.
     */
    public ClassCustomization getClassCustomization() {
        return classCustomization;
    }

    /**
     * Class model of parent class if present.
     * @return class model of a parent
     */
    public ClassModel getParentClassModel() {
        return parentClassModel;
    }

    /**
     * Get sorted class properties copy, combination of field and its getter / setter, javabeans alike.
     * @return sorted class properties.
     */
    public PropertyModel[] getSortedProperties() {
        return sortedProperties;
    }

    public void setProperties(List<PropertyModel> parsedProperties) {
        sortedProperties = parsedProperties.toArray(new PropertyModel[]{});
        this.properties = parsedProperties.stream().collect(Collectors.toMap(PropertyModel::getPropertyName, (mod) -> mod));
    }

    /**
     * Get class properties copy, combination of field and its getter / setter, javabeans alike.
     * @return class properties.
     */
    public Map<String, PropertyModel> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
}
