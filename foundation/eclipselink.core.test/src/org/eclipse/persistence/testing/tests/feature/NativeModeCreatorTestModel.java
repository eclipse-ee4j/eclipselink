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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.tests.expressions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.aggregate.AggregateSystem;
import org.eclipse.persistence.testing.models.collections.CollectionsSystem;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.models.legacy.LegacySystem;
import org.eclipse.persistence.testing.models.mapping.MappingSystem;
import org.eclipse.persistence.testing.models.multipletable.ProjectSystem;
import org.eclipse.persistence.testing.models.ownership.OwnershipSystem;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;
import org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel;

/**
 * This suite runs the employee basic tests using a table qualifier, native sql and native sequencing.
 */
public class NativeModeCreatorTestModel extends TestModel {
    protected Boolean usesNativeSQL;
    protected Sequence defaultSequence;
    protected Boolean shouldBindAllParameters;
    protected String qualifier;

    /**
     * This sets the table qualifier and native mode.
     */
    public void addForcedRequiredSystems() {
        DatabasePlatform platform = getSession().getPlatform();
        usesNativeSQL = Boolean.valueOf(platform.usesNativeSQL());
        defaultSequence = getSession().getLogin().getDefaultSequence();
        shouldBindAllParameters = Boolean.valueOf(platform.shouldBindAllParameters());

        if (platform.isSybase() || platform.isSQLAnywhere() || platform.isOracle() || platform.isSQLServer() || platform.isInformix() || 
            platform.isMySQL() || platform.isDB2() || platform.isTimesTen()) {
            
            platform.setUsesNativeSQL(true);
            getSession().getLogin().useNativeSequencing();
            getDatabaseSession().getSequencingControl().resetSequencing();
        }
        // We need to disable binding for testing native SQL.
        platform.setShouldBindAllParameters(false);

        getExecutor().removeConfigureSystem(new EmployeeSystem());
        // Force the database to be recreated to ensure the sequences are defined.
        addForcedRequiredSystem(new EmployeeNativeModeSystem());
        
        getExecutor().removeConfigureSystem(new InheritanceSystem());
        addForcedRequiredSystem(new InheritanceSystem());
    }

    public void addRequiredSystems() {
        addRequiredSystem(new OwnershipSystem());
        addRequiredSystem(new LegacySystem());
        addRequiredSystem(new ProjectSystem());
        addRequiredSystem(new CollectionsSystem());
        addRequiredSystem(new MappingSystem());
        addRequiredSystem(new AggregateSystem());
    }

    public void addTests() {
        addTest(EmployeeBasicTestModel.getReadObjectTestSuite());
        addTest(EmployeeBasicTestModel.getUpdateObjectTestSuite());
        addTest(EmployeeBasicTestModel.getInsertObjectTestSuite());
        addTest(EmployeeBasicTestModel.getDeleteObjectTestSuite());
        addTest(EmployeeBasicTestModel.getReadAllTestSuite());
        addTest(new ExpressionTestSuite());
        
        addTest(InheritanceTestModel.getDeleteObjectTestSuite());

        TestSuite seqSuite = new TestSuite();
        seqSuite.setName("EmployeeNativeSeqTestSuite");
        seqSuite.setDescription("This suite tests native sequencing in the employee demo.");
        seqSuite.addTest(new OracleNativeSeqInitTest(false, 0));
        seqSuite.addTest(new OracleNativeSeqInitTest(false, 1));
        seqSuite.addTest(new OracleNativeSeqInitTest(false, 2));
        seqSuite.addTest(new OracleNativeSeqInitTest(false, 3));
        addTest(seqSuite);
    }

    /**
     * This sets the table qualifier and native mode.
     */
    public void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        // Set table qualifier for Oracle, assume user name is schema.
        if (getSession().getLogin().getPlatform().isOracle()) {
            String oldUser = null;
            try {
                oldUser = getAbstractSession().getAccessor().getConnection().getMetaData().getUserName();
            } catch (Exception exception) {
                throw new TestErrorException("Meta-data error.", exception);
            }
            getExecutor().swapCleanDatabaseSession();
            getSession().getLogin().setUserName("scott");
            getSession().getLogin().setPassword("tiger");
            qualifier = getSession().getLogin().getTableQualifier();
            getSession().getLogin().setTableQualifier(oldUser);
            getDatabaseSession().logout();

            DatabaseSession newDBSession = getDatabaseSession();
            new EmployeeNativeModeSystem().addDescriptors(newDBSession);
            new InheritanceSystem().addDescriptors(newDBSession);
            new OwnershipSystem().addDescriptors(newDBSession);
            new LegacySystem().addDescriptors(newDBSession);
            new ProjectSystem().addDescriptors(newDBSession);
            new CollectionsSystem().addDescriptors(newDBSession);
            new MappingSystem().addDescriptors(newDBSession);
            new AggregateSystem().addDescriptors(newDBSession);
            
            getDatabaseSession().login();
            
            new EmployeeNativeModeSystem().dropTableConstraints(getSession());
            new InheritanceSystem().dropTableConstraints(getSession());
        }
    }

    /**
     * This unsets the table qualifier and native mode.
     */
    public void reset() {
        getExecutor().resetSession();

        DatabasePlatform platform = getSession().getPlatform();

        if (platform.isSybase() || platform.isSQLAnywhere() || platform.isOracle() || platform.isSQLServer() || platform.isInformix() || 
            platform.isMySQL() || platform.isDB2() || platform.isTimesTen()) {
            
            if (usesNativeSQL != null) {
                platform.setUsesNativeSQL(usesNativeSQL.booleanValue());
            }
            if (defaultSequence != null) {
                getSession().getLogin().setDefaultSequence(defaultSequence);
                getDatabaseSession().getSequencingControl().resetSequencing();
            }
        }
        if (shouldBindAllParameters != null) {
            platform.setShouldBindAllParameters(shouldBindAllParameters.booleanValue());
        }
        if (qualifier != null) {
            getSession().getLogin().setTableQualifier(qualifier);
        }
        
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        getExecutor().removeConfigureSystem(new EmployeeSystem());
        getExecutor().removeConfigureSystem(new InheritanceSystem());

        getDatabaseSession().logout();
        getDatabaseSession().login();
    }
}
