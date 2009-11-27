/*******************************************************************************
 * Copyright (c) 2008 Nathan Beyer (Cerner). All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/20/2008-1.0.1 Nathan Beyer (Cerner) 
 *       - 241308: Primary key is incorrectly assigned to embeddable class 
 *                 field with the same name as the primary key field's name
 ******************************************************************************/  
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
