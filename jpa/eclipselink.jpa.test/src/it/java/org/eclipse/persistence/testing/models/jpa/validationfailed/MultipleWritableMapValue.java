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
//     tware - test for bug 293827
package org.eclipse.persistence.testing.models.jpa.validationfailed;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

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
