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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.choice;

import java.util.Calendar;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLValidator;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class ChoiceTestCases extends OXTestCase {
    private final static int START_YYYY = 1970;
    private final static int START_MM = 0;
    private final static int START_DD = 1;
    private XMLContext xmlContext;
    private XMLValidator xmlValidator;
    private Calendar cal;
    private Employee employee;
    private Employee.Period period;

    public ChoiceTestCases(String name) throws Exception {
        super(name);
    }

    public void setUp() throws Exception {
        xmlContext = new XMLContext(new EmployeeProject());
        xmlValidator = xmlContext.createValidator();
        cal = Calendar.getInstance();
        cal.clear();
        cal.set(START_YYYY, START_MM, START_DD);
    }

    public void testValidEmployee() throws Exception {
        employee = new Employee();

        period = employee.getStartDateOrEndDate();
        period.setStartDate(cal);

        employee.setStartDateOrEndDate(period);

        assertTrue("Valid employee reported invalid", xmlValidator.validateRoot(employee));
    }

    /**
     * Since we are dealing with a choice, either a startDate OR an endDate is
     * valid, but not both.
     */
    public void testInvalidEmployee() throws Exception {
        employee = new Employee();

        period = employee.getStartDateOrEndDate();
        period.setStartDate(cal);
        period.setEndDate(cal);

        employee.setStartDateOrEndDate(period);

        assertFalse("Invalid employee found to be valid", xmlValidator.validateRoot(employee));
    }
}
