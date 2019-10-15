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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@SequenceGenerator(name = "MaterialEreignisSEQ", initialValue = 100, allocationSize = 100)
public class MaterialEreignis {

    @Id
    @GeneratedValue(generator = "MaterialEreignisSEQ")
    private long id;

    @Version
    private Long version;

    @OneToOne(cascade = CascadeType.REFRESH)
    private Material changedObject;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    private MaterialHist beforeChange;

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    private MaterialHist afterChange;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Material getChangedObject() {
        return changedObject;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setChangedObject(Material changedObject) {
        this.changedObject = changedObject;
    }

    public MaterialHist getBeforeChange() {
        return beforeChange;
    }

    public void setBeforeChange(MaterialHist beforeChange) {
        this.beforeChange = beforeChange;
    }

    public MaterialHist getAfterChange() {
        return afterChange;
    }

    public void setAfterChange(MaterialHist afterChange) {
        this.afterChange = afterChange;
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s]", getClass().getSimpleName(), id);
    }
}
