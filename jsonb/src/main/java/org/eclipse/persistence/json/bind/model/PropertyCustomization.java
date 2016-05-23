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
 * Customization for a property of a class.
 *
 * @author Roman Grigoriadi
 */
public class PropertyCustomization extends Customization {

    private final String jsonReadName;

    private final String jsonWriteName;


    /**
     * Copies properties from builder an creates immutable instance.
     * @param builder not null
     */
    PropertyCustomization(CustomizationBuilder builder) {
        super(builder);
        this.jsonReadName = builder.getJsonReadName();
        this.jsonWriteName = builder.getJsonWriteName();
    }

    /**
     * Name as appears in JSON for reading property.
     * @return read name
     */
    public String getJsonReadName() {
        return jsonReadName;
    }

    /**
     * Name as should be written to JSON for marshalling.
     * @return write name
     */
    public String getJsonWriteName() {
        return jsonWriteName;
    }

}
