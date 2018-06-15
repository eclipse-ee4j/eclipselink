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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.performance;

import java.io.*;

//import com.tangosol.io.pof.*;
/**
 * <b>Purpose</b>: Abstract superclass for Large & Small projects in Employee Demo
 * <p><b>Description</b>:     Project is an example of an abstract superclass. It demonstrates how class inheritance can be mapped to database tables.
 * It's subclasses are concrete and may or may not add columns through additional tables. The PROJ_TYPE field in the
 * database table indicates which subclass to instantiate. Projects are involved in a M:M relationship with employees.
 * The Employee classs maintains the definition of the relation table.
 * @see LargeProject
 * @see SmallProject
 */
//@org.hibernate.annotations.Cache(usage=org.hibernate.annotations.CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//public abstract class Project implements Serializable, PortableObject {
public abstract class Project implements Serializable {
    protected long id;
    protected long version;
    protected String name;
    protected String description;
    protected Employee teamLeader;

    public Project() {
        this.name = "";
        this.description = "";
    }

/*    public void readExternal(PofReader in) throws IOException {
        this.id = in.readLong(0);
        this.version = in.readLong(1);
        this.name = in.readString(2);
        this.description = in.readString(3);
    }

    public void writeExternal(PofWriter out) throws IOException {
        out.writeLong(0, this.id);
        out.writeLong(1, this.version);
        out.writeString(2, this.name);
        out.writeString(3, this.description);
    }*/

    public String getDescription() {
        return description;
    }

    /**
     * Return the persistent identifier of the receiver.
     */
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Employee getTeamLeader() {
        return teamLeader;
    }

    public long getVersion() {
        return version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the persistent identifier of the receiver.
     */
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeamLeader(Employee teamLeader) {
        this.teamLeader = teamLeader;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
