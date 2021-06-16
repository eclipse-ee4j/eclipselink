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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.calendartest;

import java.util.Calendar;

public class Employee extends org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.Employee {

    private Calendar birthdate;

    public Employee() {
    super();
        birthdate = Calendar.getInstance();
  }

    public Calendar getBirthdate()
    {
        return birthdate;
    }

    public void setBirthdate(Calendar birthdate)
    {
        this.birthdate = birthdate;
    }

  public boolean equals(Object object) {
        boolean returnVal = super.equals(object);
        if((returnVal) && ((this.getBirthdate()!=null && ((Employee)object).getBirthdate()!=null)||(this.getBirthdate().equals(((Employee)object).getBirthdate()))) )
        {
                return true;
        }
    return false;

  }

    public String toString()
  {
    return super.toString() + " Birthdate:" + this.getBirthdate().get(Calendar.YEAR) +" Month:" +this.getBirthdate().get(Calendar.MONTH)+ " Day:" +this.getBirthdate().get(Calendar.DAY_OF_MONTH) + " Time:" + this.getBirthdate().get(Calendar.HOUR_OF_DAY) +":"+this.getBirthdate().get(Calendar.MINUTE)+":"+this.getBirthdate().get(Calendar.SECOND);
  }
}
