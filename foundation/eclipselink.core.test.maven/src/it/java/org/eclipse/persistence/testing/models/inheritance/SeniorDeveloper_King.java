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
