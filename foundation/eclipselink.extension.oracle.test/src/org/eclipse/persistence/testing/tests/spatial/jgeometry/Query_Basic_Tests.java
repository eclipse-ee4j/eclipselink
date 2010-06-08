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
package org.eclipse.persistence.testing.tests.spatial.jgeometry;

import java.util.List;
import java.util.Vector;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import oracle.spatial.geometry.JGeometry;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.databaseaccess.BindCallCustomParameter;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.spatial.jgeometry.SimpleSpatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.Spatial;


/**
 * Query tests that do not involve using a spatial operator in the selection
 * criteria.
 */
public class Query_Basic_Tests extends SimpleSpatialTestCase {
    
    public Query_Basic_Tests(String name){
        super(name);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("Query_Basic_Tests");
        suite.addTest(new Query_Basic_Tests("testReadAll"));
        suite.addTest(new Query_Basic_Tests("testReadNotNullGeometry"));
        suite.addTest(new Query_Basic_Tests("testRawUsage"));

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
       
    public void testReadAll() throws Exception {
        String sql = "select GID, GEOMETRY from SIMPLE_SPATIAL ORDER BY GID";
        SQLReader reader = new SQLReader(session, sql);

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        raq.addAscendingOrdering("id");

        List<Spatial> results = 
            (List<Spatial>)session.executeQuery(raq);

        String compareResult = reader.compare(results);

        assertNull(compareResult, compareResult);
    }

    /**
     * Comparisons to geometry != NULL always fail.
     */
    public void testReadNotNullGeometry() throws Exception {
        ReadAllQuery roq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = roq.getExpressionBuilder();
        roq.setSelectionCriteria(eb.get("geometry").notNull());

        List<SimpleSpatial> results = 
            (List<SimpleSpatial>)session.executeQuery(roq);

        assertNotNull(results);
        int countAll = DeleteTests.countSimpleSpatial(session);

        assertEquals("More then one found", countAll - 1, results.size());
    }

    public void testRawUsage() throws Exception {
        Vector stringsVec = new Vector();
        stringsVec.add("SDO_WITHIN_DISTANCE("); // Geometry 1
        stringsVec.add(","); // Geometry 2
        stringsVec.add(","); // PARAMS
        stringsVec.add(")");

        ExpressionOperator op = new ExpressionOperator(-1, stringsVec);
        op.bePrefix();

        ReadAllQuery raq = new ReadAllQuery(SimpleSpatial.class);
        ExpressionBuilder eb = raq.getExpressionBuilder();

        JGeometry comparison = 
            JGeometry.createMultiPoint(new Object[] { new double[] { 5, 6 }, 
                                                      new double[] { 7, 8 } }, 
                                       2, 0);

        Vector args = new Vector(2);
        args.add(new BindCallCustomParameter(comparison));
        args.add("DISTANCE=10");

        Expression criteria = 
            eb.get("geometry").performOperator(op, args).equal("TRUE");

        raq.setSelectionCriteria(criteria);

        session.executeQuery(raq);
    }
    


}
