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
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.inheritance;

public class Employee extends Person {
    private String jobTitle;

    public Employee() {}

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String newJobTitle) {
        jobTitle = newJobTitle;
    }

    public String toString() {
        String returnString = super.toString();
        returnString += " Employee(jobTitle=" + jobTitle + ")";
        return returnString;
    }

    public boolean equals(Object object) {
        boolean superEquals = super.equals(object);
        if(!superEquals){
            return false;
        }

        if(!(object instanceof Employee)) {
            return false;
        }

        String title = ((Employee)object).getJobTitle();

        if (title == null) {
            if (this.getJobTitle() == null) {
                return true;
            }
            return false;
        }

        if(title.equals(this.getJobTitle())) {
            return true;
        }

        return false;
    }
}
