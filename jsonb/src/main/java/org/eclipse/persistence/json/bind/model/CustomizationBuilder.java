/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.json.bind.internal.adapter.JsonbAdapterInfo;

/**
 * Builder for ensuring immutable state of {@link Customization} objects.
 *
 * @author Roman Grigoriadi
 */
public class CustomizationBuilder {

    private boolean nillable;

    private boolean jsonbTransient;

    private String jsonReadName;

    private String jsonWriteName;

    private JsonbAdapterInfo adapterInfo;

    /**
     * Creates customization for class properties.
     * @return class property customization
     */
    public PropertyCustomization buildPropertyCustomization() {
        return new PropertyCustomization(this);
    }

    /**
     * Creates customization for class.
     * @return class customization
     */
    public ClassCustomization buildClassCustomization() {
        return new ClassCustomization(this);
    }

    /**
     * Marshall null values to JSON.
     * @return if true marshalling null values is active
     */
    public boolean isNillable() {
        return nillable;
    }

    /**
     * Marshall null values to JSON.
     */
    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    /**
     * Skip marshalling / unmarshalling for this customization.
     * Works as java "transient" keyword.
     */
    public boolean isJsonbTransient() {
        return jsonbTransient;
    }

    /**
     * Skip marshalling / unmarshalling for this customization.
     * Works as java "transient" keyword.
     */
    public void setJsonbTransient(boolean jsonbTransient) {
        this.jsonbTransient = jsonbTransient;
    }

    /**
     * Name as appears in JSON for reading property.
     */
    public String getJsonReadName() {
        return jsonReadName;
    }

    /**
     * Name as appears in JSON for reading property.
     */
    public void setJsonReadName(String jsonReadName) {
        this.jsonReadName = jsonReadName;
    }

    /**
     * Name as should be written to JSON for marshalling.
     */
    public String getJsonWriteName() {
        return jsonWriteName;
    }

    /**
     * Name as should be written to JSON for marshalling.
     */
    public void setJsonWriteName(String jsonWriteName) {
        this.jsonWriteName = jsonWriteName;
    }

    /**
     * Adapter a class or a property of a class.
     * @return adapter
     */
    public JsonbAdapterInfo getAdapterInfo() {
        return adapterInfo;
    }

    /**
     * Adapter a class or a property of a class.
     * @param adapterInfo adapter info
     */
    public void setAdapterInfo(JsonbAdapterInfo adapterInfo) {
        this.adapterInfo = adapterInfo;
    }
}
