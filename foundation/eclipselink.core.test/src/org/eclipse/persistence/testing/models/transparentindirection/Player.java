/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.transparentindirection;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

public class Player {
    public long m_id;
    public ValueHolderInterface m_team;

    public Player() {
        m_id = System.currentTimeMillis();
        m_team = new ValueHolder();
    }

    public Integer getId() {
        return new Integer(new Long(m_id).intValue());
    }

    public Team getTeam() {
        return (Team)m_team.getValue();
    }

    public void setTeam(Team team) {
        m_team.setValue(team);
    }
}
