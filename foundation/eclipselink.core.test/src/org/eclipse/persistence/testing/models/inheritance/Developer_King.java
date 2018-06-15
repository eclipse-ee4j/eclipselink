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
package org.eclipse.persistence.testing.models.inheritance;

public class Developer_King extends Person_King {
    private String responsibility;
    public int codeIndex;

    public Developer_King() {
        super();
    }

    public static Developer_King exp2() {
        Developer_King d1 = new Developer_King();
        d1.setId(2);
        d1.setName("Bob");

        //codeIndex is set using native sequencing
        //d1.setCodeIndex(29);
        d1.setResponsibility("Doing Java Stuff");
        return d1;
    }

    public static Developer_King exp3() {
        Developer_King d2 = new Developer_King();
        d2.setId(3);

        //codeIndex is set using native sequencing
        //d2.setCodeIndex(5);
        d2.setName("Dennis");
        d2.setResponsibility("Doing C++ Stuff");
        return d2;
    }

    public int getCodeIndex() {
        return codeIndex;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setCodeIndex(int theCodeIndex) {
        codeIndex = theCodeIndex;
    }

    public void setResponsibility(String theResponsibility) {
        responsibility = theResponsibility;
    }
}
