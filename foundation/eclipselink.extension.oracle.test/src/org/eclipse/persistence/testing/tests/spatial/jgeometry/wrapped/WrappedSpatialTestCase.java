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
package org.eclipse.persistence.testing.tests.spatial.jgeometry.wrapped;

import java.util.List;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.WrappedJGeometryTableCreator;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.WrappedSpatial;
import org.eclipse.persistence.testing.tests.spatial.jgeometry.SampleGeometries;
import org.eclipse.persistence.testing.tests.spatial.jgeometry.SimpleSpatialTestCase;
import org.eclipse.persistence.tools.schemaframework.TableCreator;

public class WrappedSpatialTestCase extends SimpleSpatialTestCase {

    public WrappedSpatialTestCase(String name){
        super(name);
    }
    
    public static void repopulate(DatabaseSession session, boolean replaceTables) throws Exception {       
        if (replaceTables){
            replaceTables(session);
        }
        UnitOfWork uow = session.acquireUnitOfWork();

        List existing = uow.readAllObjects(WrappedSpatial.class);
        uow.deleteAllObjects(existing);
        uow.commit();

        session.getIdentityMapAccessor().initializeIdentityMaps();

        SampleGeometries samples = new SampleGeometries(DEFAULT_SRID);

        uow = session.acquireUnitOfWork();
        uow.registerAllObjects(samples.wrappedPopulation());
        uow.commit();

        assertEquals(samples.wrappedPopulation().size(), countWrappedSpatial(session));
        session.getIdentityMapAccessor().initializeIdentityMaps();
    }

    public static void replaceTables(DatabaseSession session){

        session.executeNonSelectingSQL("DELETE FROM USER_SDO_GEOM_METADATA WHERE TABLE_NAME = 'WRAPPED_SPATIAL'");     

        TableCreator tableCreator = new WrappedJGeometryTableCreator();       
        tableCreator.replaceTables(session);

        session.executeNonSelectingSQL("INSERT INTO USER_SDO_GEOM_METADATA(TABLE_NAME, COLUMN_NAME, DIMINFO) VALUES('WRAPPED_SPATIAL', 'GEOMETRY.GEOM'," +
                " mdsys.sdo_dim_array(mdsys.sdo_dim_element('X', -100, 100, 0.005), mdsys.sdo_dim_element('Y', -100, 100, 0.005)))");     
        session.executeNonSelectingSQL("delete from WRAPPED_SPATIAL where gid between 1000 and 1013");
        session.executeNonSelectingSQL("CREATE INDEX wrapped_test_index on WRAPPED_SPATIAL(geometry.geom) indextype is mdsys.spatial_index");       
        session.executeNonSelectingSQL("commit");       
    }
    
    public static int countWrappedSpatial(DatabaseSession session) {
        ReportQuery rq = 
            new ReportQuery(WrappedSpatial.class, new ExpressionBuilder());
        rq.addCount();
        rq.setShouldReturnSingleValue(true);

        return ((Number)session.executeQuery(rq)).intValue();
    }
}
