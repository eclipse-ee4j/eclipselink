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
package org.eclipse.persistence.testing.tests.spatial.jgeometry;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.spatial.jgeometry.SimpleSpatial;
import junit.framework.*;
import junit.extensions.TestSetup;
import org.eclipse.persistence.testing.framework.TestProblemException;

/**
 * SQL samples from C:\oracle\db\10.2\md\demo\examples\eginsert.sql
 * Note: Table re-named from TEST81 to SIMPLE_SPATIAL
 */
public class CreateTests extends SimpleSpatialTestCase {
    
    public CreateTests(String name){
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("CreateTests");
        suite.addTest(new CreateTests("testCreate_SRID_0_WithoutBinding"));
        suite.addTest(new CreateTests("testCreate_SRID_0_WithBinding"));
        suite.addTest(new CreateTests("testInsertNullWithBinding"));
        suite.addTest(new CreateTests("testInsertNullWithoutBinding"));

        return new TestSetup(suite) {
            protected void setUp(){
                try{
                    SimpleSpatialTestCase.repopulate(getSession(), true);
                } catch (Exception e){
                    throw new TestProblemException("Could not setup JGeometry test model", e);
                }
            }

            protected void tearDown() {
            }
        };
    }
    
    public void testCreate_SRID_0_WithoutBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(false);
        
        SimpleSpatialTestCase.repopulate(getSession(), false);
    }

    public void testCreate_SRID_0_WithBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(true);
        
        SimpleSpatialTestCase.repopulate(getSession(), false);
    }
    
    public void testInsertNullWithBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(true);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        SimpleSpatial ss = (SimpleSpatial)uow.newInstance(SimpleSpatial.class);
        ss.setId(123456789);
        
        uow.writeChanges();
        uow.release();
    }

    public void testInsertNullWithoutBinding() throws Exception {
        getSession().getLogin().setShouldBindAllParameters(false);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        
        SimpleSpatial ss = (SimpleSpatial)uow.newInstance(SimpleSpatial.class);
        ss.setId(123456789);
        
        uow.writeChanges();
        uow.release();
    }
}
