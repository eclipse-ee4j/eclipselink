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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.sequence;

import java.util.Calendar;

public class Employee {
    Period _StartDateAndEndDate;

    public Employee() {
        _StartDateAndEndDate = new Period();
    }

    public Period getStartDateAndEndDate() {
        return _StartDateAndEndDate;
    }

    public void setStartDateAndEndDate(Period value) {
        _StartDateAndEndDate = value;
    }

    public static class Period {
        Calendar _StartDate;
        Calendar _EndDate;

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
