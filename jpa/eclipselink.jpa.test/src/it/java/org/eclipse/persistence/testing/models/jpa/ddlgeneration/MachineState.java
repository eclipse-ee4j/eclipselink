/*
 * Copyright (c) 2008, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2008, 2015 Nathan Beyer (Cerner). All rights reserved.
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
//     08/20/2008-1.0.1 Nathan Beyer (Cerner)
//       - 241308: Primary key is incorrectly assigned to embeddable class
//                 field with the same name as the primary key field's name
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MACHINE_STATE")
public class MachineState {
    @Id
    private long id;

    @AttributeOverrides( {
        @AttributeOverride(name = "id", column = @Column(name = "THREAD_ID")),
        @AttributeOverride(name = "name", column = @Column(name = "THREAD_NAME")) })
    private ThreadInfo thread;

    public MachineState() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ThreadInfo getThread() {
        return thread;
    }

    public void setThread(ThreadInfo thread) {
        this.thread = thread;
    }
}
