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
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.dynamic.employee;

//javase imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

//java eXtension imports
import javax.persistence.EntityManager;

//JUnit4 imports
import static org.junit.Assert.assertEquals;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.testing.tests.jpa.dynamic.DynamicEmployeeEntityComparator;

public class DynamicEmployeeSystem {

    public static final String PACKAGE_PREFIX = DynamicEmployeeSystem.class.getPackage().getName();

    protected DynamicEntity[] employees = null;
    protected DynamicEntity[] projects = null;
    
    public DynamicEmployeeSystem() {
        super();
    }

    public DynamicEntity[] employees() {
       return employees;
    }
    public DynamicEntity[] projects() {
       return projects;
    }

    public void assertSame(List<DynamicEntity> dbEmps) {
        assertEquals("Incorrect quantity of employees", this.employees.length, dbEmps.size());
        Collections.sort(dbEmps, new DynamicEmployeeEntityComparator());

        List<DynamicEntity> sampleEmps = new ArrayList<DynamicEntity>();
        for (int index = 0; index < this.employees.length; index++) {
            sampleEmps.add(this.employees[index]);
        }
        Collections.sort(sampleEmps, new DynamicEmployeeEntityComparator());

        for (int index = 0; index < this.employees.length; index++) {
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
    
    public static DynamicEmployeeSystem buildProject(DynamicHelper dynamicHelper) {
        DynamicClassLoader dcl = dynamicHelper.getDynamicClassLoader();
        Class<?> employeeClass = dcl.createDynamicClass(PACKAGE_PREFIX + ".Employee");
        Class<?> addressClass = dcl.createDynamicClass(PACKAGE_PREFIX + ".Address");
        Class<?> phoneClass = dcl.createDynamicClass(PACKAGE_PREFIX + ".PhoneNumber");
        Class<?> periodClass = dcl.createDynamicClass(PACKAGE_PREFIX + ".EmploymentPeriod");
        Class<?> projectClass = dcl.createDynamicClass(PACKAGE_PREFIX + ".Project");
        JPADynamicTypeBuilder employee = new JPADynamicTypeBuilder(employeeClass, null, 
            "D_EMPLOYEE", "D_SALARY");
        JPADynamicTypeBuilder address = new JPADynamicTypeBuilder(addressClass, null, "D_ADDRESS");
        JPADynamicTypeBuilder phone = new JPADynamicTypeBuilder(phoneClass, null, "D_PHONE");
        JPADynamicTypeBuilder period = new JPADynamicTypeBuilder(periodClass, null);
        JPADynamicTypeBuilder project = new JPADynamicTypeBuilder(projectClass, null, "D_PROJECT");
        configureAddress(address);
        configureEmployee(employee, address, phone, period, project);
        configurePhone(phone, employee);
        configurePeriod(period);
        configureProject(project, employee);
        employee.addManyToManyMapping("projects", project.getType(), "D_PROJ_EMP");
        dynamicHelper.addTypes(true, true, employee.getType(), address.getType(), phone.getType(), 
            period.getType(), project.getType());
        
        return new DynamicEmployeeSystem();
    }

    private static void configurePhone(JPADynamicTypeBuilder phone, JPADynamicTypeBuilder employee) {
        phone.setPrimaryKeyFields("PHONE_TYPE", "EMP_ID");
        phone.addDirectMapping("type", String.class, "PHONE_TYPE");
        phone.addDirectMapping("ownerId", int.class, "EMP_ID").readOnly();
        phone.addDirectMapping("areaCode", String.class, "AREA_CODE");
        phone.addDirectMapping("number", String.class, "PNUMBER");
        phone.addOneToOneMapping("owner", employee.getType(), "EMP_ID");
    }

    private static void configureAddress(JPADynamicTypeBuilder address) {
        address.setPrimaryKeyFields("ADDR_ID");
        address.addDirectMapping("id", int.class, "ADDR_ID");
        address.addDirectMapping("street", String.class, "STREET");
        address.addDirectMapping("city", String.class, "CITY");
        address.addDirectMapping("province", String.class, "PROV");
        address.addDirectMapping("postalCode", String.class, "P_CODE");
        address.addDirectMapping("country", String.class, "COUNTRY");
        address.configureSequencing("ADDR_SEQ", "ADDR_ID");
    }

    private static void configureEmployee(JPADynamicTypeBuilder employee, JPADynamicTypeBuilder address, 
        JPADynamicTypeBuilder phone, JPADynamicTypeBuilder period, JPADynamicTypeBuilder project) {
        employee.setPrimaryKeyFields("EMP_ID");
        employee.addDirectMapping("id", int.class, "D_EMPLOYEE.EMP_ID");
        employee.addDirectMapping("firstName", String.class, "D_EMPLOYEE.F_NAME");
        employee.addDirectMapping("lastName", String.class, "D_EMPLOYEE.L_NAME");
        employee.addDirectMapping("gender", String.class, "D_EMPLOYEE.GENDER");
        employee.addDirectMapping("salary", int.class, "D_SALARY.SALARY");
        OneToOneMapping addressMapping = 
            employee.addOneToOneMapping("address", address.getType(), "ADDR_ID");
        addressMapping.setCascadeAll(true);
        addressMapping.setIsPrivateOwned(true);
        employee.addOneToOneMapping("manager", employee.getType(), "MANAGER_ID");
        OneToManyMapping phoneMapping = 
            employee.addOneToManyMapping("phoneNumbers", phone.getType(), "EMP_ID");
        phoneMapping.setCascadeAll(true);
        phoneMapping.setIsPrivateOwned(true);
        employee.addAggregateObjectMapping("period", period.getType(), true);
        employee.addOneToManyMapping("managedEmployees", employee.getType(), "MANAGER_ID");
        employee.configureSequencing("EMP_SEQ", "EMP_ID");
    }

    private static void configurePeriod(JPADynamicTypeBuilder period) {
        period.addDirectMapping("startDate", Date.class, "START_DATE");
        period.addDirectMapping("endDate", Date.class, "END_DATE");
    }

    private static void configureProject(JPADynamicTypeBuilder project, JPADynamicTypeBuilder employee) {
        project.setPrimaryKeyFields("PROJ_ID");
        project.addDirectMapping("id", int.class, "PROJ_ID");
        project.addDirectMapping("name", String.class, "NAME");
        project.addDirectMapping("description", String.class, "DESCRIP");
        project.configureSequencing("PROJ_SEQ", "PROJ_ID");
    }
    
    public void populate(JPADynamicHelper dynamicHelper, EntityManager em) {
        DynamicEntity[] employees = {
            basicEmployeeExample1(dynamicHelper),
            basicEmployeeExample2(dynamicHelper),
            basicEmployeeExample3(dynamicHelper),
            basicEmployeeExample4(dynamicHelper),
            basicEmployeeExample5(dynamicHelper),
            basicEmployeeExample6(dynamicHelper),
            basicEmployeeExample7(dynamicHelper),
            basicEmployeeExample8(dynamicHelper),
            basicEmployeeExample9(dynamicHelper),
            basicEmployeeExample10(dynamicHelper),
            basicEmployeeExample11(dynamicHelper),
            basicEmployeeExample12(dynamicHelper)
        };
        this.employees = employees;
        DynamicEntity[] projects = {
            basicProjectExample1(dynamicHelper),
            basicProjectExample2(dynamicHelper), 
            basicProjectExample3(dynamicHelper), 
            basicProjectExample4(dynamicHelper), 
            basicProjectExample5(dynamicHelper),  
            basicProjectExample6(dynamicHelper),
            basicProjectExample7(dynamicHelper), 
            basicProjectExample8(dynamicHelper), 
            basicProjectExample9(dynamicHelper), 
            basicProjectExample10(dynamicHelper)
        };
        this.projects = projects;
        // Setup management hierarchy
        addManagedEmployees(dynamicHelper, employees, 0, new int[]{ 2, 3, 4 });
        addManagedEmployees(dynamicHelper, employees, 1, new int[]{ 5, 0 });
        addManagedEmployees(dynamicHelper, employees, 2, new int[]{});
        addManagedEmployees(dynamicHelper, employees, 3, new int[]{});
        addManagedEmployees(dynamicHelper, employees, 4, new int[]{});
        addManagedEmployees(dynamicHelper, employees, 5, new int[]{});
        addManagedEmployees(dynamicHelper, employees, 6, new int[]{});
        addManagedEmployees(dynamicHelper, employees, 7, new int[]{});
        addManagedEmployees(dynamicHelper, employees, 8, new int[]{});
        addManagedEmployees(dynamicHelper, employees, 9, new int[]{ 7, 8, 10, 11 });
        addManagedEmployees(dynamicHelper, employees, 10, new int[]{ 6 });
        addManagedEmployees(dynamicHelper, employees, 11, new int[]{ 1 });
        // Setup Employee-Project associations
        addProjects(dynamicHelper, employees, projects, 0, new int[]{ 0, 1, 2 });
        addProjects(dynamicHelper, employees, projects, 1, new int[]{ 3, 4, 0 });
        addProjects(dynamicHelper, employees, projects, 2, new int[]{ 3 });
        addProjects(dynamicHelper, employees, projects, 4, new int[]{ 3, 1 });
        addProjects(dynamicHelper, employees, projects, 5, new int[]{});
        addProjects(dynamicHelper, employees, projects, 6, new int[]{});

        em.getTransaction().begin();
        for (int index = 0; index < this.employees.length; index++) {
            em.persist(this.employees[index]);
        }
        for (int index = 0; index < this.projects.length; index++) {
            em.persist(this.projects[index]);
        }
        em.getTransaction().commit();
        em.close();
    }
   
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample1(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "Bob");
        employee.set("lastName", "Smith");
        employee.set("gender", "Male");
        employee.set("salary", 35000);
        setPeriod(dynamicHelper, employee, new Date(1993, 0, 1), new Date(1996, 0, 1));
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Toronto");
        address.set("postalCode", "L5J2B5");
        address.set("province", "ONT");
        address.set("street", "1450 Acme Cr., Suite 4");
        address.set("country", "Canada");
        employee.set("address", address);
        addPhoneNumber(dynamicHelper, employee, "Work", "613", "5558812");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample2(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "John");
        employee.set("lastName", "Way");
        employee.set("gender", "Male");
        employee.set("salary", 53000);
        setPeriod(dynamicHelper, employee, new Date(1991, 10, 11), null);
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Ottawa");
        address.set("postalCode", "K5J2B5");
        address.set("province", "ONT");
        address.set("street", "12 Merivale Rd., Suite 5");
        address.set("country", "Canada");
        employee.set("address", address);
        addPhoneNumber(dynamicHelper, employee, "Work", "613", "5558812");
        addPhoneNumber(dynamicHelper, employee, "ISDN", "905", "5553691");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample3(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "Charles");
        employee.set("lastName", "Chanley");
        employee.set("gender", "Male");
        employee.set("salary", 43000);
        setPeriod(dynamicHelper, employee, new Date(1995, 0, 1), new Date(2001, 11, 31));
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Montreal");
        address.set("postalCode", "Q2S5Z5");
        address.set("province", "QUE");
        address.set("street", "1 Canadien Place");
        address.set("country", "Canada");
        employee.set("address", address);
        addPhoneNumber(dynamicHelper, employee, "Pager", "976", "5556666");
        addPhoneNumber(dynamicHelper, employee, "ISDN", "905", "5553691");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample4(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "Emanual");
        employee.set("lastName", "Smith");
        employee.set("gender", "Male");
        employee.set("salary", 49631);
        setPeriod(dynamicHelper, employee, new Date(2001, 11, 31), new Date(1995, 0, 1));
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Vancouver");
        address.set("postalCode", "N5J2N5");
        address.set("province", "BC");
        address.set("street", "20 Mountain Blvd., Floor 53, Suite 6");
        address.set("country", "Canada");
        employee.set("address", address);
        addPhoneNumber(dynamicHelper, employee, "Work Fax", "613", "5555943");
        addPhoneNumber(dynamicHelper, employee, "Cellular", "416", "5551111");
        addPhoneNumber(dynamicHelper, employee, "Pager", "976", "5556666");
        addPhoneNumber(dynamicHelper, employee, "ISDN", "905", "5553691");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample5(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "Sarah");
        employee.set("lastName", "Way");
        employee.set("gender", "Female");
        employee.set("salary", 87000);
        setPeriod(dynamicHelper, employee, new Date(2001, 6, 31), new Date(1995, 4, 1));
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Prince Rupert");
        address.set("postalCode", "K3K5D5");
        address.set("province", "BC");
        address.set("street", "3254 Parkway Place");
        address.set("country", "Canada");
        employee.set("address", address);
        addPhoneNumber(dynamicHelper, employee, "Work", "613", "5558812");
        addPhoneNumber(dynamicHelper, employee, "ISDN", "905", "5553691");
        addPhoneNumber(dynamicHelper, employee, "Home", "613", "5551234");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample6(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "Marcus");
        employee.set("lastName", "Saunders");
        employee.set("gender", "Male");
        employee.set("salary", 54300);
        setPeriod(dynamicHelper, employee, new Date(2001, 11, 31), new Date(1995, 0, 12));
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Perth");
        address.set("postalCode", "Y3Q2N9");
        address.set("province", "ONT");
        address.set("street", "234 Caledonia Lane");
        address.set("country", "Canada");
        employee.set("address", address);
        addPhoneNumber(dynamicHelper, employee, "ISDN", "905", "5553691");
        addPhoneNumber(dynamicHelper, employee, "Work", "613", "5558812");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample7(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "Nancy");
        employee.set("lastName", "White");
        employee.set("gender", "Female");
        employee.set("salary", 31000);
        setPeriod(dynamicHelper, employee, new Date(1996, 0, 1), new Date(1993, 0, 1));
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Metcalfe");
        address.set("postalCode", "Y4F7V6");
        address.set("province", "ONT");
        address.set("street", "2 Anderson Rd.");
        address.set("country", "Canada");
        employee.set("address", address);
        addPhoneNumber(dynamicHelper, employee, "Home", "613", "5551234");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample8(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "Fred");
        employee.set("lastName", "Jones");
        employee.set("gender", "Male");
        employee.set("salary", 500000);
        setPeriod(dynamicHelper, employee, new Date(2001, 11, 31), new Date(1995, 0, 1));
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Victoria");
        address.set("postalCode", "Z5J2N5");
        address.set("province", "BC");
        address.set("street", "382 Hyde Park Blvd.");
        address.set("country", "Canada");
        employee.set("address", address);
        addPhoneNumber(dynamicHelper, employee, "Cellular", "416", "5551111");
        addPhoneNumber(dynamicHelper, employee, "ISDN", "905", "5553691");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample9(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "Betty");
        employee.set("lastName", "Jones");
        employee.set("gender", "Female");
        employee.set("salary", 500001);
        setPeriod(dynamicHelper, employee, new Date(2001, 11, 31), new Date(1995, 0, 1));
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Smith Falls");
        address.set("postalCode", "C6C6C6");
        address.set("province", "ONT");
        address.set("street", "89 Chocolate Drive");
        address.set("country", "Canada");
        employee.set("address", address);
        addPhoneNumber(dynamicHelper, employee, "Work", "613", "5558812");
        addPhoneNumber(dynamicHelper, employee, "ISDN", "905", "5553691");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample10(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "Jill");
        employee.set("lastName", "May");
        employee.set("gender", "Female");
        setPeriod(dynamicHelper, employee, new Date(1991, 10, 111), null);
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Calgary");
        address.set("postalCode", "J5J2B5");
        address.set("province", "AB");
        address.set("street", "1111 Mooseland Rd.");
        address.set("country", "Canada");
        employee.set("address", address);
        employee.set("salary", 56232);
        addPhoneNumber(dynamicHelper, employee, "Work", "613", "5558812");
        addPhoneNumber(dynamicHelper, employee, "Work Fax", "613", "5555943");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample11(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "Sarah-Lou");
        employee.set("lastName", "Smitty");
        employee.set("gender", "Female");
        setPeriod(dynamicHelper, employee, new Date(1993, 0, 1), new Date(1996, 0, 1));
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Arnprior");
        address.set("postalCode", "W1A2B5");
        address.set("province", "ONT");
        address.set("street", "1 Hawthorne Drive");
        address.set("country", "Canada");
        employee.set("address", address);
        employee.set("salary", 75000);
        addPhoneNumber(dynamicHelper, employee, "Work Fax", "613", "5555943");
        addPhoneNumber(dynamicHelper, employee, "Home", "613", "5551234");
        addPhoneNumber(dynamicHelper, employee, "Cellular", "416", "5551111");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected DynamicEntity basicEmployeeExample12(JPADynamicHelper dynamicHelper) {
        DynamicEntity employee = newInstance(dynamicHelper, "Employee");
        employee.set("firstName", "Jim-Bob");
        employee.set("lastName", "Jefferson");
        employee.set("gender", "Male");
        setPeriod(dynamicHelper, employee, new Date(1995, 0, 12), new Date(2001, 11, 31));
        DynamicEntity address = newInstance(dynamicHelper, "Address");
        address.set("city", "Yellowknife");
        address.set("postalCode", "Y5J2N5");
        address.set("province", "YK");
        address.set("street", "1112 Gold Rush Rd.");
        address.set("country", "Canada");
        employee.set("address", address);
        employee.set("salary", 50000);
        addPhoneNumber(dynamicHelper, employee, "Home", "613", "5551234");
        addPhoneNumber(dynamicHelper, employee, "Cellular", "416", "5551111");
        return employee;
    }
    protected DynamicEntity basicProjectExample1(JPADynamicHelper dynamicHelper) {
        DynamicEntity project = newInstance(dynamicHelper, "Project");
        project.set("name", "Enterprise");
        project.set("description", "A enterprise wide application to report on the corporations database through TopLink.");
        return project;
    }
    protected DynamicEntity basicProjectExample2(JPADynamicHelper dynamicHelper) {
        DynamicEntity project = newInstance(dynamicHelper, "Project");
        project.set("name", "Sales Reporter");
        project.set("description", "A reporting application using JDK to report on the corporations database through TopLink.");
        return project;
    }
    protected DynamicEntity basicProjectExample3(JPADynamicHelper dynamicHelper) {
        DynamicEntity project = newInstance(dynamicHelper, "Project");
        project.set("name", "TOP-Employee Manager");
        project.set("description", "A management application to report on the corporations database through TopLink.");
        return project;
    }
    protected DynamicEntity basicProjectExample4(JPADynamicHelper dynamicHelper) {
        DynamicEntity project = newInstance(dynamicHelper, "Project");
        project.set("name", "Problem Reporter");
        project.set("description", "A PRS application to report on the corporations database through TopLink.");
        return project;
    }
    protected DynamicEntity basicProjectExample5(JPADynamicHelper dynamicHelper) {
        DynamicEntity project = newInstance(dynamicHelper, "Project");
        project.set("name", "Feather Reporter");
        project.set("description", "An extremely lightweight application to report on the corporations database through TopLink.");
        return project;
    }
    protected DynamicEntity basicProjectExample6(JPADynamicHelper dynamicHelper) {
        DynamicEntity project = newInstance(dynamicHelper, "Project");
        project.set("name", "Makework");
        project.set("description", "A makework project.");
        return project;
    }
    protected DynamicEntity basicProjectExample7(JPADynamicHelper dynamicHelper) {
        DynamicEntity project = newInstance(dynamicHelper, "Project");
        project.set("name", "Marketing Query Tool");
        project.set("description", "A tool to help marketing query various things.");
        return project;
    }
    protected DynamicEntity basicProjectExample8(JPADynamicHelper dynamicHelper) {
        DynamicEntity project = newInstance(dynamicHelper, "Project");
        project.set("name", "Shipping Query Tool");
        project.set("description", "A tool to help shipping query various things.");
        return project;
    }
    protected DynamicEntity basicProjectExample9(JPADynamicHelper dynamicHelper) {
        DynamicEntity project = newInstance(dynamicHelper, "Project");
        project.set("name", "Accounting Query Tool");
        project.set("description", "A tool to help accounting query various things.");
        return project;
    }
    protected DynamicEntity basicProjectExample10(JPADynamicHelper dynamicHelper) {
        DynamicEntity project = newInstance(dynamicHelper, "Project");
        project.set("name", "Staff Query Tool");
        project.set("description", "A tool to help staff query various things.");
        return project;
    }

    @SuppressWarnings("unchecked")
    protected DynamicEntity addPhoneNumber(JPADynamicHelper dynamicHelper, DynamicEntity employee, String type, 
        String areaCode, String number) {
        DynamicEntity phone = newInstance(dynamicHelper, "PhoneNumber");
        phone.set("type", type);
        phone.set("areaCode", areaCode);
        phone.set("number", number);
        phone.set("owner", employee);
        employee.<Collection>get("phoneNumbers").add(phone);
        return phone;
    }

    protected void setPeriod(JPADynamicHelper dynamicHelper, DynamicEntity employee, Date startDate, Date endDate) {
        DynamicEntity period = newInstance(dynamicHelper, "EmploymentPeriod");
        period.set("startDate", startDate);
        period.set("endDate", endDate);
        employee.set("period", period);
    }

    @SuppressWarnings("unchecked")
    protected void addManagedEmployees(JPADynamicHelper dynamicHelper, DynamicEntity[] employees, 
        int managerIndex, int[] employeeIndeces) {
        DynamicEntity manager = employees[managerIndex];
        if (manager.<Collection> get("managedEmployees").isEmpty()) {
            for (int index = 0; index < employeeIndeces.length; index++) {
                manager.<Collection> get("managedEmployees").add(employees[employeeIndeces[index]]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void addProjects(JPADynamicHelper dynamicHelper, DynamicEntity[] employees, 
        DynamicEntity[] projects, int empIndex, int[] projIndeces) {
        DynamicEntity employee = employees[empIndex];
        for (int index = 0; index < projIndeces.length; index++) {
            employee.<Collection> get("projects").add(projects[projIndeces[index]]);
        }
    }

    protected DynamicEntity newInstance(JPADynamicHelper dynamicHelper, String entityAlias) {
        return dynamicHelper.newDynamicEntity(entityAlias);
    }
}
