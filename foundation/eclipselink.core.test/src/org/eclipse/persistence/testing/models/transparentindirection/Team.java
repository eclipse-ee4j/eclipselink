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

import java.util.*;

public class Team {
    public int m_id;
    public Hashtable m_players;

    public Team() {
        m_players = new Hashtable();
    }

    public Hashtable getPlayers() {
        return m_players;
    }

    public void setPlayers(Hashtable players) {
        m_players = players;
    }
}
