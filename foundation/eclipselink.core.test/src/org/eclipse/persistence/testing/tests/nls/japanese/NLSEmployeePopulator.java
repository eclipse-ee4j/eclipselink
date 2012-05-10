/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.nls.japanese;

import java.sql.*;
import java.util.*;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;

/**
 * <p><b>Purpose</b>: To build and populate the database for example and testing purposes.
 * This population routine is fairly complex and makes use of the population manager to
 * resolve interrated objects as the employee objects are an interconnection graph of objects.
 *
 * This is not the recomended way to create new objects in your application,
 * this is just the easiest way to create interconnected new example objects from code.
 * Normally in your application the objects will be defined as part of a transactional and user interactive process.
 */
public class NLSEmployeePopulator {
    protected PopulationManager populationManager;

    public NLSEmployeePopulator() {
        this.populationManager = PopulationManager.getDefaultManager();
    }

    public Address addressExample1() {
        Address address = new Address();

        address.setCity("\u3068\u30db\u30c4\u30db\u30bb\u30c8\u30db");
        address.setPostalCode("L5J2B5");
        address.setProvince("\u305d\u305b\u3068");
        address.setStreet("1450 \u3042\u30a6\u30b9\u30aa \u3046\u30c4., \u30c6\u30ca\u30b1\u30c8\u30aa 4");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public Address addressExample10() {
        Address address = new Address();

        address.setCity("\u3046\u30a2\u30b7\u30ad\u30a2\u30c4\u30ce");
        address.setPostalCode("J5J2B5");
        address.setProvince("\u3042\u3057\u3044");
        address.setStreet("1111 \u3059\u30db\u30db\u30c6\u30aa \u3064\u30a8.");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public Address addressExample11() {
        Address address = new Address();

        address.setCity("\u3042\u30c4\u30bb\u30de\u30c4\u30b1\u30db\u30c4");
        address.setPostalCode("W1A2B5");
        address.setProvince("\u305d\u305b\u3068");
        address.setStreet("1 \u305b\u30db\u30cc\u30af\u30aa\u30c4\u30aa \u3048\u30c4\u30b1\u30cb\u30aa");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public Address addressExample12() {
        Address address = new Address();

        address.setCity("\u306e\u30aa\u30b7\u30b7\u30db\u30cc \u3055\u30bb\u30b1\u30ab\u30aa");
        address.setPostalCode("Y5J2N5");
        address.setProvince("\u306e\u3055");
        address.setStreet("1112 \u304d\u30db\u30b7\u30a8 \u3064\u30ca\u30c6\u30af \u30c4\u30a8.");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public Address addressExample2() {
        Address address = new Address();

        address.setCity("\u305d\u30c8\u30c8\u30a2\u30cc\u30a2");
        address.setPostalCode("K5J2B5");
        address.setProvince("\u305d\u305b\u3068");
        address.setStreet("12 \u3059\u30aa\u30c4\u30b1\u30cb\u30a2\u30b7 \u3064\u30a8., \u30c6\u30ca\u30b1\u30c8\u30aa 5");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public Address addressExample3() {
        Address address = new Address();

        address.setCity("\u305f\u30aa\u30c4\u30c8\u30af");
        address.setPostalCode("Y3Q2N9");
        address.setProvince("\u305d\u305b\u3068");
        address.setStreet("234 \u3051'\u30b9 \u3057\u30db\u30c6\u30c8 \u3057\u30a2\u30bb\u30aa");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public Address addressExample4() {
        Address address = new Address();

        address.setCity("\u305f\u30c4\u30b1\u30bb\u30a6\u30aa \u3064\u30ca\u30de\u30aa\u30c4\u30c8");
        address.setPostalCode("K3k5DD");
        address.setProvince("\u3044\u3046");
        address.setStreet("3254 \u3064\u30aa\u30a2\u30b7 \u3046\u30db\u30b7\u30a8 \u305f\u30b7\u30a2\u30a6\u30aa");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public Address addressExample5() {
        Address address = new Address();

        address.setCity("\u306b\u30a2\u30bb\u30a6\u30db\u30ca\u30cb\u30aa\u30c4");
        address.setPostalCode("N5J2N5");
        address.setProvince("\u3044\u3046");
        address.setStreet("1111 \u3059\u30db\u30ca\u30bb\u30c8\u30a2\u30b1\u30bb \u3044\u30b7\u30cb\u30a8. \u304b\u30b7\u30db\u30db\u30c4 53, \u30c6\u30ca\u30b1\u30c8\u30aa 6");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public Address addressExample6() {
        Address address = new Address();

        address.setCity("\u3059\u30db\u30bb\u30c8\u30c4\u30aa\u30a2\u30b7");
        address.setPostalCode("Q2S5Z5");
        address.setProvince("\u3061\u306a\u304a");
        address.setStreet("1 \u304f\u30a2\u30a4\u30c6 \u305f\u30b7\u30a2\u30a6\u30aa");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public Address addressExample7() {
        Address address = new Address();

        address.setCity("\u3059\u30aa\u30c8\u30a6\u30a2\u30b7\u30ab\u30aa");
        address.setPostalCode("Y4F7V6");
        address.setProvince("\u305d\u305b\u3068");
        address.setStreet("2 \u3042\u30bb\u30a8\u30aa\u30c4\u30c6\u30db\u30bb \u3064\u30a8.");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public Address addressExample8() {
        Address address = new Address();

        address.setCity("\u306b\u30b1\u30a6\u30c8\u30db\u30c4\u30b1\u30a2");
        address.setPostalCode("Z5J2N5");
        address.setProvince("\u3044\u3046");
        address.setStreet("382 \u304f\u30ce\u30a8\u30aa \u305f\u30a2\u30c4\u30b5");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public Address addressExample9() {
        Address address = new Address();

        address.setCity("\u3066\u30b9\u30b1\u30c8\u30af \u304b\u30a2\u30b7\u30b7\u30c6");
        address.setPostalCode("C6C6C6");
        address.setProvince("\u305d\u305b\u3068");
        address.setStreet("1 \u3046\u30af\u30db\u30a6\u30db\u30b7\u30a2\u30c8\u30aa \u3048\u30c4\u30b1\u30cb\u30aa");
        address.setCountry("\u3046\u30a2\u30bb\u30a2\u30a8\u30a2");
        return address;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample1() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u3044\u30db\u30a4");
            employee.setLastName("\u3066\u30b9\u30b1\u30c8\u30af");
            employee.setMale();
            employee.setSalary(35000);
            employee.setPeriod(employmentPeriodExample1());
            employee.setAddress(addressExample1());
            employee.addResponsibility("\u3059\u30a2\u30b5\u30aa \u30c8\u30af\u30aa \u30a6\u30db\u30ab\u30ab\u30aa\u30aa.");
            employee.addResponsibility("\u3046\u30b7\u30aa\u30a2\u30bb \u30c8\u30af\u30aa \u30b5\u30b1\u30c8\u30a6\u30af\u30aa\u30bb.");
            employee.addPhoneNumber(phoneNumberExample1());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample10() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u3053\u30b1\u30b7\u30b7");
            employee.setLastName("\u3059\u30a2\u30ce");
            employee.setFemale();
            employee.setPeriod(employmentPeriodExample10());
            employee.setAddress(addressExample10());
            employee.setSalary(56232);
            employee.addPhoneNumber(phoneNumberExample1());
            employee.addPhoneNumber(phoneNumberExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample11() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u3066\u30a2\u30c4\u30a2\u30af-\u30b7\u30db\u30db");
            employee.setLastName("\u3066\u30b9\u30b1\u30c8\u30c8\u30ce");
            employee.setFemale();
            employee.setPeriod(employmentPeriodExample11());
            employee.setAddress(addressExample11());
            employee.setSalary(75000);
            employee.addPhoneNumber(phoneNumberExample2());
            employee.addPhoneNumber(phoneNumberExample3());
            employee.addPhoneNumber(phoneNumberExample4());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample12() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u3053\u30b1\u30b9-\u30a4\u30db\u30a4");
            employee.setLastName("\u3053\u30aa\u30ab\u30ab\u30aa\u30c4\u30c6\u30db\u30bb");
            employee.setMale();
            employee.setPeriod(employmentPeriodExample12());
            employee.setAddress(addressExample12());
            employee.setSalary(50000);
            employee.addPhoneNumber(phoneNumberExample3());
            employee.addPhoneNumber(phoneNumberExample4());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample2() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u3053\u30db\u30af\u30bb");
            employee.setLastName("\u306c\u30a2\u30ce");
            employee.setMale();
            employee.setSalary(53000);
            employee.setNormalHours(new Time[] { Helper.timeFromHourMinuteSecond(8, 0, 0), Helper.timeFromHourMinuteSecond(17, 30, 0) });
            employee.setPeriod(employmentPeriodExample2());
            employee.setAddress(addressExample2());
            employee.addResponsibility("\u304b\u30b1\u30c4\u30aa \u30de\u30aa\u30db\u30de\u30b7\u30aa \u30ab\u30db\u30c4 \u30ad\u30db\u30db\u30ab\u30b1\u30bb\u30ad \u30db\u30ab\u30ab.");
            employee.addResponsibility("\u304f\u30b1\u30c4\u30aa \u30de\u30aa\u30db\u30de\u30b7\u30aa \u30cc\u30af\u30aa\u30bb \u30b9\u30db\u30c4\u30aa \u30de\u30aa\u30db\u30de\u30b7\u30aa \u30a2\u30c4\u30aa \u30c4\u30aa\u30c1\u30ca\u30b1\u30c4\u30aa\u30a8.");

            employee.addPhoneNumber(phoneNumberExample1());
            employee.addPhoneNumber(phoneNumberExample6());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample3() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u3046\u30af\u30a2\u30c4\u30b7\u30aa\u30c6");
            employee.setLastName("\u3046\u30af\u30a2\u30bb\u30b7\u30aa\u30ce");
            employee.setMale();
            employee.setSalary(43000);
            employee.setNormalHours(new Time[] { Helper.timeFromHourMinuteSecond(7, 0, 0), Helper.timeFromHourMinuteSecond(15, 30, 0) });
            employee.setPeriod(employmentPeriodExample6());
            employee.setAddress(addressExample6());
            employee.addResponsibility("\u306c\u30c4\u30b1\u30c8\u30aa \u30b7\u30db\u30c8\u30c6 \u30db\u30ab \u3053\u30a2\u30cb\u30a2 \u30a6\u30db\u30a8\u30aa.");

            employee.addPhoneNumber(phoneNumberExample5());
            employee.addPhoneNumber(phoneNumberExample6());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample4() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u304a\u30b9\u30a2\u30bb\u30ca\u30a2\u30b7");
            employee.setLastName("\u3066\u30b9\u30b1\u30c8\u30af");
            employee.setMale();
            employee.setSalary(49631);
            employee.setNormalHours(new Time[] { Helper.timeFromHourMinuteSecond(6, 45, 0), Helper.timeFromHourMinuteSecond(16, 32, 0) });
            employee.setPeriod(employmentPeriodExample5());
            employee.setAddress(addressExample5());
            employee.addResponsibility("\u304f\u30a2\u30cb\u30aa \u30c8\u30db \u30ab\u30b1\u30cd \u30c8\u30af\u30aa \u3048\u30a2\u30c8\u30a2\u30a4\u30a2\u30c6\u30aa \u30de\u30c4\u30db\u30a4\u30b7\u30aa\u30b9.");

            employee.addPhoneNumber(phoneNumberExample2());
            employee.addPhoneNumber(phoneNumberExample4());
            employee.addPhoneNumber(phoneNumberExample5());
            employee.addPhoneNumber(phoneNumberExample6());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample5() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u3066\u30a2\u30c4\u30a2\u30af");
            employee.setLastName("\u306c\u30a2\u30ce");
            employee.setFemale();
            employee.setSalary(87000);
            employee.setNormalHours(new Time[] { Helper.timeFromHourMinuteSecond(12, 0, 0), Helper.timeFromHourMinuteSecond(20, 0, 30) });
            employee.setPeriod(employmentPeriodExample4());
            employee.setAddress(addressExample4());
            employee.addResponsibility("\u306c\u30c4\u30b1\u30c8\u30aa \u30a6\u30db\u30a8\u30aa \u30a8\u30db\u30a6\u30ca\u30b9\u30aa\u30bb\u30c8\u30a2\u30c8\u30b1\u30db\u30bb.");

            employee.addPhoneNumber(phoneNumberExample1());
            employee.addPhoneNumber(phoneNumberExample6());
            employee.addPhoneNumber(phoneNumberExample3());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample6() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u3059\u30a2\u30c4\u30a6\u30ca\u30c6");
            employee.setLastName("\u3066\u30a2\u30ca\u30bb\u30a8\u30aa\u30c4\u30c6");
            employee.setMale();
            employee.setSalary(54300);
            employee.setPeriod(employmentPeriodExample3());
            employee.setAddress(addressExample3());
            employee.addResponsibility("\u306c\u30c4\u30b1\u30c8\u30aa \u30ca\u30c6\u30aa\u30c4 \u30c6\u30de\u30aa\u30a6\u30b1\u30ab\u30b1\u30a6\u30a2\u30c8\u30b1\u30db\u30bb\u30c6.");

            employee.addPhoneNumber(phoneNumberExample6());
            employee.addPhoneNumber(phoneNumberExample1());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample7() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u305b\u30a2\u30bb\u30a6\u30ce");
            employee.setLastName("\u306c\u30af\u30b1\u30c8\u30aa");
            employee.setFemale();
            employee.setSalary(31000);
            employee.setPeriod(employmentPeriodExample7());
            employee.setAddress(addressExample7());

            employee.addPhoneNumber(phoneNumberExample3());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample8() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u304b\u30c4\u30aa\u30a8");
            employee.setLastName("\u3053\u30db\u30bb\u30aa\u30c6");
            employee.setMale();
            employee.setSalary(500000);
            employee.setPeriod(employmentPeriodExample8());
            employee.setAddress(addressExample8());

            employee.addPhoneNumber(phoneNumberExample4());
            employee.addPhoneNumber(phoneNumberExample6());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee basicEmployeeExample9() {
        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = createEmployee();

        try {
            employee.setFirstName("\u3044\u30aa\u30c8\u30c8\u30ce");
            employee.setLastName("\u3053\u30db\u30bb\u30aa\u30c6");
            employee.setFemale();
            employee.setSalary(500001);
            employee.setNormalHours(new Time[] { Helper.timeFromHourMinuteSecond(22, 0, 0), Helper.timeFromHourMinuteSecond(5, 30, 0) });
            employee.setPeriod(employmentPeriodExample9());
            employee.setAddress(addressExample9());

            employee.addPhoneNumber(phoneNumberExample1());
            employee.addPhoneNumber(phoneNumberExample6());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.LargeProject basicLargeProjectExample1() {
        org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("\u3066\u30a2\u30b7\u30aa\u30c6 \u3064\u30aa\u30de\u30db\u30c4\u30c8\u30b1\u30bb\u30ad");
            largeProject.setDescription("\u3042 \u30c4\u30aa\u30de\u30db\u30c4\u30c8\u30b1\u30bb\u30ad \u30a2\u30de\u30de\u30b7\u30b1\u30a6\u30a2\u30c8\u30b1\u30db\u30bb \u30c8\u30db \u30c4\u30aa\u30de\u30db\u30c4\u30c8 \u30db\u30bb \u30c8\u30af\u30aa \u30a6\u30db\u30c4\u30de\u30db\u30c4\u30a2\u30c8\u30b1\u30db\u30bb\u30c6 \u30a8\u30a2\u30c8\u30a2\u30a4\u30a2\u30c6\u30aa \u30c8\u30af\u30c4\u30db\u30ca\u30ad\u30af \u3068\u30db\u30de\u3057\u30b1\u30bb\u30b5.");
            largeProject.setBudget(5000);
            largeProject.setMilestoneVersion(Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1991, 10, 11, 12, 0, 0, 0));

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return largeProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.LargeProject basicLargeProjectExample2() {
        org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("\u3066\u30cc\u30b1\u30c4\u30b7\u30ce \u3048\u30b1\u30c4\u30b7\u30ce");
            largeProject.setDescription("\u3042 \u30c6\u30cc\u30b1\u30c4\u30b7\u30ce \u30a2\u30de\u30de\u30b7\u30b1\u30a6\u30a2\u30c8\u30b1\u30db\u30bb \u30c8\u30db \u30c4\u30aa\u30de\u30db\u30c4\u30c8 \u30db\u30bb \u30c8\u30af\u30aa \u30a6\u30db\u30c4\u30de\u30db\u30c4\u30a2\u30c8\u30b1\u30db\u30bb\u30c6 \u30a8\u30a2\u30c8\u30a2\u30a4\u30a2\u30c6\u30aa \u30c8\u30af\u30c4\u30db\u30ca\u30ad\u30af \u3068\u30db\u30de\u3057\u30b1\u30bb\u30b5.");
            largeProject.setBudget(100.98);
            largeProject.setMilestoneVersion(Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1999, 11, 25, 11, 40, 44, 0));

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return largeProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.LargeProject basicLargeProjectExample3() {
        org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("\u3068\u305d\u305f\u304a\u30b9\u30de\u30b7\u30db\u30ce\u30aa\u30aa \u3059\u30a2\u30bb\u30a2\u30ad\u30aa\u30b9\u30aa\u30bb\u30c8");
            largeProject.setDescription("\u3042 \u30b9\u30a2\u30bb\u30a2\u30ad\u30aa\u30b9\u30aa\u30bb\u30c8 \u30a2\u30de\u30de\u30b7\u30b1\u30a6\u30a2\u30c8\u30b1\u30db\u30bb \u30c8\u30db \u30c4\u30aa\u30de\u30db\u30c4\u30c8 \u30db\u30bb \u30c8\u30af\u30aa \u30a6\u30db\u30c4\u30de\u30db\u30c4\u30a2\u30c8\u30b1\u30db\u30bb\u30c6 \u30a8\u30a2\u30c8\u30a2\u30a4\u30a2\u30c6\u30aa \u30c8\u30af\u30c4\u30db\u30ca\u30ad\u30af \u3068\u30db\u30de\u3057\u30b1\u30bb\u30b5.");
            largeProject.setBudget(4000.98);
            largeProject.setMilestoneVersion(Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1997, 10, 12, 1, 0, 0, 0));

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return largeProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.LargeProject basicLargeProjectExample4() {
        org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("\u304a\u30bb\u30c8\u30aa\u30c4\u30de\u30c4\u30b1\u30c6\u30aa \u3066\u30ce\u30c6\u30c8\u30aa\u30b9");
            largeProject.setDescription("\u3042 \u30aa\u30bb\u30c8\u30aa\u30c4\u30de\u30c4\u30b1\u30c6\u30aa \u30cc\u30b1\u30a8\u30aa \u30a2\u30de\u30de\u30b7\u30b1\u30a6\u30a2\u30c8\u30b1\u30db\u30bb \u30c8\u30db \u30c4\u30aa\u30de\u30db\u30c4\u30c8 \u30db\u30bb \u30c8\u30af\u30aa \u30a6\u30db\u30c4\u30de\u30db\u30c4\u30a2\u30c8\u30b1\u30db\u30bb\u30c6 \u30a8\u30a2\u30c8\u30a2\u30a4\u30a2\u30c6\u30aa \u30c8\u30af\u30c4\u30db\u30ca\u30ad\u30af \u3068\u30db\u30de\u3057\u30b1\u30bb\u30b5.");
            largeProject.setBudget(40.98);
            largeProject.setMilestoneVersion(Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1996, 8, 6, 6, 40, 44, 0));

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return largeProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.LargeProject basicLargeProjectExample5() {
        org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProject = createLargeProject();

        try {
            largeProject.setName("\u305f\u30c4\u30db\u30a4\u30b7\u30aa\u30b9 \u3064\u30aa\u30de\u30db\u30c4\u30c8\u30b1\u30bb\u30ad \u3066\u30ce\u30c6\u30c8\u30aa\u30b9");
            largeProject.setDescription("\u3042 \u305f\u3064\u3066 \u30a2\u30de\u30de\u30b7\u30b1\u30a6\u30a2\u30c8\u30b1\u30db\u30bb \u30c8\u30db \u30c4\u30aa\u30de\u30db\u30c4\u30c8 \u30db\u30bb \u30c8\u30af\u30aa \u30a6\u30db\u30c4\u30de\u30db\u30c4\u30a2\u30c8\u30b1\u30db\u30bb\u30c6 \u30a8\u30a2\u30c8\u30a2\u30a4\u30a2\u30c6\u30aa \u30c8\u30af\u30c4\u30db\u30ca\u30ad\u30af \u3068\u30db\u30de\u3057\u30b1\u30bb\u30b5.");
            largeProject.setBudget(101.98);
            largeProject.setMilestoneVersion(Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1997, 9, 6, 1, 40, 44, 0));

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return largeProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject basicSmallProjectExample1() {
        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("\u304a\u30bb\u30c8\u30aa\u30c4\u30de\u30c4\u30b1\u30c6\u30aa");
            smallProject.setDescription("\u3042 \u30aa\u30bb\u30c8\u30aa\u30c4\u30de\u30c4\u30b1\u30c6\u30aa \u30cc\u30b1\u30a8\u30aa \u30a2\u30de\u30de\u30b7\u30b1\u30a6\u30a2\u30c8\u30b1\u30db\u30bb \u30c8\u30db \u30c4\u30aa\u30de\u30db\u30c4\u30c8 \u30db\u30bb \u30c8\u30af\u30aa \u30a6\u30db\u30c4\u30de\u30db\u30c4\u30a2\u30c8\u30b1\u30db\u30bb\u30c6 \u30a8\u30a2\u30c8\u30a2\u30a4\u30a2\u30c6\u30aa \u30c8\u30af\u30c4\u30db\u30ca\u30ad\u30af \u3068\u30db\u30de\u3057\u30b1\u30bb\u30b5.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject basicSmallProjectExample10() {
        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("\u3066\u30c8\u30a2\u30ab\u30ab \u3061\u30ca\u30aa\u30c4\u30ce \u3068\u30db\u30db\u30b7");
            smallProject.setDescription("\u3042 \u30c8\u30db\u30db\u30b7 \u30c8\u30db \u30af\u30aa\u30b7\u30de \u30c6\u30c8\u30a2\u30ab\u30ab \u30c1\u30ca\u30aa\u30c4\u30ce \u30c8\u30af\u30b1\u30bb\u30ad\u30c6.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject basicSmallProjectExample2() {
        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("\u3066\u30a2\u30b7\u30aa\u30c6 \u3064\u30aa\u30de\u30db\u30c4\u30c8\u30aa\u30c4");
            smallProject.setDescription("\u3042 \u30c4\u30aa\u30de\u30db\u30c4\u30c8\u30b1\u30bb\u30ad \u30a2\u30de\u30de\u30b7\u30b1\u30a6\u30a2\u30c8\u30b1\u30db\u30bb \u30ca\u30c6\u30b1\u30bb\u30ad \u3053\u3048\u3055 \u30c8\u30db \u30c4\u30aa\u30de\u30db\u30c4\u30c8 \u30db\u30bb \u30c8\u30af\u30aa \u30a6\u30db\u30c4\u30de\u30db\u30c4\u30a2\u30c8\u30b1\u30db\u30bb\u30c6 \u30a8\u30a2\u30c8\u30a2\u30a4\u30a2\u30c6\u30aa \u30c8\u30af\u30c4\u30db\u30ca\u30ad\u30af \u3068\u30db\u30de\u3057\u30b1\u30bb\u30b5.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject basicSmallProjectExample3() {
        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("\u3068\u305d\u305f\u304a\u30b9\u30de\u30b7\u30db\u30ce\u30aa\u30aa \u3059\u30a2\u30bb\u30a2\u30ad\u30aa\u30c4");
            smallProject.setDescription("\u3042 \u30b9\u30a2\u30bb\u30a2\u30ad\u30aa\u30b9\u30aa\u30bb\u30c8 \u30a2\u30de\u30de\u30b7\u30b1\u30a6\u30a2\u30c8\u30b1\u30db\u30bb \u30c8\u30db \u30c4\u30aa\u30de\u30db\u30c4\u30c8 \u30db\u30bb \u30c8\u30af\u30aa \u30a6\u30db\u30c4\u30de\u30db\u30c4\u30a2\u30c8\u30b1\u30db\u30bb\u30c6 \u30a8\u30a2\u30c8\u30a2\u30a4\u30a2\u30c6\u30aa \u30c8\u30af\u30c4\u30db\u30ca\u30ad\u30af \u3068\u30db\u30de\u3057\u30b1\u30bb\u30b5.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject basicSmallProjectExample4() {
        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("\u305f\u30c4\u30db\u30a4\u30b7\u30aa\u30b9 \u3064\u30aa\u30de\u30db\u30c4\u30c8\u30aa\u30c4");
            smallProject.setDescription("\u3042 \u305f\u3064\u3066 \u30a2\u30de\u30de\u30b7\u30b1\u30a6\u30a2\u30c8\u30b1\u30db\u30bb \u30c8\u30db \u30c4\u30aa\u30de\u30db\u30c4\u30c8 \u30db\u30bb \u30c8\u30af\u30aa \u30a6\u30db\u30c4\u30de\u30db\u30c4\u30a2\u30c8\u30b1\u30db\u30bb\u30c6 \u30a8\u30a2\u30c8\u30a2\u30a4\u30a2\u30c6\u30aa \u30c8\u30af\u30c4\u30db\u30ca\u30ad\u30af \u3068\u30db\u30de\u3057\u30b1\u30bb\u30b5.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject basicSmallProjectExample5() {
        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("\u3066\u30cc\u30b1\u30c4\u30b7\u30ce \u3048\u30b1\u30c4\u30b7");
            smallProject.setDescription("\u3042 \u30c6\u30cc\u30b1\u30c4\u30b7\u30b7\u30ce \u30a2\u30de\u30de\u30b7\u30b1\u30a6\u30a2\u30c8\u30b1\u30db\u30bb \u30c8\u30db \u30c4\u30aa\u30de\u30db\u30c4\u30c8 \u30db\u30bb \u30c8\u30af\u30aa \u30a6\u30db\u30c4\u30de\u30db\u30c4\u30a2\u30c8\u30b1\u30db\u30bb\u30c6 \u30a8\u30a2\u30c8\u30a2\u30a4\u30a2\u30c6\u30aa \u30c8\u30af\u30c4\u30db\u30ca\u30ad\u30af \u3068\u30db\u30de\u3057\u30b1\u30bb\u30b5.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject basicSmallProjectExample6() {
        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("\u3044\u30b7\u30aa\u30aa\u30de \u3044\u30b7\u30db\u30a4");
            smallProject.setDescription("\u3044\u30b7\u30aa\u30aa\u30de \u30a4\u30b7\u30db\u30a4 \u30b1\u30c6 \u30b3\u30ca\u30c6\u30c8 \u30a2 \u30bb\u30b1\u30a6\u30aa \u30c8\u30db\u30ce.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject basicSmallProjectExample7() {
        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("\u3059\u30a2\u30c4\u30b5\u30aa\u30c8\u30b1\u30bb\u30ad \u3061\u30ca\u30aa\u30c4\u30ce \u3068\u30db\u30db\u30b7");
            smallProject.setDescription("\u3042 \u30c8\u30db\u30db\u30b7 \u30c8\u30db \u30af\u30aa\u30b7\u30de \u30b9\u30a2\u30c4\u30b5\u30aa\u30c8\u30b1\u30bb\u30ad \u30c1\u30ca\u30aa\u30c4\u30ce \u30c8\u30af\u30b1\u30bb\u30ad\u30c6.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject basicSmallProjectExample8() {
        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("\u3066\u30af\u30b1\u30de\u30de\u30b1\u30bb\u30ad \u3061\u30ca\u30aa\u30c4\u30ce \u3068\u30db\u30db\u30b7");
            smallProject.setDescription("\u3042 \u30c8\u30db\u30db\u30b7 \u30c8\u30db \u30af\u30aa\u30b7\u30de \u30c6\u30af\u30b1\u30de\u30de\u30b1\u30bb\u30ad \u30c1\u30ca\u30aa\u30c4\u30ce \u30c8\u30af\u30b1\u30bb\u30ad\u30c6.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject basicSmallProjectExample9() {
        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = createSmallProject();

        try {
            smallProject.setName("\u3042\u30a6\u30a6\u30db\u30ca\u30bb\u30c8\u30b1\u30bb\u30ad \u3061\u30ca\u30aa\u30c4\u30ce \u3068\u30db\u30db\u30b7");
            smallProject.setDescription("\u3042 \u30c8\u30db\u30db\u30b7 \u30c8\u30db \u30af\u30aa\u30b7\u30de \u30a2\u30a6\u30a6\u30db\u30ca\u30bb\u30c8\u30b1\u30bb\u30ad \u30c1\u30ca\u30aa\u30c4\u30ce \u30c8\u30af\u30b1\u30bb\u30ad\u30c6.");

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return smallProject;
    }

    /**
    *    Call all of the example methods in this system to guarantee that all our objects
    *    are registered in the population manager
    */
    public void buildExamples() {
        // First ensure that no preivous examples are hanging around.
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(NLSEmployee.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(SmallProject.class);
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(LargeProject.class);

        employeeExample1();
        employeeExample2();
        employeeExample3();
        employeeExample4();
        employeeExample5();
        employeeExample6();
        employeeExample7();
        employeeExample8();
        employeeExample9();
        employeeExample10();
        employeeExample11();
        employeeExample12();
        largeProjectExample1();
        largeProjectExample2();
        largeProjectExample3();
        largeProjectExample4();
        largeProjectExample5();
        smallProjectExample1();
        smallProjectExample2();
        smallProjectExample3();
        smallProjectExample4();
        smallProjectExample5();
        smallProjectExample6();
        smallProjectExample7();
        smallProjectExample8();
        smallProjectExample9();
        smallProjectExample10();
    }

    protected boolean containsObject(Class domainClass, String identifier) {
        return populationManager.containsObject(domainClass, identifier);
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee createEmployee() {
        return new NLSEmployee();
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.LargeProject createLargeProject() {
        return new LargeProject();
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject createSmallProject() {
        return new SmallProject();
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample1() {
        if (containsObject(NLSEmployee.class, "0001")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0001");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample1();
        registerObject(NLSEmployee.class, employee, "0001");

        try {
            employee.addManagedEmployee(employeeExample3());
            employee.addManagedEmployee(employeeExample4());
            employee.addManagedEmployee(employeeExample5());

            employee.addProject(smallProjectExample1());
            employee.addProject(smallProjectExample2());
            employee.addProject(smallProjectExample3());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample10() {
        if (containsObject(NLSEmployee.class, "0010")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0010");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample10();
        try {
            employee.addManagedEmployee(employeeExample12());
        } catch (Exception exception) {
        }
        registerObject(NLSEmployee.class, employee, "0010");

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample11() {
        if (containsObject(NLSEmployee.class, "0011")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0011");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample11();
        try {
            employee.addManagedEmployee(employeeExample7());
        } catch (Exception exception) {
        }
        registerObject(NLSEmployee.class, employee, "0011");

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample12() {
        if (containsObject(NLSEmployee.class, "0012")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0012");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample12();
        registerObject(NLSEmployee.class, employee, "0012");

        try {
            employee.addManagedEmployee(employeeExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample2() {
        if (containsObject(NLSEmployee.class, "0002")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0002");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample2();
        registerObject(NLSEmployee.class, employee, "0002");

        try {
            employee.addManagedEmployee(employeeExample6());
            employee.addManagedEmployee(employeeExample1());

            employee.addProject(smallProjectExample4());
            employee.addProject(smallProjectExample5());
            employee.addProject(largeProjectExample1());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample3() {
        if (containsObject(NLSEmployee.class, "0003")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0003");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample3();
        registerObject(NLSEmployee.class, employee, "0003");

        try {
            employee.addProject(smallProjectExample4());
            employee.addProject(largeProjectExample4());
            employee.addProject(largeProjectExample5());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample4() {
        if (containsObject(NLSEmployee.class, "0004")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0004");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample4();
        registerObject(NLSEmployee.class, employee, "0004");

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample5() {
        if (containsObject(NLSEmployee.class, "0005")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0005");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample5();
        registerObject(NLSEmployee.class, employee, "0005");

        try {
            employee.addProject(smallProjectExample4());
            employee.addProject(largeProjectExample1());
            employee.addProject(largeProjectExample3());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample6() {
        if (containsObject(NLSEmployee.class, "0006")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0006");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample6();
        registerObject(NLSEmployee.class, employee, "0006");

        try {
            employee.addProject(largeProjectExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample7() {
        if (containsObject(NLSEmployee.class, "0007")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0007");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample7();
        registerObject(NLSEmployee.class, employee, "0007");

        try {
            employee.addProject(largeProjectExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample8() {
        if (containsObject(NLSEmployee.class, "0008")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0008");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample8();
        registerObject(NLSEmployee.class, employee, "0008");

        return employee;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.Employee employeeExample9() {
        if (containsObject(NLSEmployee.class, "0009")) {
            return (org.eclipse.persistence.testing.models.employee.interfaces.Employee)getObject(NLSEmployee.class, "0009");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.Employee employee = basicEmployeeExample9();
        registerObject(NLSEmployee.class, employee, "0009");

        return employee;
    }

    public EmploymentPeriod employmentPeriodExample1() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setEndDate(Helper.dateFromYearMonthDate(1996, 0, 1));
        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(1993, 0, 1));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample10() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(1991, 10, 11));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample11() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setEndDate(Helper.dateFromYearMonthDate(1996, 0, 1));
        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(1993, 0, 1));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample12() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setEndDate(Helper.dateFromYearMonthDate(1901, 11, 31));
        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(1995, 0, 12));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample2() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(1991, 10, 11));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample3() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setEndDate(Helper.dateFromYearMonthDate(1901, 11, 31));
        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(1995, 0, 12));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample4() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setEndDate(Helper.dateFromYearMonthDate(19101, 6, 31));
        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(1995, 4, 1));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample5() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setEndDate(Helper.dateFromYearMonthDate(1901, 11, 31));
        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(19-5, 0, 1));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample6() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setEndDate(Helper.dateFromYearMonthDate(1901, 11, 31));
        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(1995, 0, 12));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample7() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setEndDate(Helper.dateFromYearMonthDate(1996, 0, 1));
        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(1993, 0, 1));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample8() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setEndDate(Helper.dateFromYearMonthDate(1901, 11, 31));
        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(1895, 0, 1));
        return employmentPeriod;
    }

    public EmploymentPeriod employmentPeriodExample9() {
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();

        employmentPeriod.setEndDate(Helper.dateFromYearMonthDate(1901, 11, 31));
        employmentPeriod.setStartDate(Helper.dateFromYearMonthDate(1895, 0, 1));
        return employmentPeriod;
    }

    protected Vector getAllObjects() {
        return populationManager.getAllObjects();
    }

    public Vector getAllObjectsForClass(Class domainClass) {
        return populationManager.getAllObjectsForClass(domainClass);
    }

    protected Object getObject(Class domainClass, String identifier) {
        return populationManager.getObject(domainClass, identifier);
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProjectExample1() {
        if (containsObject(LargeProject.class, "0001")) {
            return (LargeProject)getObject(LargeProject.class, "0001");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProject = basicLargeProjectExample1();
        registerObject(largeProject, "0001");

        try {
            largeProject.setTeamLeader(employeeExample2());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return largeProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProjectExample2() {
        if (containsObject(LargeProject.class, "0002")) {
            return (LargeProject)getObject(LargeProject.class, "0002");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProject = basicLargeProjectExample2();
        registerObject(largeProject, "0002");
        return largeProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProjectExample3() {
        if (containsObject(LargeProject.class, "0003")) {
            return (LargeProject)getObject(LargeProject.class, "0003");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProject = basicLargeProjectExample3();
        registerObject(largeProject, "0003");
        return largeProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProjectExample4() {
        if (containsObject(LargeProject.class, "0004")) {
            return (LargeProject)getObject(LargeProject.class, "0004");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProject = basicLargeProjectExample4();
        registerObject(largeProject, "0004");

        try {
            largeProject.setTeamLeader(employeeExample3());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return largeProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProjectExample5() {
        if (containsObject(LargeProject.class, "0005")) {
            return (LargeProject)getObject(LargeProject.class, "0005");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.LargeProject largeProject = basicLargeProjectExample5();
        registerObject(largeProject, "0005");

        try {
            largeProject.setTeamLeader(employeeExample5());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        ;

        return largeProject;
    }

    public PhoneNumber phoneNumberExample1() {
        return new PhoneNumber("\u306c\u30db\u30c4\u30b5", "613", "2258812");
    }

    public PhoneNumber phoneNumberExample2() {
        return new PhoneNumber("\u306c\u30db\u30c4\u30b5 \u304b\u30a2\u30cd", "613", "2255943");
    }

    public PhoneNumber phoneNumberExample3() {
        return new PhoneNumber("\u304f\u30db\u30b9\u30aa", "613", "5551234");
    }

    public PhoneNumber phoneNumberExample4() {
        return new PhoneNumber("\u3046\u30aa\u30b7\u30b7\u30ca\u30b7\u30a2\u30c4", "416", "5551111");
    }

    public PhoneNumber phoneNumberExample5() {
        return new PhoneNumber("\u305f\u30a2\u30ad\u30aa\u30c4", "976", "5556666");
    }

    public PhoneNumber phoneNumberExample6() {
        return new PhoneNumber("\u3051\u3066\u3048\u305b", "905", "5553691");
    }

    protected void registerObject(Class domainClass, Object domainObject, String identifier) {
        populationManager.registerObject(domainClass, domainObject, identifier);
    }

    protected void registerObject(Object domainObject, String identifier) {
        populationManager.registerObject(domainObject, identifier);
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProjectExample1() {
        if (containsObject(SmallProject.class, "0001")) {
            return (SmallProject)getObject(SmallProject.class, "0001");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = basicSmallProjectExample1();
        registerObject(smallProject, "0001");
        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProjectExample10() {
        if (containsObject(SmallProject.class, "0010")) {
            return (SmallProject)getObject(SmallProject.class, "0010");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = basicSmallProjectExample10();
        registerObject(smallProject, "0010");

        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProjectExample2() {
        if (containsObject(SmallProject.class, "0002")) {
            return (SmallProject)getObject(SmallProject.class, "0002");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = basicSmallProjectExample2();
        registerObject(smallProject, "0002");
        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProjectExample3() {
        if (containsObject(SmallProject.class, "0003")) {
            return (SmallProject)getObject(SmallProject.class, "0003");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = basicSmallProjectExample3();
        registerObject(smallProject, "0003");
        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProjectExample4() {
        if (containsObject(SmallProject.class, "0004")) {
            return (SmallProject)getObject(SmallProject.class, "0004");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = basicSmallProjectExample4();
        registerObject(smallProject, "0004");
        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProjectExample5() {
        if (containsObject(SmallProject.class, "0005")) {
            return (SmallProject)getObject(SmallProject.class, "0005");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = basicSmallProjectExample5();
        registerObject(smallProject, "0005");
        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProjectExample6() {
        if (containsObject(SmallProject.class, "0006")) {
            return (SmallProject)getObject(SmallProject.class, "0006");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = basicSmallProjectExample6();
        registerObject(smallProject, "0006");
        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProjectExample7() {
        if (containsObject(SmallProject.class, "0007")) {
            return (SmallProject)getObject(SmallProject.class, "0007");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = basicSmallProjectExample7();
        registerObject(smallProject, "0007");
        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProjectExample8() {
        if (containsObject(SmallProject.class, "0008")) {
            return (SmallProject)getObject(SmallProject.class, "0008");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = basicSmallProjectExample8();
        registerObject(smallProject, "0008");
        return smallProject;
    }

    public org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProjectExample9() {
        if (containsObject(SmallProject.class, "0009")) {
            return (SmallProject)getObject(SmallProject.class, "0009");
        }

        org.eclipse.persistence.testing.models.employee.interfaces.SmallProject smallProject = basicSmallProjectExample9();
        ;
        registerObject(smallProject, "0009");
        return smallProject;
    }
}
