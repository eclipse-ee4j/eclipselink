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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self;

import java.util.Calendar;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeObjectSelfTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/CompositeObjectSelf.xml";
    private final static int CONTROL_START_YYYY = 1970;
    private final static int CONTROL_START_MM = 0;
    private final static int CONTROL_START_DD = 1;
    private final static int CONTROL_END_YYYY = 2004;
    private final static int CONTROL_END_MM = 0;
    private final static int CONTROL_END_DD = 1;

    public CompositeObjectSelfTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new EmployeeProject());
    }

    protected Object getControlObject() {
        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(CONTROL_START_YYYY, CONTROL_START_MM, CONTROL_START_DD);

        Calendar end = Calendar.getInstance();
        end.clear();
        end.set(CONTROL_END_YYYY, CONTROL_END_MM, CONTROL_END_DD);

        Employee employee = new Employee();

        Employee.Period period = employee.getStartDateAndEndDate();
        period.setStartDate(start);
        period.setEndDate(end);

        employee.setStartDateAndEndDate(period);

        return employee;
    }
}
