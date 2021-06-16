/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     15/08/2011-2.3.1 Guy Pelletier
//       - 298494: JPQL exists subquery generates unnecessary table join
package org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.Cache;

import static jakarta.persistence.InheritanceType.JOINED;
import static org.eclipse.persistence.config.CacheIsolationType.PROTECTED;

@Entity
@Table(name="JPA_AC_FOOD")
@Inheritance(strategy=JOINED)
@DiscriminatorValue("F")
@Cache(isolation=PROTECTED)
public class Food {
    @Id
    @GeneratedValue
    @Column(name="F_ID")
    public Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
