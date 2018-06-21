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

public class Restriction implements SimpleDerivation {
    private String baseType;//QName lateR??

    //can have a typeDefParticle (seq choice all if in complexContent) OR simplerestmodel (facets
    private TypeDefParticle typeDefParticle;
    private Choice choice;
    private Sequence sequence;
    private All all;
    private SimpleType simpleType;
    private java.util.ArrayList enumerationFacets;
    private AnyAttribute anyAttribute;
    private String minInclusive;
    private String maxInclusive;
    private String minExclusive;
    private String maxExclusive;
    private String totalDigits;
    private String fractionDigits;
    private java.util.List<String> patterns = new ArrayList<String>();
    private String length;
    private String minLength;
    private String maxLength;

    //private List facets
    private java.util.List attributes;
    private Restrictable owner;

    public Restriction() {
    }

    public Restriction(String baseType) {
        this.baseType = baseType;
    }

    public void setMinInclusive(String minInclusive) {
        this.minInclusive = minInclusive;
    }

    public String getMinInclusive() {
        return minInclusive;
    }

    public void setMaxInclusive(String maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public String getMaxInclusive() {
        return maxInclusive;
    }

    public void setMinExclusive(String minExclusive) {
        this.minExclusive = minExclusive;
    }

    public String getMinExclusive() {
        return minExclusive;
    }

    public void setMaxExclusive(String maxExclusive) {
        this.maxExclusive = maxExclusive;
    }

    public String getMaxExclusive() {
        return maxExclusive;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getBaseType() {
        return baseType;
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

    public void setChoice(Choice choice) {
        this.choice = choice;
        typeDefParticle = choice;
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
        typeDefParticle = all;
    }

    public All getAll() {
        return all;
    }

    public void setSimpleType(SimpleType simpleType) {
        this.simpleType = simpleType;
    }

    public SimpleType getSimpleType() {
        return simpleType;
    }

    public void setAttributes(java.util.List attributes) {
        this.attributes = attributes;
    }

    public java.util.List getAttributes() {
        return attributes;
    }

    public java.util.ArrayList getEnumerationFacets() {
        return enumerationFacets;
    }

    public void setEnumerationFacets(java.util.ArrayList values) {
        enumerationFacets = values;
    }

    public void setOwner(Restrictable owner) {
        this.owner = owner;
    }

    public Restrictable getOwner() {
        return owner;
    }

    public String getOwnerName() {
        if (owner != null) {
            return owner.getOwnerName();
        }
        return null;
    }

    public AnyAttribute getAnyAttribute() {
        return anyAttribute;
    }

    public void setAnyAttribute(AnyAttribute any) {
        anyAttribute = any;
    }

    public String getTotalDigits() {
        return totalDigits;
    }

    public void setTotalDigits(String totalDigits) {
        this.totalDigits = totalDigits;
    }

    public void setTotalDigits(int totalDigits) {
        this.totalDigits = String.valueOf(totalDigits);
    }

    public String getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(String fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    public void setFractionDigits(int fractionDigits) {
        this.fractionDigits = String.valueOf(fractionDigits);
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public void addPattern(String regexp) {
        this.patterns.add(regexp);
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setLength(int length) {
        this.length = String.valueOf(length);
    }

    public String getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = String.valueOf(minLength);
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = String.valueOf(maxLength);
    }

    /**
     * Overwrites attributes of this restriction with attributes of the argument,
     * if they are not null.
     *
     * @param restriction
     */
    public void mergeWith(Restriction restriction) {
        if (restriction.getAll() != null) this.setAll(restriction.getAll());
        if (restriction.getAnyAttribute() != null) this.setAnyAttribute(restriction.getAnyAttribute());
        // if (restriction.getBaseType()) // this one doesn't make sense to do
        // if (restriction.getSimpleType()) // this one doesn't make sense to do
        if (restriction.getTypeDefParticle() != null) this.setTypeDefParticle(restriction.getTypeDefParticle()); // not sure if this one makes sense
        if (restriction.getSequence() != null) this.setSequence(restriction.getSequence());
        if (restriction.getChoice() != null) this.setChoice(restriction.getChoice());
        if (restriction.getEnumerationFacets() != null) this.setEnumerationFacets(restriction.getEnumerationFacets());
        if (restriction.getPatterns() != null) this.setPatterns(restriction.getPatterns()); // restriction.getPatterns() is "always" not null
        if (restriction.getMaxInclusive() != null) this.setMaxInclusive(restriction.getMaxInclusive());
        if (restriction.getMinInclusive() != null) this.setMinInclusive(restriction.getMinInclusive());
        if (restriction.getMaxExclusive() != null) this.setMaxExclusive(restriction.getMaxExclusive());
        if (restriction.getMinExclusive() != null) this.setMinExclusive(restriction.getMinExclusive());
        if (restriction.getTotalDigits() != null) this.setFractionDigits(restriction.getTotalDigits());
        if (restriction.getFractionDigits() != null) this.setFractionDigits(restriction.getFractionDigits());
        if (restriction.getLength() != null) this.setLength(restriction.getLength());
        if (restriction.getMinLength() != null) this.setMinLength(restriction.getMinLength());
        if (restriction.getMaxLength() != null) this.setMaxLength(restriction.getMaxLength());
    }
}
