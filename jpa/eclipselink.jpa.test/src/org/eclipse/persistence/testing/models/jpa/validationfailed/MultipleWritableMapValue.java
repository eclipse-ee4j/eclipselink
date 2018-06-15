/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - test for bug 293827
package org.eclipse.persistence.testing.models.jpa.validationfailed;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name="CMP3_MWMAPVALUE")
public class MultipleWritableMapValue {

    @Id
    private int id = 0;

    @Column(name="VALUE")
    private int value = 0;

    @ManyToOne(fetch=LAZY)
    @JoinColumn(name="HOLDER_ID")
    private MultipleWritableMapHolder holder = null;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public MultipleWritableMapHolder getHolder() {
        return holder;
    }
    public void setHolder(MultipleWritableMapHolder holder) {
        this.holder = holder;
    }
}
