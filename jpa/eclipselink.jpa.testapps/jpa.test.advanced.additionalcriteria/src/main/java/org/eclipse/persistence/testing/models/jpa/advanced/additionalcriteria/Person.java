/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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

import static jakarta.persistence.InheritanceType.JOINED;

@Entity(name="AC_PERSON")
@Table(name="JPA_AC_PERSON")
@Inheritance(strategy=JOINED)
@DiscriminatorValue("P")
public class Person {
    @Id
    @GeneratedValue
    @Column(name="P_ID")
    public Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
