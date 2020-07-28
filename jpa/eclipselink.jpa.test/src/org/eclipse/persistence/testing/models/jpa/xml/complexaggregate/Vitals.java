/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     04/02/2008-1.0M6 Guy Pelletier
//       - 224155: embeddable-attributes should be extended in the EclipseLink ORM.XML schema
package org.eclipse.persistence.testing.models.jpa.xml.complexaggregate;

import java.io.Serializable;

public class Vitals implements Serializable {
    TeamVitals teamVitals;
    PersonalVitals personalVitals;

    public Vitals() {}

    public PersonalVitals getPersonalVitals() {
        return personalVitals;
    }

    public void setPersonalVitals(PersonalVitals personalVitals) {
        this.personalVitals = personalVitals;
    }

    public TeamVitals getTeamVitals() {
        return teamVitals;
    }

    public void setTeamVitals(TeamVitals teamVitals) {
        this.teamVitals = teamVitals;
    }
}
