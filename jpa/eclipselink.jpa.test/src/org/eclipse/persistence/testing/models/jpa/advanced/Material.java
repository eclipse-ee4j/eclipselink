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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@SequenceGenerator(name = "MaterialSEQ", initialValue = 100, allocationSize = 100)
public class Material {

    @Id
    @GeneratedValue(generator = "MaterialSEQ")
    private Long id;

    @Version
    private Long version;

    @OneToOne
    private MaterialHist lastHist;

    @OneToMany(mappedBy = "material", cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    private List<PlanArbeitsgang> restproduktionsweg = new ArrayList<>();

    private String ident;

    private String wert;

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

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getWert() {
        return wert;
    }

    public void setWert(String wert) {
        this.wert = wert;
    }

    public MaterialHist getLastHist() {
        return lastHist;
    }

    public void setLastHist(MaterialHist lastHist) {
        this.lastHist = lastHist;
    }

    public List<PlanArbeitsgang> getRestproduktionsweg() {
        return restproduktionsweg;
    }

    public void setRestproduktionsweg(List<PlanArbeitsgang> restproduktionsweg) {
        this.restproduktionsweg = restproduktionsweg;
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s, version=%s, ident=%s]", getClass().getSimpleName(), id, version, ident);
    }
}
