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

import java.util.ArrayList;

public class AttributeGroup {
    private java.util.List attributes;
    private AnyAttribute anyAttribute;
    private String name;
    private String ref;

    public AttributeGroup() {
        attributes = new ArrayList();
    }

    public void setAttributes(java.util.List attributes) {
        this.attributes = attributes;
    }

    public java.util.List getAttributes() {
        return attributes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAnyAttribute(AnyAttribute anyAttribute) {
        this.anyAttribute = anyAttribute;
    }

    public AnyAttribute getAnyAttribute() {
        return anyAttribute;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getRef() {
        return ref;
    }
}