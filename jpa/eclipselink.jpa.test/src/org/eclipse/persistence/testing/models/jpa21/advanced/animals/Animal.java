/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/12/2018-3.0 Tomas Kraus
//       - 531726 - COUNT with TREAT generates incorrect joins for Joined Inheritance
package org.eclipse.persistence.testing.models.jpa21.advanced.animals;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * {@code Animal} entity is common ancestor in this model.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "ANIMAL_TYPE")
@Table(name = "JPA21_ANIMAL")
public class Animal {

    @Id
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Animals model initialization
    /** Beavers names. */
    private static final String[] BEAVERS = {
      "Justin", "Chevie"
    };

    /** Hedgehogs names. */
    private static final String[] HEDGEHOGS = {
        "Spike", "Ozzy", "Sonic"
    };

    /**
     * Initialize {@code Animals} test model.
     *
     * @param em entity manager used to create entities.
     */
    public static void initAnimals(final EntityManager em) {
        for (String name : BEAVERS) {
            Beaver beaver = new Beaver();
            beaver.setName(name);
            em.persist(beaver);
        }
        for (String name : HEDGEHOGS) {
            Hedgehog hedgehog = new Hedgehog();
            hedgehog.setName(name);
            em.persist(hedgehog);
        }
    }

}
