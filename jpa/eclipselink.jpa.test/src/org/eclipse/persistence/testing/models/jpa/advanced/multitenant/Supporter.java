/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
//     08/11/2012-2.5 Guy Pelletier
//       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
//     20/11/2012-2.5 Guy Pelletier
//       - 394524: Invalid query key [...] in expression
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import static javax.persistence.CascadeType.PERSIST;

import static org.eclipse.persistence.annotations.MultitenantType.TABLE_PER_TENANT;
import static org.eclipse.persistence.annotations.TenantTableDiscriminatorType.PREFIX;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;

@Entity
@Table(name="JPA_SUPPORTER")
@Multitenant(TABLE_PER_TENANT)
@TenantTableDiscriminator(type=PREFIX)
@NamedQueries({
    @NamedQuery(
        name = "Supporter.findAll",
        query = "SELECT a FROM Supporter a ORDER BY a.id DESC"),
    @NamedQuery(
        name = "Supporter.findBySupporterInfo",
        query = "SELECT a FROM Supporter a WHERE a.info.description = :desc"),
    @NamedQuery(
        name = "Supporter.findBySupporterInfoSub",
        query = "SELECT a FROM Supporter a WHERE a.info.subInfo.subDescription = :subDesc")
})
public class Supporter {
    @Id
    @GeneratedValue
    public long id;
    public String name;

    @ManyToMany(mappedBy="supporters")
    public List<Candidate> supportedCandidates;

    @OneToOne(cascade=PERSIST)
    @JoinColumn(name="SUPPORTER_INFO_ID")
    public SupporterInfo info;

    public Supporter() {
        supportedCandidates = new ArrayList<Candidate>();
    }

    protected void addSupportedCandidate(Candidate candidate) {
        supportedCandidates.add(candidate);
    }

    public long getId() {
        return id;
    }

    public SupporterInfo getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }

    public List<Candidate> getSupportedCandidates() {
        return supportedCandidates;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setInfo(SupporterInfo info) {
        this.info = info;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSupportedCandidates(List<Candidate> supportedCandidates) {
        this.supportedCandidates = supportedCandidates;
    }

    public String toString() {
        return "Supporter (" + getName() + ") [" + getId() + "]";
    }
}
