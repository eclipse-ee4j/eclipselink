package dynamic.testing.employee;

//javase imports
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

//domain-specific (testing) imports
import static dynamic.testing.DynamicTestingHelper.createLogin;
import static dynamic.testing.DynamicTestingHelper.logLevel;

public class EmployeeQueriesTestSuite {

    static final String PROJECT_XML = "dynamic/testing/Employee_utf8.xml";
    
    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;
    static DynamicEntity[] employees = null;
    static DynamicEntity[] addresses = null;
    static DynamicEntity[] projects = null;

    @BeforeClass
    public static void setUp() {
        DynamicClassLoader dcl = new DynamicClassLoader(MappingConfigTestSuite.class.getClassLoader());
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
        populate();
    }

    protected static void populate() {
        DynamicEntity[] employees = {
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
        DynamicEntity[] projects = {
            basicProjectExample1(),
            basicProjectExample2(), 
            basicProjectExample3(), 
            basicProjectExample4(), 
            basicProjectExample5(),  
            basicProjectExample6(),
            basicProjectExample7(), 
            basicProjectExample8(), 
            basicProjectExample9(), 
            basicProjectExample10()
        };
        // Setup management hierarchy
        addManagedEmployees(0, new int[] { 2, 3, 4 }, employees);
        addManagedEmployees(1, new int[] { 5, 0 }, employees);
        addManagedEmployees(2, new int[] {}, employees);
        addManagedEmployees(3, new int[] {}, employees);
        addManagedEmployees(4, new int[] {}, employees);
        addManagedEmployees(5, new int[] {}, employees);
        addManagedEmployees(6, new int[] {}, employees);
        addManagedEmployees(7, new int[] {}, employees);
        addManagedEmployees(8, new int[] {}, employees);
        addManagedEmployees(9, new int[] { 7, 8, 10, 11 }, employees);
        addManagedEmployees(10, new int[] { 6 }, employees);
        addManagedEmployees(11, new int[] { 1 }, employees);
        // Setup Employee-Project associations
        addProjects(0, new int[] { 0, 1, 2 }, employees, projects);
        addProjects(1, new int[] { 3, 4, 0 }, employees, projects);
        addProjects(2, new int[] { 3 }, employees, projects);
        addProjects(4, new int[] { 3, 1 }, employees, projects);
        addProjects(5, new int[] {}, employees, projects);
        addProjects(6, new int[] {}, employees, projects);

        UnitOfWork uow = session.acquireUnitOfWork();
        for (int index = 0; index < employees.length; index++) {
            uow.registerNewObject(employees[index]);
        }
        for (int index = 0; index < projects.length; index++) {
            uow.registerNewObject(projects[index]);
        }
        uow.commit();
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
    public void foo() {
        
    }
    
    @SuppressWarnings("deprecation")
    protected static DynamicEntity basicEmployeeExample1() {
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
        addPhoneNumber(employee, "Work", "613", "5558812");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected static DynamicEntity basicEmployeeExample10() {
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
    protected static DynamicEntity basicEmployeeExample11() {
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
    protected static DynamicEntity basicEmployeeExample12() {
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
    protected static DynamicEntity basicEmployeeExample2() {
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
        addPhoneNumber(employee, "Work", "613", "5558812");
        addPhoneNumber(employee, "ISDN", "905", "5553691");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected static DynamicEntity basicEmployeeExample3() {
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
        addPhoneNumber(employee, "Pager", "976", "5556666");
        addPhoneNumber(employee, "ISDN", "905", "5553691");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected static DynamicEntity basicEmployeeExample4() {
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
        addPhoneNumber(employee, "Work Fax", "613", "5555943");
        addPhoneNumber(employee, "Cellular", "416", "5551111");
        addPhoneNumber(employee, "Pager", "976", "5556666");
        addPhoneNumber(employee, "ISDN", "905", "5553691");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected static DynamicEntity basicEmployeeExample5() {
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
        addPhoneNumber(employee, "Work", "613", "5558812");
        addPhoneNumber(employee, "ISDN", "905", "5553691");
        addPhoneNumber(employee, "Home", "613", "5551234");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected static DynamicEntity basicEmployeeExample6() {
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
        addPhoneNumber(employee, "ISDN", "905", "5553691");
        addPhoneNumber(employee, "Work", "613", "5558812");
        return employee;
    }
    @SuppressWarnings("deprecation")
    protected static DynamicEntity basicEmployeeExample7() {
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
    protected static DynamicEntity basicEmployeeExample8() {
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
    protected static DynamicEntity basicEmployeeExample9() {
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
    protected static DynamicEntity basicProjectExample1() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Enterprise");
        project.set("description", "A enterprise wide application to report on the corporations database through TopLink.");
        return project;
    }
    protected static DynamicEntity basicProjectExample2() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Sales Reporter");
        project.set("description", "A reporting application using JDK to report on the corporations database through TopLink.");
        return project;
    }
    protected static DynamicEntity basicProjectExample3() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "TOP-Employee Manager");
        project.set("description", "A management application to report on the corporations database through TopLink.");
        return project;
    }
    protected static DynamicEntity basicProjectExample4() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Problem Reporter");
        project.set("description", "A PRS application to report on the corporations database through TopLink.");
        return project;
    }
    protected static DynamicEntity basicProjectExample5() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Feather Reporter");
        project.set("description", "An extremely lightweight application to report on the corporations database through TopLink.");
        return project;
    }
    protected static DynamicEntity basicProjectExample6() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Makework");
        project.set("description", "A makework project.");
        return project;
    }
    protected static DynamicEntity basicProjectExample7() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Marketing Query Tool");
        project.set("description", "A tool to help marketing query various things.");
        return project;
    }
    protected static DynamicEntity basicProjectExample8() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Shipping Query Tool");
        project.set("description", "A tool to help shipping query various things.");
        return project;
    }
    protected static DynamicEntity basicProjectExample9() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Accounting Query Tool");
        project.set("description", "A tool to help accounting query various things.");
        return project;
    }
    protected static DynamicEntity basicProjectExample10() {
        DynamicEntity project = newInstance("Project");
        project.set("name", "Staff Query Tool");
        project.set("description", "A tool to help staff query various things.");
        return project;
    }

    @SuppressWarnings("unchecked")
    protected static DynamicEntity addPhoneNumber(DynamicEntity employee, String type, String areaCode, String number) {
        DynamicEntity phone = newInstance("PhoneNumber");
        phone.set("type", type);
        phone.set("areaCode", areaCode);
        phone.set("number", number);
        phone.set("owner", employee);
        employee.<Collection>get("phoneNumbers").add(phone);
        return phone;
    }

    protected static void setPeriod(DynamicEntity employee, Date startDate, Date endDate) {
        DynamicEntity period = newInstance("EmploymentPeriod");
        period.set("startDate", startDate);
        period.set("endDate", endDate);
        employee.set("period", period);
    }

    @SuppressWarnings("unchecked")
    protected static void addManagedEmployees(int managerIndex, int[] employeeIndeces, DynamicEntity[] employees) {
        DynamicEntity manager = employees[managerIndex];
        if (manager.<Collection> get("managedEmployees").isEmpty()) {
            for (int index = 0; index < employeeIndeces.length; index++) {
                manager.<Collection> get("managedEmployees").add(employees[employeeIndeces[index]]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected static void addProjects(int empIndex, int[] projIndeces, DynamicEntity[] employees, DynamicEntity[] projects) {
        DynamicEntity employee = employees[empIndex];
        for (int index = 0; index < projIndeces.length; index++) {
            employee.<Collection> get("projects").add(projects[projIndeces[index]]);
        }
    }

    static DynamicEntity newInstance(String entityAlias) {
        ClassDescriptor descriptor = session.getDescriptorForAlias(entityAlias);
        return (DynamicEntity)descriptor.getInstantiationPolicy().buildNewInstance();
    }
}