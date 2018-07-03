/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.oxm.schema.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A Value Object class representing XML Schema's Element.
 */
public final class Element extends SimpleComponent {
    private ComplexType complexType;
    private String minOccurs;
    private String maxOccurs;
    private boolean nillable;
    private boolean abstractValue;
    private String substitutionGroup;
    private String maxInclusive;
    private String minInclusive;
    private String maxExclusive;
    private String minExclusive;
    private int totalDigits;
    private int fractionDigits;
    private String pattern;
    private List<String> patterns = new ArrayList<String>();
    private int length;
    private int minLength;
    private int maxLength;

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

    public String getSubstitutionGroup() {
        return substitutionGroup;
    }

    public void setSubstitutionGroup(String group) {
        this.substitutionGroup = group;
    }

    public String getMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(String maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public String getMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(String minInclusive) {
        this.minInclusive = minInclusive;
    }

    public String getMaxExclusive() {
        return maxExclusive;
    }

    public void setMaxExclusive(String maxExclusive) {
        this.maxExclusive = maxExclusive;
    }

    public String getMinExclusive() {
        return minExclusive;
    }

    public void setMinExclusive(String minExclusive) {
        this.minExclusive = minExclusive;
    }

    public int getTotalDigits() {
        return totalDigits;
    }

    public void setTotalDigits(int totalDigits) {
        this.totalDigits = totalDigits;
    }

    public int getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(int fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void addPattern(String pattern) {
        this.patterns.add(pattern);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
