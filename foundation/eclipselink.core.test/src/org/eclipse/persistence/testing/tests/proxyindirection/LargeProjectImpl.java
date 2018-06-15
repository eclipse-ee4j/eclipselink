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
package org.eclipse.persistence.testing.tests.proxyindirection;


/*
 * Large Project implementation.
 *
 * Implementation of the Large Project interface.
 *
 * @author        Rick Barkhouse
 * @since        08/24/2000 11:11:00
 */
public class LargeProjectImpl extends ProjectImpl implements LargeProject, Project {
    public double budget;
    public String investor;

    public double getBudget() {
        return this.budget;
    }

    public String getInvestor() {
        return this.investor;
    }

    public void setBudget(double value) {
        this.budget = value;
    }

    public void setInvestor(String value) {
        this.investor = value;
    }

    public String toString() {
        return "[LargeProject #" + getID() + "] " + getName() + " - " + getDescription() + " funded by " + getInvestor() + " for $" + getBudget();
    }
}
