/*
 * Copyright (c) 2009, 2022 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa.beanvalidation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

@Entity(name="CMP3_BV_TASK")
public class Task {

    @Id
    private int        id;
    
    @Version
    private int        version;
    
    @NotNull
    private String     name;
    
    @Column
    private int        priority;
    
    public Task() {}


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }


    public int getPriority() {
        return priority;
    }


    public void setPriority(final int priority) {
        this.priority = priority;
    }
}
