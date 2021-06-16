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
//     05/24/2012 ailitchev
//       Bug 380580 - DatabaseField mapped with Temporal in EmbeddedId has no type
//             caused an NPE in ddl generation code for this class
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.util.*;
import jakarta.persistence.*;

import java.util.Date;


@Entity(name="DDL_PROJECT")
@Table(name="DDL_PROJECT")
public class Project implements java.io.Serializable
{
    // Instance variables
    //private String pname;
    //private Date pdate;
    private String resource;
    private int duration;

    public Project() {}

    public Project(String res, int dur) {

        //this.pname=pname;
        //this.pdate=pdate;
        this.resource=res;
        this.duration=dur;
    }



    protected ProjectPK projectPk = new ProjectPK();

    // ===========================================================
    // getters and setters for persistent fields

    @EmbeddedId
    public ProjectPK getProjectPk() {
          return projectPk;
    }

    public void setProjectPk(ProjectPK ppk) {
          this.projectPk = ppk;
    }


    @Column(name="RES")
    public String getRes() {
    return resource;
    }
    public void setRes(String resource) {
    this.resource = resource;
    }

    @Column(name="DURATION")
    public int getDuration() {
    return duration;
    }
    public void setDuration(int dur) {
    this.duration = dur;
    }

}
