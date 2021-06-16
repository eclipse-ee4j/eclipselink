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

import org.eclipse.persistence.testing.models.inheritance.Developer_King;

public class SeniorDeveloper_King extends Developer_King {
    private int officeNum;

    public SeniorDeveloper_King() {
        super();
    }

    public static SeniorDeveloper_King exp4() {
        SeniorDeveloper_King s1 = new SeniorDeveloper_King();
        s1.setId(4);
        s1.setName("Joan");
        s1.setOfficeNum(189);
        s1.setResponsibility("CEO");
        return s1;
    }

    public int getOfficeNum() {
        return officeNum;
    }

    public void setOfficeNum(int theOfficeNum) {
        officeNum = theOfficeNum;
    }
}
