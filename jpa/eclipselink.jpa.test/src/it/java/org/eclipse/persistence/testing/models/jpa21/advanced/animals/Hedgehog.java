/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     03/12/2018-3.0 Tomas Kraus
//       - 531726 - COUNT with TREAT generates incorrect joins for Joined Inheritance
package org.eclipse.persistence.testing.models.jpa21.advanced.animals;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * {@code Hedgehog} entity is direct {@code Animal} entity descendant.
 */
@Entity
@Table(name = "JPA21_HEDGEHOG")
public class Hedgehog extends Animal {

    @Override
    public String toString() {
        return "The hedgehog " + getName() + " can never be buggered at all";
    }

}
