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
import java.util.HashMap;
import java.util.Map;

public class ComplexType implements TypeDefParticleOwner {
    private String name;
    private boolean mixed;
    private boolean abstractValue;    
    private AnyAttribute anyAttribute;
    private TypeDefParticle typeDefParticle;
    private Choice choice;
    private Sequence sequence;
    private All all;
    private Element owner;    
    //simple content or complex content or typedef and attrDecls    
    private ComplexContent complexContent;
    private SimpleContent simpleContent;
    private Map attributesMap;
    private Annotation annotation;
    private java.util.List orderedAttributes;

    public ComplexType() {        
        orderedAttributes = new ArrayList();
        attributesMap = new HashMap();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMixed(boolean mixed) {
        this.mixed = mixed;
    }

    public boolean isMixed() {
        return mixed;
    }

    public void setAbstractValue(boolean abstractValue) {
        this.abstractValue = abstractValue;
    }

    public boolean isAbstractValue() {
        return abstractValue;
    }
   

    public void setAnyAttribute(AnyAttribute anyAttribute) {
        this.anyAttribute = anyAttribute;
    }

    public AnyAttribute getAnyAttribute() {
        return anyAttribute;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
        if (choice != null) {
            choice.setOwner(this);
            this.typeDefParticle = choice;
        }
    }

    public Choice getChoice() {
        return choice;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
        if (sequence != null) {
            sequence.setOwner(this);
            this.typeDefParticle = sequence;
        }
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setAll(All all) {
        this.all = all;
        if (all != null) {
            all.setOwner(this);
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

    public void setComplexContent(ComplexContent complexContent) {
        if (complexContent != null) {
            complexContent.setOwner(this);
        }
        this.complexContent = complexContent;
    }

    public ComplexContent getComplexContent() {
        return complexContent;
    }

    public void setSimpleContent(SimpleContent simpleContent) {
        if (simpleContent != null) {
            simpleContent.setOwner(this);
        }
        this.simpleContent = simpleContent;
    }

    public SimpleContent getSimpleContent() {
        return simpleContent;
    }

    public String getNameOrOwnerName() {
        if (getName() != null) {
            return getName();
        } else if (getOwner() != null) {
            return getOwner().getName();
        }
        return null;
    }

    public Element getOwner() {
        return owner;
    }

    public void setOwner(Element owner) {
        this.owner = owner;
    }

    public void setAttributesMap(Map attributesMap) {
        this.attributesMap = attributesMap;
    }

    public Map getAttributesMap() {
        return attributesMap;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }
  
    public void setOrderedAttributes(java.util.List orderedAttributes) {
        this.orderedAttributes = orderedAttributes;
    }

    public java.util.List getOrderedAttributes() {
        return orderedAttributes;
    }
}
