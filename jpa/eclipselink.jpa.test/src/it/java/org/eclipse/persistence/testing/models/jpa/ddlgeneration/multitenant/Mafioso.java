/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     04/28/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 6)
//     11/10/2011-2.4 Guy Pelletier
//       - 357474: Address primaryKey option from tenant discriminator column
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.InheritanceType.JOINED;

@Entity
@Table(name="DDL_MAFIOSO")
@Multitenant
@TenantDiscriminatorColumn(name="TENANT_ID", contextProperty="tenant.id")
@Inheritance(strategy=JOINED)
@DiscriminatorColumn(name="DTYPE")
public abstract class Mafioso {
    public enum Gender { Female, Male }

    private int id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private MafiaFamily family;

    public Mafioso() {}

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name="FAMILY_ID", referencedColumnName="ID")
    })
    public MafiaFamily getFamily() {
        return family;
    }

    public String getFirstName() {
        return firstName;
    }

    @ObjectTypeConverter(
        name="gender",
        dataType=String.class,
        objectType=org.eclipse.persistence.testing.models.jpa.ddlgeneration.multitenant.Mafioso.Gender.class,
        conversionValues={
            @ConversionValue(dataValue="F", objectValue="Female"),
            @ConversionValue(dataValue="M", objectValue="Male")
        }
    )
    public Gender getGender() {
        return gender;
    }

    @Id
    @Column(name="ID")
    @GeneratedValue
    public int getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFamily(MafiaFamily family) {
        this.family = family;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
