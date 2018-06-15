/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.model.property.PropertyConfig;

/**
 * @see PropertyConfig
 *
 * @version 1.0a
 * @author Pascal Filion
 */
public class PropertyAdapter extends SCAdapter {
    // property change
    public static final String NAME_PROPERTY = "name";
    public static final String VALUE_PROPERTY = "value";

    /**
     * Creates a new PropertyAdapter for the specified model object.
     */
    PropertyAdapter( SCAdapter parent, PropertyConfig scConfig) {

        super( parent, scConfig);
    }

    /**
     * Creates a new PropertyAdapter.
     */
    protected PropertyAdapter( SCAdapter parent) {

        super( parent);
    }

    /**
     * Factory method for building this model.
     */
    protected Object buildModel() {

        return new PropertyConfig();
    }

    public String getName() {

        return propertyConfig().getName();
    }

    public String getValue() {

        return propertyConfig().getValue();
    }

    /**
     * Returns this Config Model Object.
     */
    final PropertyConfig propertyConfig() {

        return ( PropertyConfig)this.getModel();
    }

    public void setName( String name) {

        String oldName = getName();
        propertyConfig().setName(name);
        firePropertyChanged(NAME_PROPERTY, oldName, name);
    }

    public void setValue( String value) {

        String oldValue = getValue();
        propertyConfig().setValue(value);
        firePropertyChanged(VALUE_PROPERTY, oldValue, value);
    }

    public void toString( StringBuffer sb) {

        sb.append("name=");
        sb.append(getName());
        sb.append(", value=");
        sb.append(getValue());
    }
}
