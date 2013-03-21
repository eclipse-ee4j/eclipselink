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

import java.util.ArrayList;

public class Extension {
    private String baseType;//QName lateR??
    private java.util.List attributes;
    private Content owner;
    private java.util.List orderedAttributes;
    private TypeDefParticle typeDefParticle;
    private Choice choice;
    private Sequence sequence;
    private All all;
    private AnyAttribute anyAttribute;

    public Extension() {
        orderedAttributes = new ArrayList();
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
        if (choice != null) {
            this.typeDefParticle = choice;
        }
    }

    public Choice getChoice() {
        return choice;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
        if (sequence != null) {
            this.typeDefParticle = sequence;
        }
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setAll(All all) {
        this.all = all;
        if (all != null) {
            this.typeDefParticle = all;
        }
    }

    public All getAll() {
        return all;
    }

    public void setTypeDefParticle(TypeDefParticle typeDefParticle) {
        this.typeDefParticle = typeDefParticle;
        if (typeDefParticle instanceof Choice) {
            setChoice((Choice)typeDefParticle);
        } else if (typeDefParticle instanceof Sequence) {
            setSequence((Sequence)typeDefParticle);
        } else {
            setAll((All)typeDefParticle);
        }
    }

    public TypeDefParticle getTypeDefParticle() {
        return typeDefParticle;
    }

    public void setOwner(Content owner) {
        this.owner = owner;
    }

    public Content getOwner() {
        return owner;
    }

    public String getOwnerName() {
        if (owner != null) {
            return owner.getOwnerName();
        }
        return null;
    }

    public void setOrderedAttributes(java.util.List orderedAttributes) {
        this.orderedAttributes = orderedAttributes;
    }

    public java.util.List getOrderedAttributes() {
        return orderedAttributes;
    }
    
    public AnyAttribute getAnyAttribute() {
        return anyAttribute;
    }

    public void setAnyAttribute(AnyAttribute any) {
        anyAttribute = any;
    }
}
