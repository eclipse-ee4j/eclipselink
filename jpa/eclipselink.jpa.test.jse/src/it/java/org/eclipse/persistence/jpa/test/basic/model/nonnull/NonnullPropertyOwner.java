/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.basic.model.nonnull;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "NNOPT_PROPERTY")
@Access(AccessType.PROPERTY)
public class NonnullPropertyOwner extends NonnullPropertyBase {
    private long id;
    private String implicitBasic;
    private String explicitBasic;
    private String requiredBasic;
    private String nullableBasic;
    private NonnullTarget requiredMany;
    private NonnullTarget optionalMany;
    private NonnullTarget requiredOne;
    private NonnullTarget optionalOne;
    @Nonnull
    private String ignoredFieldValue;
    private String transientValue;

    @Id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nonnull
    public String getImplicitBasic() {
        return implicitBasic;
    }

    public void setImplicitBasic(String implicitBasic) {
        this.implicitBasic = implicitBasic;
    }

    @Nonnull
    @Basic(optional = true)
    public String getExplicitBasic() {
        return explicitBasic;
    }

    public void setExplicitBasic(String explicitBasic) {
        this.explicitBasic = explicitBasic;
    }

    @Nonnull
    @Basic(optional = false)
    public String getRequiredBasic() {
        return requiredBasic;
    }

    public void setRequiredBasic(String requiredBasic) {
        this.requiredBasic = requiredBasic;
    }

    public String getNullableBasic() {
        return nullableBasic;
    }

    public void setNullableBasic(String nullableBasic) {
        this.nullableBasic = nullableBasic;
    }

    @Nonnull
    @ManyToOne(optional = true)
    @JoinColumn(name = "NN_MTO")
    public NonnullTarget getRequiredMany() {
        return requiredMany;
    }

    public void setRequiredMany(NonnullTarget requiredMany) {
        this.requiredMany = requiredMany;
    }

    @ManyToOne
    @JoinColumn(name = "OPT_MTO")
    public NonnullTarget getOptionalMany() {
        return optionalMany;
    }

    public void setOptionalMany(NonnullTarget optionalMany) {
        this.optionalMany = optionalMany;
    }

    @Nonnull
    @OneToOne(optional = true)
    @JoinColumn(name = "NN_OTO")
    public NonnullTarget getRequiredOne() {
        return requiredOne;
    }

    public void setRequiredOne(NonnullTarget requiredOne) {
        this.requiredOne = requiredOne;
    }

    @OneToOne
    @JoinColumn(name = "OPT_OTO")
    public NonnullTarget getOptionalOne() {
        return optionalOne;
    }

    public void setOptionalOne(NonnullTarget optionalOne) {
        this.optionalOne = optionalOne;
    }

    public String getIgnoredFieldValue() {
        return ignoredFieldValue;
    }

    public void setIgnoredFieldValue(String ignoredFieldValue) {
        this.ignoredFieldValue = ignoredFieldValue;
    }

    @Nonnull
    @Transient
    public String getTransientValue() {
        return transientValue;
    }

    public void setTransientValue(String transientValue) {
        this.transientValue = transientValue;
    }
    @Nonnull
    public String getNoSetterValue() {
        return "not persistent";
    }

    @Nonnull
    public String getArgumentValue(String value) {
        return value;
    }
}
