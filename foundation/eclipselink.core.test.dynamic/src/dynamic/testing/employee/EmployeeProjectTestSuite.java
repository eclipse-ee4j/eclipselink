/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *               http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     mnorman - tweaks to work from Ant command-line,
 *               et database properties from System, etc.
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package dynamic.testing.employee;

//javase imports
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

import dynamic.testing.DynamicEmployeeEntityComparator;

//domain-specific (testing) imports
import static dynamic.testing.DynamicTestingHelper.createLogin;
import static dynamic.testing.DynamicTestingHelper.logLevel;

/*
 * Test cases verifying the use of employee-project.xml 
 */
public class EmployeeProjectTestSuite  {

    static final String PROJECT_XML = "dynamic/testing/Employee_utf8.xml";
    
    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;
    static DynamicEntity[] employees = null;
    static DynamicEntity[] addresses = null;
    static DynamicEntity[] projects = null;

    @BeforeClass
    public static void setUp() {
        DynamicClassLoader dcl = new DynamicClassLoader(EmployeeProjectTestSuite.class.getClassLoader());
        Project project = null;
        try {
            project = DynamicTypeBuilder.loadDynamicProject(PROJECT_XML, createLogin(), dcl);
        }
        catch (IOException e) {
            //e.printStackTrace();
            fail("cannot find Employee_utf8.xml");
        }
        session = project.createDatabaseSession();
        if (SessionLog.OFF == logLevel) {
            session.dontLogMessages();
        }
        else {
            session.setLogLevel(logLevel); 
        }
        dynamicHelper = new DynamicHelper(session);
        session.login();
        new SchemaManager(session).replaceDefaultTables();
    }

    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE DX_SALARY");
        session.executeNonSelectingSQL("DROP TABLE DX_PROJ_EMP");
        session.executeNonSelectingSQL("DROP TABLE DX_PROJECT");
        session.executeNonSelectingSQL("DROP TABLE DX_PHONE");
        session.executeNonSelectingSQL("DROP TABLE DX_EMPLOYEE");
        session.executeNonSelectingSQL("DROP TABLE DX_ADDRESS");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @Test
    public void verifyEmployeeDescriptor() {
        ClassDescriptor descriptor = session.getDescriptorForAlias("Employee");
        assertNotNull(descriptor);
        assertEquals("Employee", descriptor.getAlias());
        assertNull(descriptor.getInheritancePolicyOrNull());

        // Address Mapping
        OneToOneMapping addrMapping = (OneToOneMapping) descriptor.getMappingForAttributeName("address");
        assertNotNull(addrMapping);
        assertTrue(addrMapping.isPrivateOwned());
        assertEquals(session.getDescriptorForAlias("Address"), addrMapping.getReferenceDescriptor());

        // PhoneNumber Mapping
        OneToManyMapping phoneMapping = (OneToManyMapping) descriptor.getMappingForAttributeName("phoneNumbers");
        assertNotNull(phoneMapping);
        assertTrue(phoneMapping.isPrivateOwned());
        assertEquals(session.getDescriptorForAlias("PhoneNumber"), phoneMapping.getReferenceDescriptor());

        // Manager Mapping
        OneToOneMapping managerMapping = (OneToOneMapping) descriptor.getMappingForAttributeName("manager");
        assertNotNull(managerMapping);
        assertFalse(managerMapping.isPrivateOwned());
        assertEquals(descriptor, managerMapping.getReferenceDescriptor());

        // Managed Employees Mapping
        OneToManyMapping managedEmployeesMapping = (OneToManyMapping) descriptor.getMappingForAttributeName("managedEmployees");
        assertNotNull(managedEmployeesMapping);
        assertFalse(managedEmployeesMapping.isPrivateOwned());
        assertEquals(descriptor, managedEmployeesMapping.getReferenceDescriptor());

        // Projects Mapping
        ManyToManyMapping projectsMapping = (ManyToManyMapping) descriptor.getMappingForAttributeName("projects");
        assertNotNull(projectsMapping);
        assertFalse(projectsMapping.isPrivateOwned());
        assertEquals(session.getDescriptorForAlias("Project"), projectsMapping.getReferenceDescriptor());
    }

    @Test
    public void verifyAddressDescriptor() {
        ClassDescriptor descriptor = session.getDescriptorForAlias("Address");
        assertNotNull(descriptor);
        assertEquals("Address", descriptor.getAlias());
        assertNull(descriptor.getInheritancePolicyOrNull());
    }

    @Test
    public void verifyPhoneNumberDescriptor() {
        ClassDescriptor descriptor = session.getDescriptorForAlias("PhoneNumber");
        assertNotNull(descriptor);
        assertEquals("PhoneNumber", descriptor.getAlias());
        assertNull(descriptor.getInheritancePolicyOrNull());
    }

    @Test
    public void verifyProjectDescriptor() {
        ClassDescriptor descriptor = session.getDescriptorForAlias("Project");
        assertNotNull(descriptor);
        assertEquals("Project", descriptor.getAlias());
        //assertNotNull(descriptor.getInheritancePolicyOrNull());
        assertNull(descriptor.getInheritancePolicyOrNull());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void createNewInstance() {
        DynamicType employeeType = dynamicHelper.getType("Employee");
        DynamicType periodType = dynamicHelper.getType("EmploymentPeriod");

        DynamicEntity entity = employeeType.newDynamicEntity();
        // entity.set("id", 1);
        entity.set("firstName", "First");
        entity.set("lastName", "Last");
        entity.set("salary", 12345);

        DynamicEntity period = periodType.newDynamicEntity();
        period.set("startDate", Calendar.getInstance());

        entity.set("period", period);

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(entity);
        uow.commit();

        ReportQuery countQuery = dynamicHelper.newReportQuery("Employee", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        assertEquals(1, ((Number) session.executeQuery(countQuery)).intValue());

        List<DynamicEntity> allObjects = session.readAllObjects(employeeType.getJavaClass());
        assertEquals(1, allObjects.size());
        
        session.release();
    }
    
    @Ignore
    public void populate() {
        employees = new DynamicEntity[] {
            basicEmployeeExample1(),
            basicEmployeeExample2(),
            basicEmployeeExample3(),
            basicEmployeeExample4(),
            basicEmployeeExample5(),
            basicEmployeeExample6(),
            basicEmployeeExample7(),
            basicEmployeeExample8(),
            basicEmployeeExample9(),
            basicEmployeeExample10(),
            basicEmployeeExample11(),
            basicEmployeeExample12()
        };
        projects = new DynamicEntity[] {
            basicProjectExample1(), 
            basicProjectExample2(),
            basicProjectExample3(),
            basicProjectExample4(),
            basicProjectExample5(),
            basicProjectExample7(),
            basicProjectExample8(),
            basicProjectExample9(),
            basicProjectExample10()
        };
        // Setup management hierarchy
        addManagedEmployees(0, new int[]{ 2, 3, 4 });
        addManagedEmployees(1, new int[]{ 5, 0 });
        addManagedEmployees(2, new int[]{});
        addManagedEmployees(3, new int[]{});
        addManagedEmployees(4, new int[]{});
        addManagedEmployees(5, new int[]{});
        addManagedEmployees(6, new int[]{});
        addManagedEmployees(7, new int[]{});
        addManagedEmployees(8, new int[]{});
        addManagedEmployees(9, new int[]{ 7, 8, 10, 11 });
        addManagedEmployees(10, new int[]{ 6 });
        addManagedEmployees(11, new int[]{ 1 });
        // Setup Employee-Project associations
        addProjects(0, new int[] { 0, 1, 2 });
        addProjects(1, new int[] { 3, 4, 0 });
        addProjects(2, new int[] { 3 });
        addProjects(4, new int[] { 3, 1 });
        
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample1() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "Bob");
        employee.set("lastName", "Smith");
        employee.set("gender", "Male");
        employee.set("salary", 35000);

        setPeriod(employee, new Date(1993, 0, 1), new Date(1996, 0, 1));

        DynamicEntity address = newInstance("Address");
        address.set("city", "Toronto");
        address.set("postalCode", "L5J2B5");
        address.set("province", "ONT");
        address.set("street", "1450 Acme Cr., Suite 4");
        address.set("country", "Canada");
        employee.set("address", address);

        // employee.add("responsibilities", "Water the office plants.");
        // employee.add("responsibilities", "Maintain the kitchen facilities.");
        addPhoneNumber(employee, "Work", "613", "5558812");

        return employee;
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample10() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "Jill");
        employee.set("lastName", "May");
        employee.set("gender", "Female");

        setPeriod(employee, new Date(1991, 10, 111), null);

        DynamicEntity address = newInstance("Address");
        address.set("city", "Calgary");
        address.set("postalCode", "J5J2B5");
        address.set("province", "AB");
        address.set("street", "1111 Mooseland Rd.");
        address.set("country", "Canada");
        employee.set("address", address);

        employee.set("salary", 56232);
        addPhoneNumber(employee, "Work", "613", "5558812");
        addPhoneNumber(employee, "Work Fax", "613", "5555943");

        return employee;
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample11() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "Sarah-Lou");
        employee.set("lastName", "Smitty");
        employee.set("gender", "Female");

        setPeriod(employee, new Date(1993, 0, 1), new Date(1996, 0, 1));

        DynamicEntity address = newInstance("Address");
        address.set("city", "Arnprior");
        address.set("postalCode", "W1A2B5");
        address.set("province", "ONT");
        address.set("street", "1 Hawthorne Drive");
        address.set("country", "Canada");
        employee.set("address", address);

        employee.set("salary", 75000);
        addPhoneNumber(employee, "Work Fax", "613", "5555943");
        addPhoneNumber(employee, "Home", "613", "5551234");
        addPhoneNumber(employee, "Cellular", "416", "5551111");

        return employee;
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample12() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "Jim-Bob");
        employee.set("lastName", "Jefferson");
        employee.set("gender", "Male");

        setPeriod(employee, new Date(1995, 0, 12), new Date(2001, 11, 31));

        DynamicEntity address = newInstance("Address");
        address.set("city", "Yellowknife");
        address.set("postalCode", "Y5J2N5");
        address.set("province", "YK");
        address.set("street", "1112 Gold Rush Rd.");
        address.set("country", "Canada");
        employee.set("address", address);

        employee.set("salary", 50000);
        addPhoneNumber(employee, "Home", "613", "5551234");
        addPhoneNumber(employee, "Cellular", "416", "5551111");

        return employee;
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample2() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "John");
        employee.set("lastName", "Way");
        employee.set("gender", "Male");
        employee.set("salary", 53000);

        setPeriod(employee, new Date(1991, 10, 11), null);

        DynamicEntity address = newInstance("Address");
        address.set("city", "Ottawa");
        address.set("postalCode", "K5J2B5");
        address.set("province", "ONT");
        address.set("street", "12 Merivale Rd., Suite 5");
        address.set("country", "Canada");
        employee.set("address", address);

        // employee.add("responsibilities",
        // "Hire people when more people are required.");
        // employee.add("responsibilities",
        // "Lay off employees when less people are required.");
        addPhoneNumber(employee, "Work", "613", "5558812");
        addPhoneNumber(employee, "ISDN", "905", "5553691");

        return employee;
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample3() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "Charles");
        employee.set("lastName", "Chanley");
        employee.set("gender", "Male");
        employee.set("salary", 43000);

        setPeriod(employee, new Date(1995, 0, 1), new Date(2001, 11, 31));

        DynamicEntity address = newInstance("Address");
        address.set("city", "Montreal");
        address.set("postalCode", "Q2S5Z5");
        address.set("province", "QUE");
        address.set("street", "1 Canadien Place");
        address.set("country", "Canada");
        employee.set("address", address);

        // employee.add("responsibilities",
        // "Perform code reviews as required.");
        addPhoneNumber(employee, "Pager", "976", "5556666");
        addPhoneNumber(employee, "ISDN", "905", "5553691");

        return employee;
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample4() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "Emanual");
        employee.set("lastName", "Smith");
        employee.set("gender", "Male");
        employee.set("salary", 49631);

        setPeriod(employee, new Date(2001, 11, 31), new Date(1995, 0, 1));

        DynamicEntity address = newInstance("Address");
        address.set("city", "Vancouver");
        address.set("postalCode", "N5J2N5");
        address.set("province", "BC");
        address.set("street", "20 Mountain Blvd., Floor 53, Suite 6");
        address.set("country", "Canada");
        employee.set("address", address);

        // employee.add("responsibilities",
        // "Have to fix the Database problem.");
        addPhoneNumber(employee, "Work Fax", "613", "5555943");
        addPhoneNumber(employee, "Cellular", "416", "5551111");
        addPhoneNumber(employee, "Pager", "976", "5556666");
        addPhoneNumber(employee, "ISDN", "905", "5553691");

        return employee;
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample5() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "Sarah");
        employee.set("lastName", "Way");
        employee.set("gender", "Female");
        employee.set("salary", 87000);

        setPeriod(employee, new Date(2001, 6, 31), new Date(1995, 4, 1));

        DynamicEntity address = newInstance("Address");
        address.set("city", "Prince Rupert");
        address.set("postalCode", "K3K5D5");
        address.set("province", "BC");
        address.set("street", "3254 Parkway Place");
        address.set("country", "Canada");
        employee.set("address", address);

        // employee.add("responsibilities", "Write code documentation.");
        addPhoneNumber(employee, "Work", "613", "5558812");
        addPhoneNumber(employee, "ISDN", "905", "5553691");
        addPhoneNumber(employee, "Home", "613", "5551234");

        return employee;
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample6() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "Marcus");
        employee.set("lastName", "Saunders");
        employee.set("gender", "Male");
        employee.set("salary", 54300);

        setPeriod(employee, new Date(2001, 11, 31), new Date(1995, 0, 12));

        DynamicEntity address = newInstance("Address");
        address.set("city", "Perth");
        address.set("postalCode", "Y3Q2N9");
        address.set("province", "ONT");
        address.set("street", "234 Caledonia Lane");
        address.set("country", "Canada");
        employee.set("address", address);

        // employee.add("responsibilities", "Write user specifications.");
        addPhoneNumber(employee, "ISDN", "905", "5553691");
        addPhoneNumber(employee, "Work", "613", "5558812");

        return employee;
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample7() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "Nancy");
        employee.set("lastName", "White");
        employee.set("gender", "Female");
        employee.set("salary", 31000);

        setPeriod(employee, new Date(1996, 0, 1), new Date(1993, 0, 1));

        DynamicEntity address = newInstance("Address");
        address.set("city", "Metcalfe");
        address.set("postalCode", "Y4F7V6");
        address.set("province", "ONT");
        address.set("street", "2 Anderson Rd.");
        address.set("country", "Canada");
        employee.set("address", address);

        addPhoneNumber(employee, "Home", "613", "5551234");

        return employee;
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample8() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "Fred");
        employee.set("lastName", "Jones");
        employee.set("gender", "Male");
        employee.set("salary", 500000);

        setPeriod(employee, new Date(2001, 11, 31), new Date(1995, 0, 1));

        DynamicEntity address = newInstance("Address");
        address.set("city", "Victoria");
        address.set("postalCode", "Z5J2N5");
        address.set("province", "BC");
        address.set("street", "382 Hyde Park Blvd.");
        address.set("country", "Canada");
        employee.set("address", address);

        addPhoneNumber(employee, "Cellular", "416", "5551111");
        addPhoneNumber(employee, "ISDN", "905", "5553691");

        return employee;
    }

    @SuppressWarnings("deprecation")
    public DynamicEntity basicEmployeeExample9() {
        DynamicEntity employee = newInstance("Employee");

        employee.set("firstName", "Betty");
        employee.set("lastName", "Jones");
        employee.set("gender", "Female");
        employee.set("salary", 500001);

        setPeriod(employee, new Date(2001, 11, 31), new Date(1995, 0, 1));

        DynamicEntity address = newInstance("Address");
        address.set("city", "Smith Falls");
        address.set("postalCode", "C6C6C6");
        address.set("province", "ONT");
        address.set("street", "89 Chocolate Drive");
        address.set("country", "Canada");
        employee.set("address", address);

        addPhoneNumber(employee, "Work", "613", "5558812");
        addPhoneNumber(employee, "ISDN", "905", "5553691");

        return employee;
    }

    public DynamicEntity basicProjectExample1() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Enterprise");
        project.set("description", "A enterprise wide application to report on the corporations database through TopLink.");
        return project;
    }

    public DynamicEntity basicProjectExample10() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Staff Query Tool");
        project.set("description", "A tool to help staff query various things.");
        return project;
    }

    public DynamicEntity basicProjectExample2() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Sales Reporter");
        project.set("description", "A reporting application using JDK to report on the corporations database through TopLink.");
        return project;
    }

    public DynamicEntity basicProjectExample3() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "TOP-Employee Manager");
        project.set("description", "A management application to report on the corporations database through TopLink.");
        return project;
    }

    public DynamicEntity basicProjectExample4() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Problem Reporter");
        project.set("description", "A PRS application to report on the corporations database through TopLink.");
        return project;
    }

    public DynamicEntity basicProjectExample5() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Feather Reporter");
        project.set("description", "An extremely lightweight application to report on the corporations database through TopLink.");
        return project;
    }

    public DynamicEntity basicProjectExample6() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Makework");
        project.set("description", "A makework project.");
        return project;
    }

    public DynamicEntity basicProjectExample7() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Marketing Query Tool");
        project.set("description", "A tool to help marketing query various things.");
        return project;
    }

    public DynamicEntity basicProjectExample8() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Shipping Query Tool");
        project.set("description", "A tool to help shipping query various things.");
        return project;
    }

    public DynamicEntity basicProjectExample9() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Accounting Query Tool");
        project.set("description", "A tool to help accounting query various things.");
        return project;
    }

    @SuppressWarnings("unchecked")
    protected void addManagedEmployees(int managerIndex, int[] employeeIndeces) {
        DynamicEntity manager = this.employees[managerIndex];

        if (manager.<Collection> get("managedEmployees").isEmpty()) {
            for (int index = 0; index < employeeIndeces.length; index++) {
                manager.<Collection> get("managedEmployees").add(this.employees[employeeIndeces[index]]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void addProjects(int empIndex, int[] projIndeces) {
        DynamicEntity employee = this.employees[empIndex];

        for (int index = 0; index < projIndeces.length; index++) {
            employee.<Collection>get("projects").add(projects[projIndeces[index]]);
        }
    }

    /**
     * Register all of the population in the provided EntityManager to be
     * persisted This method should only be called from within a test case. It
     * asserts that the provided EntityManager is in a transaction and that the
     * database tables are empty.
     */
    public void persistAll(Session session) {
        UnitOfWork uow = session.acquireUnitOfWork();

        // Verify that the database tables are empty
        assertCount(session, "Employee", 0);
        assertCount(session, "Address", 0);
        assertCount(session, "PhoneNumber", 0);
        assertCount(session, "Project", 0);

        for (int index = 0; index < this.employees.length; index++) {
            uow.registerNewObject(this.employees[index]);
        }
        /*
        for (int index = 0; index < this.smallProjects.length; index++) {
            uow.registerNewObject(this.smallProjects[index]);
        }
        for (int index = 0; index < this.largeProjects.length; index++) {
            uow.registerNewObject(this.largeProjects[index]);
        }
        */
        uow.commit();
        verifyCounts(session);
    }

    public void verifyCounts(Session session) {
        //assertCount(session, "Employee", this.employees.length);
        //assertCount(session, "Address", this.employees.length);
        //assertCount(session, "Project", this.smallProjects.length + this.largeProjects.length);
    }

    /**
     * Verify that the provided entity type has no rows in the database using a
     * native ReportQuery.
     * 
     * @param entityClass
     * @param count
     */
    public void assertCount(Session session, String entityAlias, int count) {
        Class<?> entityClass = getDynamicClass(entityAlias);
        ReportQuery query = new ReportQuery(entityClass, new ExpressionBuilder());
        query.addCount();
        query.setShouldReturnSingleValue(true);

        int dbCount = ((Number) session.executeQuery(query)).intValue();
        assertEquals("Incorrect quantity found of " + entityClass, count, dbCount);
    }


    /**
     * Verify that the provided list of Employee instances matches the sample
     * population.
     * 
     * @param employees
     * @param dbEmps
     */
    protected void assertSame(DynamicEntity[] employees, List<DynamicEntity> dbEmps) {
        assertEquals("Incorrect quantity of employees", employees.length, dbEmps.size());
        Collections.sort(dbEmps, new DynamicEmployeeEntityComparator());

        List<DynamicEntity> sampleEmps = new ArrayList<DynamicEntity>();
        for (int index = 0; index < employees.length; index++) {
            sampleEmps.add(employees[index]);
        }
        Collections.sort(sampleEmps, new DynamicEmployeeEntityComparator());

        for (int index = 0; index < employees.length; index++) {
            DynamicEntity emp = sampleEmps.get(index);
            DynamicEntity dbEmp = dbEmps.get(index);
            assertEquals("First name does not match on employees[" + index + "]", 
                emp.<String> get("firstName"), dbEmp.<String> get("firstName"));
            assertEquals("Last name does not match on employees[" + index + "]", 
                emp.<String> get("lastName"), dbEmp.<String> get("lastName"));
            assertEquals("Salary does not match on employees[" + index + "]", 
                emp.<Integer> get("salary"), dbEmp.<Integer> get("salary"));
        }
    }

    protected void setPeriod(DynamicEntity employee, Date startDate, Date endDate) {
        DynamicEntity period = newInstance("EmploymentPeriod");
        period.set("startDate", startDate);
        period.set("endDate", endDate);
        employee.set("period", period);
    }

    @SuppressWarnings("unchecked")
    protected DynamicEntity addPhoneNumber(DynamicEntity employee, String type, String areaCode, String number) {
        DynamicEntity phone = newInstance("PhoneNumber");
        phone.set("type", type);
        phone.set("areaCode", areaCode);
        phone.set("number", number);
        phone.set("owner", employee);
        employee.<Collection> get("phoneNumbers").add(phone);
        return phone;
    }
    
    protected DynamicEntity newInstance(String entityAlias) {
        ClassDescriptor descriptor = this.session.getDescriptorForAlias(entityAlias);
        return (DynamicEntity)descriptor.getInstantiationPolicy().buildNewInstance();
    }
    
    protected Class<?> getDynamicClass(String entityAlias) {
        ClassDescriptor descriptor = this.session.getDescriptorForAlias(entityAlias);
        return descriptor.getJavaClass();
    }
}