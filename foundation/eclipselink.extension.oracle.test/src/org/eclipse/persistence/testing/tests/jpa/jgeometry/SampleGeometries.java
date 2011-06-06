/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/08/2010 Andrei Ilitchev 
 *       Bug 300512 - Add FUNCTION support to extended JPQL
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.jgeometry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.models.jpa.structconverter.SimpleSpatial;

/**
 * SQL samples from C:\oracle\db\10.2\md\demo\examples\eginsert.sql
 * Extended the base class to support jpa.structconverter.SimpleSpatial
 * Note: Table re-named from TEST81 to JPA_JGEOMETRY
 * fields we renamed from {GID, GEOMETRY} to {ID, JGEOMETRY}
 */
public class SampleGeometries extends org.eclipse.persistence.testing.tests.spatial.jgeometry.SampleGeometries {
    private int srid = 0;

    public SampleGeometries() {
        super();
    }

    public SampleGeometries(int srid) {
        super(srid);
    }

    /**
     * @return population objects for database initialization
     */
    public List<SimpleSpatial> simpleJpaPopulation() throws Exception {
        List<SimpleSpatial> population = new ArrayList<SimpleSpatial>();

        population.add(new SimpleSpatial(1000, pointCluster1()));
        population.add(new SimpleSpatial(1001, pointCluster2()));
        population.add(new SimpleSpatial(1002, simplyPolygon()));
        population.add(new SimpleSpatial(1003, polygonOfCircularArcs()));
        population.add(new SimpleSpatial(1004, circle()));
        population.add(new SimpleSpatial(1005, rectangle()));
        population.add(new SimpleSpatial(1006, compoundLineAndRectangle()));
        population.add(new SimpleSpatial(1007, 
                                         compoundPolygonWithRectangularHole()));
        population.add(new SimpleSpatial(1008, geometry1008()));
        population.add(new SimpleSpatial(1009, geometryWithNulls()));
        population.add(new SimpleSpatial(1010, null));
        population.add(new SimpleSpatial(1011, polygon1011()));
        population.add(new SimpleSpatial(1012, polygon1012()));
        population.add(new SimpleSpatial(1013, point()));

        return population;
    }
}

