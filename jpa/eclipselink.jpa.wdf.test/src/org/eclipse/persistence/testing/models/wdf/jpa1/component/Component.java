/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.component;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "TMP_COMPONENT")
public class Component {

    private Long id;
    private Collection<Metric> metrics = new HashSet<Metric>();

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "InvTaskComp")
    @TableGenerator(name = "InvTaskComp", table = "DIS_SEQ_DIS", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VAL", pkColumnValue = "Component")
    public Long getId() {
        return id;
    }

    protected void setId(Long pId) {
        id = pId;
    }

    @OneToMany(targetEntity = Metric.class, mappedBy = "component", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
    public Collection<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(Collection<Metric> metrics) {
        this.metrics = metrics;
    }

}
