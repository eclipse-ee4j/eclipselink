/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.schema.model;

public class Include {
    private String schemaLocation;
    private String id;
    private Schema schema;

    public Include() {
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Schema getSchema() {
        return schema;
    }
}