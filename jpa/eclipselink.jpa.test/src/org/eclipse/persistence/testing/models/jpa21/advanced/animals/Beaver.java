/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/12/2018-3.0 Tomas Kraus
 *       - 531726 - COUNT with TREAT generates incorrect joins for Joined Inheritance
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa21.advanced.animals;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@code Beaver} entity is direct {@code Rodent} entity descendant.
 */
@Entity
@Table(name = "JPA21_BEAVER")
public class Beaver extends Rodent {

    @Override
    public String toString() {
        return "Beaver: " + getName();
    }

}
