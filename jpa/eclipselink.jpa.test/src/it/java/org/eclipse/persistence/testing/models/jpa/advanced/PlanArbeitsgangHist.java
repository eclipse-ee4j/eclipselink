/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;

@Entity
@SequenceGenerator(name = "PlanArbeitsgangHistSEQ", initialValue = 100, allocationSize = 100)
public class PlanArbeitsgangHist {

    @Id
    @GeneratedValue(generator = "PlanArbeitsgangHistSEQ")
    private Long id;

    @Version
    private Long version;

    private String name;

    @ManyToOne
    private PlanArbeitsgang original;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn
    private MaterialHist material;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlanArbeitsgang getOriginal() {
        return original;
    }

    public void setOriginal(PlanArbeitsgang original) {
        this.original = original;
    }

    public MaterialHist getMaterial() {
        return material;
    }

    public void setMaterial(MaterialHist material) {
        this.material = material;
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s]", getClass().getSimpleName(), id);
    }
}
