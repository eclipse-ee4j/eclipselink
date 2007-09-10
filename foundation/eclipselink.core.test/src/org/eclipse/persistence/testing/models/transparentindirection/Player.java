/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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