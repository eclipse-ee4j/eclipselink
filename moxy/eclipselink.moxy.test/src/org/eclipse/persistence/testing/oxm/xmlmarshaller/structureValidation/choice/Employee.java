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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.choice;

import java.util.Calendar;

public class Employee {
    Period _StartDateOrEndDate = null;

    public Employee() {
        _StartDateOrEndDate = new Period();
    }

    public Period getStartDateOrEndDate() {
        return _StartDateOrEndDate;
    }

    public void setStartDateOrEndDate(Period value) {
        _StartDateOrEndDate = value;
    }

    public static class Period {
        Calendar _StartDate = null;
        Calendar _EndDate = null;

        public Period() {
        }

        public void setStartDate(Calendar value) {
            _StartDate = value;
        }

        public Calendar getStartDate() {
            return _StartDate;
        }

        public void setEndDate(Calendar value) {
            _EndDate = value;
        }

        public Calendar getEndDate() {
            return _EndDate;
        }
    }
}
