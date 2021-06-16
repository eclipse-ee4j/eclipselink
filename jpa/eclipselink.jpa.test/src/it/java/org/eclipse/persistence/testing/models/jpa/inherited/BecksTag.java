/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
package org.eclipse.persistence.testing.models.jpa.inherited;

import static jakarta.persistence.GenerationType.TABLE;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.TableGenerator;

@Entity
@Table(name="CMP3_BECKS_TAG")
public class BecksTag implements Cloneable {
    private Integer id;
    private String callNumber;

    public BecksTag() {}

    public BecksTag clone() throws CloneNotSupportedException {
        return (BecksTag) super.clone();
    }

    public boolean equals(Object anotherBecksTag) {
        if (anotherBecksTag.getClass() != BecksTag.class) {
            return false;
        }

        return (getId().equals(((BecksTag)anotherBecksTag).getId()));
    }

    @Column(name="CALL_NUMBER")
    public String getCallNumber() {
        return callNumber;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="BECKS_TAG_TABLE_GENERATOR")
    @TableGenerator(
        name="BECKS_TAG_TABLE_GENERATOR",
        table="CMP3_BECKS_TAG_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="BECKS_TAG_SEQ")
    public Integer getId() {
        return id;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
