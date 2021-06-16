/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.inheritance;

public class Insect {
    protected Integer in_ID;
    protected Integer in_numberOfLegs;

    protected Entomologist entomologist;

    public Integer getIn_ID() {
        return in_ID;
    }

    public Integer getIn_numberOfLegs() {
        return in_numberOfLegs;
    }

    public void setIn_ID(Integer param1) {
        in_ID = param1;
    }

    public void setIn_numberOfLegs(Integer param1) {
        in_numberOfLegs = param1;
    }

    public Entomologist getEntomologist() {
        return entomologist;
    }

    public void setEntomologist(Entomologist entomologist) {
        this.entomologist = entomologist;
    }
}
