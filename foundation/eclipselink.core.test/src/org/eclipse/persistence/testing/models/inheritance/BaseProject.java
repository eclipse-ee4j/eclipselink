/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.indirection.*;

/**
 * Designed to test cyclical relationships with inheritance.  Has a cyclical relationship
 * with ProjectWorker.
 */
public class BaseProject {
    private long id;
    private String name;
    private ValueHolderInterface teamLeader;

    public BaseProject() {
        this.name = "";
        this.teamLeader = new ValueHolder();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProjectWorker getTeamLeader() {
        return (ProjectWorker)teamLeader.getValue();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeamLeader(ProjectWorker teamLeader) {
        this.teamLeader.setValue(teamLeader);
    }
}
