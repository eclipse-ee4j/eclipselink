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
package org.eclipse.persistence.testing.tests.relationshipmaintenance;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.relationshipmaintenance.RelationshipsSystem;

/**
 * This model tests reading/writing/deleting through using the complex mapping model.
 */
public class RelationshipsTestModel extends TestModel {
    public RelationshipsTestModel() {
        setDescription("This model tests reading/writing/deleting of the complex legacy model. ");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new RelationshipsSystem());
    }

    public void addTests() {
        addTest(getUpdateObjectTestSuite());
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EJB Relationship Support Testsuite");
        suite.setDescription("");

        suite.addTest(new RemoveObjectTest());
        suite.addTest(new AddReferencedObjectTest());
        suite.addTest(new AddUnReferencedObjectTest());
        suite.addTest(new AddNewRegisteredObjectTest());
        suite.addTest(new SetToNullTest());
        suite.addTest(new SetReferencedObjectTest());
        suite.addTest(new SetUnReferencedObjectTest());
        suite.addTest(new SetNewRegisteredObjectTest());
        suite.addTest(new SetInNewRegisteredObjectTest());
        suite.addTest(new UnitOfWorkRevertAndResumeTest());
        suite.addTest(new UnitOfWorkRevertAndResumeTestDuplicates());
        suite.addTest(new DeepMergeCloneSerializedTest());
        suite.addTest(new DeepMergeCloneSerializedNewTest());
        return suite;
    }

    public void setup() {
        //This test is not supported by App driver for DB2
        if (getSession().getLogin().getDriverClassName().equalsIgnoreCase("COM.ibm.db2.jdbc.app.DB2Driver")) {
            throw new TestWarningException("This test is not supported by App driver for DB2, use Net driver instead. ");
        }
    }
}
