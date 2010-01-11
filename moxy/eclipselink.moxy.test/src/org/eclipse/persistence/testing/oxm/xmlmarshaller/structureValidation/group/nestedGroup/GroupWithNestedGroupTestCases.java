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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.group.nestedGroup;

import java.util.Calendar;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.Period;

public class GroupWithNestedGroupTestCases extends OXTestCase {
    private final static int START_YYYY = 1970;
    private final static int START_MM = 0;
    private final static int START_DD = 1;
    private final static int END_YYYY = 2004;
    private final static int END_MM = 0;
    private final static int END_DD = 1;
    private final static int ID = 1234;
    private XMLContext xmlContext;
    private XMLValidator xmlValidator;
    private Calendar start;
    private Calendar end;
    private EmploymentInfo info;
    private Employee employee;
    private Period period;

    public GroupWithNestedGroupTestCases(String name) throws Exception {
        super(name);
    }

    public void setUp() throws Exception {
        xmlContext = new XMLContext(new EmployeeProject());
        xmlValidator = xmlContext.createValidator();

        start = Calendar.getInstance();
        start.clear();
        start.set(START_YYYY, START_MM, START_DD);

        end = Calendar.getInstance();
        end.clear();
        end.set(END_YYYY, END_MM, END_DD);
    }

    public void testValidEmployee() throws Exception {
        period = new Period();
        period.setStartDate(start);
        period.setEndDate(end);

        info = new EmploymentInfo();
        info.setId(ID);
        info.setG2(period);

        employee = new Employee();
        employee.setG1(info);

        assertTrue("Valid employee reported invalid", xmlValidator.validateRoot(employee));
    }

    /**
     * We are dealing with a sequence for the startDate and endDate, hence both
     * are required to be valid.
     */
    public void testInvalidEmployee() throws Exception {
        period = new Period();
        period.setStartDate(start);

        info = new EmploymentInfo();
        info.setId(ID);
        info.setG2(period);

        employee = new Employee();
        employee.setG1(info);

        assertFalse("Invalid employee found to be valid", xmlValidator.validateRoot(employee));
    }
}
