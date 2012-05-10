/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.sequence.nestedGroup;

import org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.Period;

public class Employee {
    EmploymentInfo _IdAndStartDateAndEndDate;

    public Employee() {
        _IdAndStartDateAndEndDate = new EmploymentInfo();
    }

    public EmploymentInfo getIdAndStartDateAndEndDate() {
        return _IdAndStartDateAndEndDate;
    }

    public void setIdAndStartDateAndEndDate(EmploymentInfo value) {
        _IdAndStartDateAndEndDate = value;
    }

    public static class EmploymentInfo {
        int _Id;
        Period _G1;

        public EmploymentInfo() {
            _G1 = new Period();
        }

        public int getId() {
            return _Id;
        }

        public void setId(int value) {
            _Id = value;
        }

        public Period getG1() {
            return _G1;
        }

        public void setG1(Period value) {
            _G1 = value;
        }
    }
}
