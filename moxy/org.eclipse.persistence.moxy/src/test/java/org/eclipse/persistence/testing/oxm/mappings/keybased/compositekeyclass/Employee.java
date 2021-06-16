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
//     bdoughan - Oct 29/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.keybased.compositekeyclass;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    private EmployeeID id;
    private Employee manager;
    private List<Employee> teamMembers;

    public Employee() {
        teamMembers = new ArrayList();
    }

    public Employee getManager() {
        return manager;
    }

    public EmployeeID getId() {
        return id;
    }

    public void setId(EmployeeID id) {
        this.id = id;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public List<Employee> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<Employee> teamMembers) {
        this.teamMembers = teamMembers;
    }

    @Override
    public boolean equals(Object object) {
        try {
            Employee test = (Employee) object;
            if(!id.equals(test.getId())) {
                return false;
            }
            if(null == manager) {
                if(null != test.getManager()) {
                    return false;
                }
            } else {
                if(!manager.getId().equals(test.getManager().getId())) {
                    return false;
                }
            }
            int teamMembersSize = teamMembers.size();
            if(teamMembersSize != test.getTeamMembers().size()) {
                return false;
            }
            for(int x=0; x<teamMembersSize; x++) {
                if(!teamMembers.get(x).getId().equals(test.getTeamMembers().get(x).getId())) {
                    return false;
                }
            }
            return true;
        } catch(ClassCastException e) {
            return false;
        }
    }

}
