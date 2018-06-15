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

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@code Rodent} entity is direct {@code Animal} entity descendant and ancestor of the {@code Beaver} entity.
 */
@Entity
@Table(name = "JPA21_RODENT")
public class Rodent extends Animal{
}
