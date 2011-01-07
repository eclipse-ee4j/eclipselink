/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
