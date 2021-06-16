/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - March 24/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.directcollection;

import java.util.List;

public class Employee {
    public int id;
    public List<String> projectIds;
    public List<Float> salaries;
    public List<String> privateData;
    public List<String> characterData;

    @jakarta.xml.bind.annotation.XmlTransient
    public boolean wasGetCalled;
    @jakarta.xml.bind.annotation.XmlTransient
    public boolean wasSetCalled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getProjectIds() {
        wasGetCalled = true;
        return projectIds;
    }

    public void setProjectIds(List<String> projectIds) {
        wasSetCalled = true;
        this.projectIds = projectIds;
    }

    public boolean equals(Object obj) {
        Employee empObj;
        try {
            empObj = (Employee) obj;
        } catch (ClassCastException e) {
            return false;
        }
        // compare id
        if (id != empObj.id) {
            return false;
        }
        // compare projectIds
        if (projectIds == null) {
            if (empObj.projectIds != null) {
                return false;
            }
        } else {
            if (empObj.projectIds == null || projectIds.size() != empObj.projectIds.size()) {
                return false;
            }
            for (String prj : projectIds) {
                if (!empObj.projectIds.contains(prj)) {
                    return false;
                }
            }
        }
        // compare salaries
        if (salaries == null) {
            if (empObj.salaries != null) {
                return false;
            }
        } else {
            if (empObj.salaries == null || salaries.size() != empObj.salaries.size()) {
                return false;
            }
            for (Float sal : salaries) {
                if (!empObj.salaries.contains(sal)) {
                    return false;
                }
            }
        }
        // compare privateData
        if (privateData == null) {
            if (empObj.privateData != null) {
                return false;
            }
        } else {
            if (empObj.privateData == null || privateData.size() != empObj.privateData.size()) {
                return false;
            }
            for (String pd : privateData) {
                if (!empObj.privateData.contains(pd)) {
                    return false;
                }
            }
        }
        // compare characterData
        if (characterData == null) {
            if (empObj.characterData != null) {
                return false;
            }
        } else {
            if (empObj.characterData == null || characterData.size() != empObj.characterData.size()) {
                return false;
            }
            for (String cd : characterData) {
                if (!empObj.characterData.contains(cd)) {
                    return false;
                }
            }
        }
        return true;
    }
}
