/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 IBM Corporation. All rights reserved.
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
//     05/30/2008-1.0M8 Guy Pelletier
//       - 230213: ValidationException when mapping to attribute in MappedSuperClass
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     08/11/2010-2.2 Guy Pelletier
//       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
package org.eclipse.persistence.testing.models.jpa21.advanced.inherited;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import javax.persistence.*;

import org.eclipse.persistence.annotations.ExistenceChecking;
import org.eclipse.persistence.annotations.ExistenceType;
import org.eclipse.persistence.annotations.Mutable;
import org.eclipse.persistence.indirection.ValueHolderInterface;

@Entity
@Table(name="JPA21_CANADIAN")
@AssociationOverride(name="beerConsumer", joinColumns=@JoinColumn(name="CONSUMER_ID"))
@ExistenceChecking(ExistenceType.CHECK_DATABASE)
public class Canadian extends Beer<Integer, Double, Canadian> {
    public enum Flavor { LAGER, LIGHT, ICE, DRY }

    private Flavor flavor;
    private Date bornOnDate;
    private HashMap<String, Serializable> properties;

    public ValueHolderInterface ignoredObject;

    public Canadian() {
        properties = new HashMap<String, Serializable>();
    }

    @Basic
    @Column(name="BORN")
    @Temporal(TemporalType.DATE)
    @Mutable
    public Date getBornOnDate() {
        return bornOnDate;
    }

    @Basic
    public Flavor getFlavor() {
        return flavor;
    }

    // Mimicking an accessor that was weaved to have value holders ... the
    // metadata processing should ignore this mapping.
    @OneToOne
    public ValueHolderInterface getIgnoredObject() {
        return ignoredObject;
    }

    @Basic
    public HashMap<String, Serializable> getProperties() {
        return properties;
    }

    public void setBornOnDate(Date bornOnDate) {
        this.bornOnDate = bornOnDate;
    }

    public void setFlavor(Flavor flavor) {
        this.flavor = flavor;
    }

    public void setIgnoredObject(ValueHolderInterface ignoredObject) {
        this.ignoredObject = ignoredObject;
    }

    public void setProperties(HashMap<String, Serializable> properties) {
        this.properties = properties;
    }
}
