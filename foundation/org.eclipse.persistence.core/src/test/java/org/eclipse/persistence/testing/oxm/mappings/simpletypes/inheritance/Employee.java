/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
            return this.getJobTitle() == null;
        }

        return title.equals(this.getJobTitle());
    }
}
