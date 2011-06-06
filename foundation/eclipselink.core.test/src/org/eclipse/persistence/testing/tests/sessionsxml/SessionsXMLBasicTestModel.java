/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.platform.database.TimesTenPlatform;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;
import org.eclipse.persistence.sessions.factories.SessionManager;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;

import org.eclipse.persistence.testing.framework.InsertObjectTest;
import org.eclipse.persistence.testing.framework.ReadAllTest;
import org.eclipse.persistence.testing.framework.ReadObjectTest;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.framework.WriteObjectTest;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmployeePopulator;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.employee.EmployeeDeleteTest;
import org.eclipse.persistence.testing.tests.employee.ProjectDeleteTest;

/**
 * Runs the Employee tests using a session loaded from session.xml and project.xml.
 */
public class SessionsXMLBasicTestModel extends TestModel {

    Session originalSession = null;
    Session newXMLSession = null;

    public SessionsXMLBasicTestModel() {
        setDescription("This model tests reading/writing/deleting using the employee demo read through the sessions.xml and project.xml.");
    }

    public SessionsXMLBasicTestModel(boolean isSRG) {
        this();
        this.isSRG = isSRG;
    }

    public void addTests() {
        addTest(getReadObjectTestSuite());
        addTest(getUpdateObjectTestSuite());
        addTest(getInsertObjectTestSuite());
        addTest(getDeleteObjectTestSuite());
        addTest(getReadAllTestSuite());
        addTest(getValidationTests());
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public void addSRGTests() {
        addTest(getSRGReadObjectTestSuite());
        addTest(getSRGUpdateObjectTestSuite());
        addTest(getSRGInsertObjectTestSuite());
        addTest(getSRGDeleteObjectTestSuite());
        addTest(getSRGReadAllTestSuite());
        addTest(getSRGValidationTests());
    }

    public static TestSuite getDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0003")));

        return suite;

    }

    public static TestSuite getInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the employee demo.");
        EmployeePopulator system = new EmployeePopulator();

        suite.addTest(new InsertObjectTest(system.basicEmployeeExample1()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample2()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample3()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample4()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample5()));

        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample1()));
        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample2()));
        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample3()));

        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample1()));
        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample2()));
        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample3()));

        return suite;
    }

    public static TestSuite getReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the employee demo.");

        suite.addTest(new ReadAllTest(Employee.class, 12));
        suite.addTest(new ReadAllTest(Project.class, 15));
        suite.addTest(new ReadAllTest(LargeProject.class, 5));
        suite.addTest(new ReadAllTest(SmallProject.class, 10));

        return suite;
    }

    public static TestSuite getReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0005")));


        Project project = (Project)manager.getObject(largeProjectClass, "0001");
        ReadObjectTest test = new ReadObjectTest(project);
        test.setQuery(new org.eclipse.persistence.queries.ReadObjectQuery(Project.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("id").equal(project.getId())));
        suite.addTest(test);

        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new WriteObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new WriteObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new WriteObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new WriteObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }

    public static TestSuite getValidationTests() {
        TestSuite suite = new TestSuite();
        suite.setName("Sessions.xml validation tests");

        SessionsXMLValidationTest.addTestsTo(suite);

        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.

    public static TestSuite getSRGDeleteObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeDeleteObjectTestSuite");
        suite.setDescription("This suite tests the deletion of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new EmployeeDeleteTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new ProjectDeleteTest(manager.getObject(largeProjectClass, "0003")));

        return suite;

    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.

    public static TestSuite getSRGInsertObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeInsertObjectTestSuite");
        suite.setDescription("This suite tests the insertion of each object in the employee demo.");
        EmployeePopulator system = new EmployeePopulator();

        suite.addTest(new InsertObjectTest(system.basicEmployeeExample1()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample2()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample3()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample4()));
        suite.addTest(new InsertObjectTest(system.basicEmployeeExample5()));

        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample1()));
        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample2()));
        suite.addTest(new InsertObjectTest(system.basicSmallProjectExample3()));

        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample1()));
        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample2()));
        suite.addTest(new InsertObjectTest(system.basicLargeProjectExample3()));

        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGReadAllTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadAllTestSuite");
        suite.setDescription("This suite tests the reading of all the objects of each class in the employee demo.");

        suite.addTest(new ReadAllTest(Employee.class, 12));
        suite.addTest(new ReadAllTest(Project.class, 15));
        suite.addTest(new ReadAllTest(LargeProject.class, 5));
        suite.addTest(new ReadAllTest(SmallProject.class, 10));

        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.
    public static TestSuite getSRGReadObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeReadObjectTestSuite");
        suite.setDescription("This suite test the reading of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new ReadObjectTest(manager.getObject(employeeClass, "0005")));


        Project project = (Project)manager.getObject(largeProjectClass, "0001");
        ReadObjectTest test = new ReadObjectTest(project);
        test.setQuery(new org.eclipse.persistence.queries.ReadObjectQuery(Project.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("id").equal(project.getId())));
        suite.addTest(test);

        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new ReadObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.

    public static TestSuite getSRGUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EmployeeUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the employee demo.");

        Class employeeClass = Employee.class;
        Class largeProjectClass = LargeProject.class;
        Class smallProjectClass = SmallProject.class;
        PopulationManager manager = PopulationManager.getDefaultManager();

        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0002")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0003")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0004")));
        suite.addTest(new WriteObjectTest(manager.getObject(employeeClass, "0005")));

        suite.addTest(new WriteObjectTest(manager.getObject(smallProjectClass, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(smallProjectClass, "0002")));
        suite.addTest(new WriteObjectTest(manager.getObject(smallProjectClass, "0003")));

        suite.addTest(new WriteObjectTest(manager.getObject(largeProjectClass, "0001")));
        suite.addTest(new WriteObjectTest(manager.getObject(largeProjectClass, "0002")));
        suite.addTest(new WriteObjectTest(manager.getObject(largeProjectClass, "0003")));

        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.

    public static TestSuite getSRGValidationTests() {
        TestSuite suite = new TestSuite();
        suite.setName("Sessions.xml validation tests");

        SessionsXMLValidationTest.addTestsTo(suite);

        return suite;
    }

    public void setup() {
        originalSession = getSession();
        if ((getSession().getPlatform() instanceof TimesTenPlatform)) {
            throw new TestProblemException("This model is not intended for TimesTen databases.");
        }

        XMLSessionConfigLoader xmlLoader = new XMLSessionConfigLoader("org/eclipse/persistence/testing/models/sessionsxml/sessions.xml");
        // Do not login here, login later on.
        newXMLSession = SessionManager.getManager().getSession(xmlLoader, "EmployeeSession", getClass().getClassLoader(), false, true);
        newXMLSession.setSessionLog(originalSession.getSessionLog());
        newXMLSession.setLogLevel(originalSession.getLogLevel());

        ((DatabaseSession)originalSession).logout();
        newXMLSession.getLogin().setPlatform(originalSession.getLogin().getPlatform());
        newXMLSession.getLogin().setDriverClassName(originalSession.getLogin().getDriverClassName());
        newXMLSession.getLogin().setConnectionString(originalSession.getLogin().getConnectionString());
        newXMLSession.getLogin().setUserName(originalSession.getLogin().getUserName());
        newXMLSession.getLogin().setEncryptedPassword(originalSession.getLogin().getPassword());

        ((DatabaseSession)newXMLSession).login();
        getExecutor().setSession(newXMLSession);

        new EmployeeSystem().createTables((DatabaseSession)newXMLSession);
        new EmployeeSystem().populate((DatabaseSession)newXMLSession);

    }

    public void reset() {
        getExecutor().setSession(originalSession);
        if(newXMLSession != null) {
            if(newXMLSession.isConnected()) {
                ((DatabaseSession)newXMLSession).logout();
            }
            newXMLSession = null;
        }
    }
}
