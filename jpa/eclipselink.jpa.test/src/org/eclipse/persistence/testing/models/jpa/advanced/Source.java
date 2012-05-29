/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     May 28, 2012 - ailitchev
 *        - Bug 341709 - Delete fails with DB constraint violation due to an internal update
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CMP3_SOURCE")
public class Source implements Serializable {
    private String id;
    private String description;

    private TargetA targetA;
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

    @Id
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

    @OneToOne()
    @JoinColumn(name="A_ID", nullable= false)
    public TargetA getTargetA() {
        return this.targetA;
    }

    public void setTargetA(TargetA targetA) {
        this.targetA = targetA;
    }

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="B_ID", nullable = false)
    public TargetB getTargetB() {
        return this.targetB;
    }

    public void setTargetB(TargetB targetB) {
        this.targetB = targetB;
    }
}
