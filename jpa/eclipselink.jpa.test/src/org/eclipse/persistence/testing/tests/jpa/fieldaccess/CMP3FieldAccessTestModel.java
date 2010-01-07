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
package org.eclipse.persistence.testing.tests.jpa.fieldaccess;

import org.eclipse.persistence.testing.models.jpa.fieldaccess.relationships.RelationshipsTableManager;
import org.eclipse.persistence.testing.tests.jpa.CMP3TestModel;
import org.eclipse.persistence.testing.framework.JUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.ExecuteUpdateTest;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.GetResultCollectionTest;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.GetResultListTest;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.GetSingleResultTest;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.IsolatedCacheTestSuite;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.NamedQueryDoesNotExistTest;
import org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.NamedQueryWithArgumentsTest;

/**
 * <p><b>Purpose</b>: To collect the tests that will run using field level annotation. 
 * Currently the tests contained are a duplicate of the tests in the 
 * CMP3RelationshipsTestModel.  In order for this test model to work correctly the 
 * EntityContainer must be initialized thought the comandline agent.
 */
public class CMP3FieldAccessTestModel extends CMP3TestModel{

    public void setup(){
        super.setup();
   		RelationshipsTableManager.getCreator().replaceTables(getServerSession());
    }

    public void addTests(){ 
        addTest(new GetSingleResultTest());
        addTest(new ExecuteUpdateTest());
        addTest(new GetResultCollectionTest());
        addTest(new GetResultListTest());
        addTest(new NamedQueryDoesNotExistTest());
        addTest(new NamedQueryWithArgumentsTest());
        addTests(JUnitTestCase.suite(IsolatedCacheTestSuite.class));
    }
   
}
