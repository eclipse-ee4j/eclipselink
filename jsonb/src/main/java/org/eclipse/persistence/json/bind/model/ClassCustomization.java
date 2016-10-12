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
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.model;

/**
 * Customization, which could be applied on a class or package level.
 *
 * @author Roman Grigoriadi
 */
public class ClassCustomization extends Customization {

    private final JsonbCreator creator;

    private String[] propertyOrder;

    /**
     * Copies properties from builder an creates immutable instance.
     *
     * @param builder not null
     */
    ClassCustomization(CustomizationBuilder builder) {
        super(builder);
        this.creator = builder.getCreator();
        this.propertyOrder = builder.getPropertyOrder();
    }

    /**
     * Copy constructor.
     *
     * @param other other customizaton instance
     */
    public ClassCustomization(ClassCustomization other) {
        super(other);
        this.creator = other.getCreator();
        this.propertyOrder = other.getPropertyOrder();
    }

    public JsonbCreator getCreator() {
        return creator;
    }

    /**
     * Names of properties to sort with.
     *
     * @return sorted names of properties
     */
    public String[] getPropertyOrder() {
        return propertyOrder;
    }

    public void setPropertyOrder(String[] propertyOrder) {
        this.propertyOrder = propertyOrder;
    }
}
