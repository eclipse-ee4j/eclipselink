/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self;

import java.util.Calendar;

public class Employee {
    Period _StartDateAndEndDate;
    Address address;
    int id;

    public Employee() {
        _StartDateAndEndDate = new Period();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Period getStartDateAndEndDate() {
        return _StartDateAndEndDate;
    }

    public void setStartDateAndEndDate(Period value) {
        _StartDateAndEndDate = value;
    }

    public boolean equals(Object object) {
        try {
            Employee employee = (Employee)object;
            if (this == employee) {
                return true;
            }
            if (id != employee.getId()) {
                return false;
            }
            if (null == _StartDateAndEndDate) {
                if (null != employee.getStartDateAndEndDate()) {
                    return false;
                }
            } else {
                if (!_StartDateAndEndDate.equals(employee.getStartDateAndEndDate())) {
                    return false;
                }
            }
            if (null == address) {
                if (null != employee.address) {
                    return false;
                }
            } else {
                if (!address.equals(employee.address)) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Employee(_StartDateAndEndDate=");
        stringBuffer.append(_StartDateAndEndDate);
        stringBuffer.append(" address=");
        stringBuffer.append(address);
        stringBuffer.append("\")");
        return stringBuffer.toString();
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

        public boolean equals(Object object) {
            try {
                Period period = (Period)object;
                if ((getStartDate() == period.getStartDate()) && (getEndDate() == period.getEndDate())) {
                    return true;
                }
                if ((getStartDate().getTime().compareTo(period.getStartDate().getTime()) != 0) || (getEndDate().getTime().compareTo(period.getEndDate().getTime()) != 0)) {
                    return false;
                }
                return true;
            } catch (ClassCastException e) {
                return false;
            }
        }
    }

}