/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inheritance;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Models an Entity with a unidirectional lazy 1:M reference to
 * another Entity with a unidirectional lazy 1:M reference.
 */
@Entity
@Table(name="JPA_PET_STORE")
public class PetStore {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(nullable=false)
    private Long id;

    @Version
    protected int version;

    @Column(name="STORE_NAME")
    private String storeName;

    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="STORE_ID")
    private List<FishTank> fishTanks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<FishTank> getFishTanks() {
        return this.fishTanks;
    }

    public void setFishTanks(List<FishTank> fishTanks) {
        this.fishTanks = fishTanks;
    }

}
