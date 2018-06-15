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
