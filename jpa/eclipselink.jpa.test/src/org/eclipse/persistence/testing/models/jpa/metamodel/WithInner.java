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
//     02/26/2018-2.7.2 Lukas Jungmann
//       - 531528: IdentifiableType.hasSingleIdAttribute() returns true when IdClass references an inner class
package org.eclipse.persistence.testing.models.jpa.metamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="INNER_PK")
@IdClass(WithInner.InnerPK.class)
public class WithInner {

    @Column(name="DESC")
    public String description;

    @Id
    @Column(name="ID1")
    public int id1;

    @Id
    @Column(name="ID2")
    public String id2;

    public static class InnerPK implements Serializable {

        private int id1;
        private String id2;

        public int getId1() {
            return id1;
        }

        public void setId1(int id1) {
            this.id1 = id1;
        }

        public String getId2() {
            return id2;
        }

        public void setId2(String id2) {
            this.id2 = id2;
        }
    }
}
