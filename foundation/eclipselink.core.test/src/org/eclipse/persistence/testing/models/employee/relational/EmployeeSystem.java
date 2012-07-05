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
package org.eclipse.persistence.testing.models.employee.relational;

import java.util.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.testing.framework.TestSystem;

/**
 * <b>Purpose</b>: To define system behavior.
 * <p><b>Responsibilities</b>:    <ul>
 * <li> Login and return an initialize database session.
 * <li> Create and populate the database.
 * </ul>
 */
public class EmployeeSystem extends TestSystem {
    protected static boolean useFastTableCreatorAfterInitialCreate = Boolean
    .getBoolean("eclipselink.test.toggle-fast-table-creator");

    public org.eclipse.persistence.sessions.Project project;

    /**
     * Use the default EmployeeProject.
     */
    public EmployeeSystem() {
        this.project = new EmployeeProject();
    }

    public void createTables(DatabaseSession session) {
        dropTableConstraints(session);
        new EmployeeTableCreator().replaceTables(session);
    }

    /**
     * Drop table constraints
     */
    public void dropTableConstraints(Session session) {
        if (!SchemaManager.FAST_TABLE_CREATOR && !useFastTableCreatorAfterInitialCreate) {
            if (session.getLogin().getPlatform().isOracle()) {
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table PHONE CASCADE CONSTRAINTS"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table RESPONS CASCADE CONSTRAINTS"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table SALARY CASCADE CONSTRAINTS"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table PROJ_EMP CASCADE CONSTRAINTS"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table LPROJECT CASCADE CONSTRAINTS"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table PROJECT CASCADE CONSTRAINTS"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table EMPLOYEE CASCADE CONSTRAINTS"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table ADDRESS CASCADE CONSTRAINTS"));
                } catch (Exception e) {
                }
            } else {
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table PHONE"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table RESPONS"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table SALARY"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table PROJ_EMP"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table LPROJECT"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table PROJECT"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table EMPLOYEE"));
                } catch (Exception e) {
                }
                try {
                    session.executeNonSelectingCall(new SQLCall("drop table ADDRESS"));
                } catch (Exception e) {
                }
            }
        } else {
            try {
                session.executeNonSelectingCall(new SQLCall("DELETE FROM PHONE"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("DELETE FROM RESPONS"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("DELETE FROM SALARY"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("DELETE FROM PROJ_EMP"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("DELETE FROM LPROJECT"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("DELETE FROM PROJECT"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("DELETE FROM EMPLOYEE"));
            } catch (Exception e) {
            }
            try {
                session.executeNonSelectingCall(new SQLCall("DELETE FROM ADDRESS"));
            } catch (Exception e) {
            }
        }
    }

    /**
     * This method demonstrates how a descriptor can be modified after being read with it's project (INI Files).
     * The properties of the PhoneNumber's Descriptor provide this method name to be called after the descriptor is built.
     * . Add a defined query which will retrieve all phone numbers with area code 613 (local Ottawa numbers).
     */
    public static void modifyPhoneDescriptor(ClassDescriptor descriptor) {
        // Add a predefined query for retrieving numbers with 613 area code.
        ExpressionBuilder builder = new ExpressionBuilder();
        ReadAllQuery query = new ReadAllQuery(PhoneNumber.class, builder);

        Expression exp = builder.get("id").equal(builder.getParameter("ID"));
        query.setSelectionCriteria(exp.and(builder.get("areaCode").equal("613")));

        query.addArgument("ID");

        descriptor.getQueryManager().addQuery("localNumbers", query);
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new EmployeeProject();
        }

        session.addDescriptors(project);
    }
        
    /**
     * This method will instantiate all of the example instances and insert them into the database
     * using the given session.
     */
    public void populate(DatabaseSession session) {
        EmployeePopulator system = new EmployeePopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(Employee.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(SmallProject.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForClass(LargeProject.class, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
}
