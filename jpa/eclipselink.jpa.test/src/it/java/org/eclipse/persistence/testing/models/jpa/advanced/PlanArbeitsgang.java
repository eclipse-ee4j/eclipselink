/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial test case class implementation 
//     01/29/2019-3.0 Sureshkumar Balakrishnan
//       - 541873: ENTITYMANAGER.DETACH() TRIGGERS LAZY LOADING INTO THE PERSISTENCE CONTEXT

package org.eclipse.persistence.testing.models.jpa.advanced;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;

@Entity
@SequenceGenerator(name = "PlanArbeitsgangSEQ", initialValue = 100, allocationSize = 100)
public class PlanArbeitsgang {

    @Id
    @GeneratedValue(generator = "PlanArbeitsgangSEQ")
    private Long id;

    @Version
    private Long version;

    private String name;

    @OneToOne
    private PlanArbeitsgangHist lastHist;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn
    private Material material;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public PlanArbeitsgangHist getLastHist() {
        return lastHist;
    }

    public void setLastHist(PlanArbeitsgangHist lastHist) {
        this.lastHist = lastHist;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s, version=%s]", getClass().getSimpleName(), id, version);
    }
}
