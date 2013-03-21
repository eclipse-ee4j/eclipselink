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

public class Element extends SimpleComponent {
    private ComplexType complexType;
    private String minOccurs;
    private String maxOccurs;
    private boolean nillable;
    private boolean abstractValue;
    private String ref;
    private String substitutionGroup;

    public Element() {
    }

    public void setComplexType(ComplexType complexType) {
        if (complexType != null) {
            complexType.setOwner(this);
        }
        this.complexType = complexType;
    }

    public ComplexType getComplexType() {
        return complexType;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setAbstractValue(boolean abstractValue) {
        this.abstractValue = abstractValue;
    }

    public boolean isAbstractValue() {
        return abstractValue;
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

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getRef() {
        return ref;
    }
    
    public String getSubstitutionGroup() {
        return substitutionGroup;
    }
    
    public void setSubstitutionGroup(String group) {
        this.substitutionGroup = group;
    }
}
