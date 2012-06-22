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

import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestSystem;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;


/**
 * <b>Purpose</b>: To define system behavior.
 * <p><b>Responsibilities</b>:    <ul>
 * <li> Login and return an initialize database session.
 * <li> Create and populate the database.
 * </ul>
 */
public class NLSEmployeeSystem extends TestSystem {

    /**
     * Use the default EmployeeProject.
     */
    public NLSEmployeeSystem() {
        project = new EmployeeProject();
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new EmployeeProject();
        }
        session.addDescriptors(project);
    }

    public void createTables(Session session) {
        try {
            session.executeNonSelectingCall(new SQLCall("drop table \u305f\u304f\u305d\u305b\u304a"));
            session.executeNonSelectingCall(new SQLCall("drop table \u3064\u304a\u3066\u305f\u305d\u305b\u3066"));
            session.executeNonSelectingCall(new SQLCall("drop table \u3066\u3042\u3057\u3042\u3064\u306e"));
            session.executeNonSelectingCall(new SQLCall("drop table \u305f_\u304a\u3059\u305f"));
            session.executeNonSelectingCall(new SQLCall("drop table \u3057\u305f\u3064\u305d\u3053"));
            session.executeNonSelectingCall(new SQLCall("drop table \u305f\u3064\u305d\u3053"));
            session.executeNonSelectingCall(new SQLCall("drop table \u304a\u3059\u305f"));
            session.executeNonSelectingCall(new SQLCall("drop table \u3042\u3048\u3048\u3064\u304a\u3066\u3066"));
        } catch (Exception e) {
        }
        new NLSEmployeeTableCreator().replaceTables((DatabaseSession)session);
    }

    /**
    * Return a connected session using the default login.
    */
    public DatabaseSession login() {
        DatabaseSession session;

        session = project.createDatabaseSession();
        session.login();

        return session;
    }

    /**
     *    This method demonstrates how a descriptor can be modified after being read with it's project (INI Files).
     *    The properties of the PhoneNumber's Descriptor provide this method name to be called after the descriptor is built.
     *    1. Add a query key 'id' so that it may be used within expressions.
     *    2. Add a defined query whic will retrieve all phone numbers with area code 613 (local Ottawa numbers).
     */
    public static void modifyPhoneDescriptor(ClassDescriptor descriptor) {
        // 1. Add query key 'id'
        //descriptor.addDirectQueryKey("id", "EMP_ID");
        descriptor.addDirectQueryKey("id", "\u304a\u3059\u305f_\u3051\u3048");

        // Add a predefined query for retrieving numbers with 613 area code.
        ReadAllQuery query = new ReadAllQuery();
        ExpressionBuilder builder = new ExpressionBuilder();

        //Expression exp = builder.get("id").equal(builder.getParameter("ID"));
        Expression exp = builder.get("id").equal(builder.getParameter("\u3051\u3048"));

        query.setReferenceClass(PhoneNumber.class);
        query.setSelectionCriteria(exp.and(builder.get("areaCode").equal("613")));
        //query.addArgument("ID");
        query.addArgument("\u3051\u3048");

        descriptor.getQueryManager().removeQuery("localNumbers");
        descriptor.getQueryManager().addQuery("localNumbers", query);
    }

    /**
     * This method will instantiate all of the example instances and insert them into the database
     * using the given session.
     */
    public void populate(DatabaseSession session) {
        NLSEmployeePopulator system = new NLSEmployeePopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(NLSEmployee.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForAbstractClass(Project.class, session, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
}
