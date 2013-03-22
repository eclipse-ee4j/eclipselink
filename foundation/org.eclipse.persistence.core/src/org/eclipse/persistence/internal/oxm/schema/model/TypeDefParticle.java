/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm.schema.model;

import java.util.List;
import java.util.ArrayList;

/**
 * <p><b>Purpose</b>: Base class for Sequence, Choice and All
 * @see org.eclipse.persistence.internal.oxm.schema.model.Sequence
 * @see org.eclipse.persistence.internal.oxm.schema.model.Choice
 * @see org.eclipse.persistence.internal.oxm.schema.model.All
 */
public abstract class TypeDefParticle {
    private TypeDefParticleOwner owner;
    private String minOccurs;
    private String maxOccurs;
    private java.util.List elements;

    //Group??
    public TypeDefParticle() {
    }

    public void addElement(Element elem) {
        getElements().add(elem);
    }

    public void setElements(List elements) {
        this.elements = elements;
    }

    public List getElements() {
        if (elements == null) {
            elements = new ArrayList();
        }
        return elements;
    }

    public void setMinOccurs(String minOccurs) {
        this.minOccurs = minOccurs;
    }

    public String getMinOccurs() {
        return minOccurs;
    }

    public void setMaxOccurs(String maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public String getMaxOccurs() {
        return maxOccurs;
    }

    public void setOwner(TypeDefParticleOwner owner) {
        this.owner = owner;
    }

    public TypeDefParticleOwner getOwner() {
        return owner;
    }

    public String getOwnerName() {
        if (owner == null) {
            return null;
        } else if (owner instanceof TypeDefParticle) {
            return ((TypeDefParticle)owner).getOwnerName();
        } else if (owner instanceof ComplexType) {
            return ((ComplexType)owner).getNameOrOwnerName();
        }
        return null;
    }
}
