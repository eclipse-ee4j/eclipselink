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

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.SimpleSpatial;



/**
 * SQL samples from C:\oracle\db\10.2\md\demo\examples\eginsert.sql
 * Note: Table re-named from TEST81 to SIMPLE_SPATIAL
 */
public class DeleteTests extends SimpleSpatialTestCase {

    public DeleteTests(String name){
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DeleteTests");
        suite.addTest(new DeleteTests("testAnySingleDelete"));

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
        
        
        public void testAnySingleDelete() throws Exception {
        int initialQuantity = countSimpleSpatial(session);

        UnitOfWork uow = session.acquireUnitOfWork();

        SimpleSpatial ss = (SimpleSpatial)uow.readObject(SimpleSpatial.class);
        assertNotNull("No SimpleSpatial found", ss);
        uow.deleteObject(ss);

        uow.writeChanges();
        int afterCount = countSimpleSpatial(uow);

        assertEquals("Number of rows does not match", initialQuantity - 1, 
                     afterCount);

        uow.release();
    }

    public static int countSimpleSpatial(Session session) {
        ReportQuery rq = 
            new ReportQuery(SimpleSpatial.class, new ExpressionBuilder());

        rq.addCount();

        rq.setShouldReturnSingleValue(true);

        Number value = (Number)session.executeQuery(rq);

        return value.intValue();
    }
}
