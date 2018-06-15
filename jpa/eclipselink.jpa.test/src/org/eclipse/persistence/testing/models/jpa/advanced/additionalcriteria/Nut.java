/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
package org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="JPA_AC_NUT")
public class Nut {
    @Id
    @GeneratedValue(generator="AC_NUT_SEQ")
    @SequenceGenerator(name="AC_NUT_SEQ", allocationSize=25)
    public Integer id;

    @Column(name="B_SIZE")
    public Integer size;

    public String color;

    public String getColor() {
        return color;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSize() {
        return size;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

}
