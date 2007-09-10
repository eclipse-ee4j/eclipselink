/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.relationships;

import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;
import org.eclipse.persistence.testing.tests.jpa.CMP3TestModel;
import org.eclipse.persistence.testing.framework.JUnitTestCase;

/**
 * <p><b>Purpose</b>: To collect the tests that will test specifics of our
 * EJB3.0 implementation through the use of the EntityContainer.  In order for
 * this test model to work correctly the EntityContainer must be initialized
 * thought the comandline agent.
 */
public class CMP3RelationshipsTestModel extends CMP3TestModel{

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
