/*******************************************************************************
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.rs.metadata.model.v2;

import javax.xml.bind.annotation.XmlElement;

/**
 * Entity resource property. Used in JPARS 2.0 metadata model.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
public class Property {
    @XmlElement
    private String type;

    @XmlElement(name = "$ref")
    private String ref;

    /** For array types **/
    @XmlElement
    private Property items;

    public Property() {
    }

    public Property(String ref) {
        this.ref = ref;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Property getItems() {
        return items;
    }

    public void setItems(Property items) {
        this.items = items;
    }
}
