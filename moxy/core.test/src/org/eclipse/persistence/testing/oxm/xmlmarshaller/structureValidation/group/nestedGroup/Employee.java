/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.group.nestedGroup;

public class Employee {
    EmploymentInfo _G1;

    public Employee() {
        _G1 = new EmploymentInfo();
    }

    public EmploymentInfo getG1() {
        return _G1;
    }

    public void setG1(EmploymentInfo value) {
        _G1 = value;
    }
}