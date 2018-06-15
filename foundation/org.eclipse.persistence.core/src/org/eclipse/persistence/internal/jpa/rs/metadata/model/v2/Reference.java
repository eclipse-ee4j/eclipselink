/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.internal.jpa.rs.metadata.model.v2;

import javax.xml.bind.annotation.XmlElement;

/**
 * Reference to REST resource. Used in JPARS 2.0 metadata model.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
public class Reference {
    /** Reference type **/
    private String type;

    /** Reference link **/
    @XmlElement(name = "$ref")
    private String ref;

    public Reference() {
    }

    public Reference(String ref) {
        this.ref = ref;
    }

    public Reference(String type, String ref) {
        this.type = type;
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
}
