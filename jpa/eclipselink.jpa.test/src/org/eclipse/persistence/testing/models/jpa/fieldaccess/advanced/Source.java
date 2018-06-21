/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     May 28, 2012 - ailitchev
//        - Bug 341709 - Delete fails with DB constraint violation due to an internal update
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
