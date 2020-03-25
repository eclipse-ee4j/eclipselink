/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.component;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TMP_METRIC")
@IdClass(MetricPk.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = 30)
// TODO fix model for abstract superclass
public abstract class Metric {

    private Component component;
    private Long componentId;
    private Long inspectionId;
    private String name;

    @Id
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Id
    @Column(name = "COMPONENT_ID")
    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    @Id
    @Column(name = "INSPECTION_ID")
    public Long getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(Long inspectionId) {
        this.inspectionId = inspectionId;
    }

    // @ManyToOne(cascade = {CascadeType.PERSIST})
    // @JoinColumn(name = "INSPECTION_ID", insertable = false, updatable = false)
    // public Inspection getInspection() {
    // return inspection;
    // }
    //
    // public void setInspection(Inspection inspection) {
    // this.inspection = inspection;
    // }

    @ManyToOne(cascade = { CascadeType.PERSIST })
    @JoinColumn(name = "COMPONENT_ID", insertable = false, updatable = false)
    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }
}
