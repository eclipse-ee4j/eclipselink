/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     May 28, 2012 - ailitchev
//        - Bug 341709 - Delete fails with DB constraint violation due to an internal update
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "CMP3_FA_SOURCE")
public class Source implements Serializable {
    @Id
    private String id;
    private String description;

    @OneToOne()
    @JoinColumn(name="A_ID", nullable= false)
    private TargetA targetA;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="B_ID", nullable = false)
    private TargetB targetB;

    /**
     *
     */
    public Source() {
    }

    public Source(String id) {
        this.id = id;
    }

    public Source(String id, String description) {
        this(id);
        this.description = description;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TargetA getTargetA() {
        return this.targetA;
    }

    public void setTargetA(TargetA targetA) {
        this.targetA = targetA;
    }

    public TargetB getTargetB() {
        return this.targetB;
    }

    public void setTargetB(TargetB targetB) {
        this.targetB = targetB;
    }
}
