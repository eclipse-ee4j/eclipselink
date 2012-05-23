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
package org.eclipse.persistence.testing.tests.spatial.jgeometry;

import java.util.ArrayList;
import java.util.List;

import oracle.spatial.geometry.JGeometry;
import org.eclipse.persistence.testing.models.spatial.jgeometry.SimpleSpatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.WrappedSpatial;

/**
 * SQL samples from C:\oracle\db\10.2\md\demo\examples\eginsert.sql
 * Note: Table re-named from TEST81 to SIMPLE_SPATIAL
 */
public class SampleGeometries {
    private int srid = 0;

    public SampleGeometries() {
    }

    public SampleGeometries(int srid) {
        this.srid = srid;
    }

    public int getSRID() {
        return this.srid;
    }

    /**
     * @return population objects for database initialization
     */
    public List<SimpleSpatial> simplePopulation() throws Exception {
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

    public List<WrappedSpatial> wrappedPopulation() throws Exception {
        List<WrappedSpatial> population = new ArrayList<WrappedSpatial>();

        population.add(new WrappedSpatial(1000, 1, pointCluster1()));
        population.add(new WrappedSpatial(1001, 2, pointCluster2()));
        population.add(new WrappedSpatial(1002, 3, simplyPolygon()));
        population.add(new WrappedSpatial(1003, 4, polygonOfCircularArcs()));
        population.add(new WrappedSpatial(1004, 5, circle()));
        population.add(new WrappedSpatial(1005, 6, rectangle()));
        population.add(new WrappedSpatial(1006, 7, 
                                          compoundLineAndRectangle()));
        population.add(new WrappedSpatial(1007, 8, 
                                          compoundPolygonWithRectangularHole()));
        population.add(new WrappedSpatial(1008, 9, geometry1008()));
        population.add(new WrappedSpatial(1009, 10, geometryWithNulls()));
        population.add(new WrappedSpatial(1010, 11, null));
        population.add(new WrappedSpatial(1011, 12, polygon1011()));
        population.add(new WrappedSpatial(1012, 13, polygon1012()));
        population.add(new WrappedSpatial(1013, 14, point()));

        return population;
    }

    /**
     * mdsys.sdo_geometry(5,
     *                   NULL, null,
     *                   mdsys.sdo_elem_info_array(1,1,3),
     *                   mdsys.sdo_ordinate_array(1.1, 1.1, 2.2, 2.2, 3.3, 4.4)));
     */
    public JGeometry pointCluster1() {
        Object[] points = 
            new Object[] { new double[] { 1.1, 1.1 }, new double[] { 2.2, 
                                                                     2.2 }, 
                           new double[] { 3.3, 4.4 } };
        return JGeometry.createMultiPoint(points, 2, getSRID());
    }

    public JGeometry pointCluster2() {

        Object[] points = 
            new Object[] { new double[] { 5, 6 }, new double[] { 7, 8 } };
        return JGeometry.createMultiPoint(points, 2, getSRID());
    }

    /**
     * mdsys.sdo_geometry(3,
     *            NULL, null,
     *            mdsys.sdo_elem_info_array(1,3,1),
     *            mdsys.sdo_ordinate_array(1, 1, 2, 2, 3, 3, 3, 1, 1, 1))
     */
    public JGeometry simplyPolygon() {
        double[] points = new double[] { 1, 1, 2, 2, 3, 3, 3, 1, 1, 1 };
        return JGeometry.createLinearPolygon(points, 2, getSRID());
    }

    /**
     * A polygon made up of circular arcs. Each triple of vertices make up an
     * arc and the end-point of arc1 is the first point of arc2
     * i.e. arc1= {(0,1), (1,0), (0,-1)} and arc2 = {(0,-1), (-1,0), (0,1)}
     *
     * mdsys.sdo_geometry(3,
     * NULL, null,
     * mdsys.sdo_elem_info_array(1,3,2),
     * mdsys.sdo_ordinate_array(0, 1, 1, 0, 0, -1, -1, 0, 0, 1)));
     */
    public JGeometry polygonOfCircularArcs() throws Exception {
        return new JGeometry(JGeometry.GTYPE_POLYGON, getSRID(), 
                             new int[] { 1, 3, 2 }, 
                             new double[] { 0, 1, 1, 0, 0, -1, -1, 0, 0, 1 });
    }

    /**
     * A circle. (1,0), (0,1), (0,-1) are three points on the circumference
     *
     * mdsys.sdo_geometry(3,
     * NULL, null,
     * mdsys.sdo_elem_info_array(1,3,4),
     * mdsys.sdo_ordinate_array(1, 0, 0, 1, 0, -1))
     */
    public JGeometry circle() throws Exception {
        return JGeometry.createCircle(1, 0, 0, 1, 0, -1, getSRID());
    }

    /**
     * A rectangle. (10,10) is the lower left corner and (20,20) the upper-right
     *
     * mdsys.sdo_geometry(3,
     * NULL, null,
     * mdsys.sdo_elem_info_array(1,3,3),
     * mdsys.sdo_ordinate_array(10, 10, 20, 20))
     */
    public JGeometry rectangle() throws Exception {
        return JGeometry.createLinearPolygon(new double[] { 10, 10, 10, 20, 20, 
                                                            20, 20, 10, 10, 
                                                            10 }, 2, 
                                             getSRID());
    }

    /**
     * A geometry collection consisting of one compound line and one rectangle
     *
     * mdsys.sdo_geometry(4,
     * NULL, null,
     * mdsys.sdo_elem_info_array(1,4,2, 1,2,1, 7,2,2, 17,3,3),
     * mdsys.sdo_ordinate_array(50,50,
     * 50,30,
     * 10,30,
     * 10,50,
     * 20,60,
     * 30,50,
     * 40,60,
     * 50,50,
     * 25,35,
     * 35,40))
     */
    public JGeometry compoundLineAndRectangle() throws Exception {
        return new JGeometry(JGeometry.GTYPE_COLLECTION, getSRID(), 
                             new int[] { 1, 4, 2, 1, 2, 1, 7, 2, 2, 17, 3, 3 }, 
                             new double[] { 50, 50, 50, 30, 10, 30, 10, 50, 20, 
                                            60, 30, 50, 40, 60, 50, 50, 25, 35, 
                                            35, 40 });
    }

    /**
     * A compound polygon with a rectangular hole
     *
     * mdsys.sdo_geometry(3,
     * NULL, null,
     * mdsys.sdo_elem_info_array(1,5,2, 1,2,1, 7,2,2, 17,3,3),
     * mdsys.sdo_ordinate_array(50,50,
     * 50,30,
     * 10,30,
     * 10,50,
     * 20,60,
     * 30,50,
     * 40,60,
     * 50,50,
     * 25,35,
     * 35,40))
     */
    public JGeometry compoundPolygonWithRectangularHole() throws Exception {
        return new JGeometry(JGeometry.GTYPE_POLYGON, getSRID(), 
                             new int[] { 1, 5, 2, 1, 2, 1, 7, 2, 2, 17, 3, 3 }, 
                             new double[] { 50, 50, 50, 30, 10, 30, 10, 50, 20, 
                                            60, 30, 50, 40, 60, 50, 50, 25, 35, 
                                            35, 40 });
    }

    /**
     * ??
     *
     * mdsys.sdo_geometry(3,
     * NULL, null,
     * mdsys.sdo_elem_info_array(1,3,1, 11,3,3),
     * mdsys.sdo_ordinate_array(50,50,
     * 50,30,
     * 10,30,
     * 10,50,
     * 50,50,
     * 25,35,
     * 35,40))
     */
    public JGeometry geometry1008() throws Exception {
        return new JGeometry(JGeometry.GTYPE_POLYGON, getSRID(), 
                             new int[] { 1, 3, 1, 11, 3, 3 }, 
                             new double[] { 50, 50, 50, 30, 10, 30, 10, 50, 50, 
                                            50, 25, 35, 35, 40 });
    }

    /**
     * This geometry will be ignored by the index creation routines
     *
     * mdsys.sdo_geometry(0, NULL, null, NULL, NULL)
     *
     * NOTE: JGeometries created this way are returned as NULL. Writing into the
     * database this way will leave a non-null instance in the cache until it
     * is refreshed or leaves the cache.
     */
    public JGeometry geometryWithNulls() throws Exception {
        return new JGeometry(0, getSRID(), null, null);
    }

    /**
     * ??
     *
     * mdsys.sdo_geometry(3,  null,
     * mdsys.sdo_point_type (12.5, 13.6, 14.7),
     * mdsys.sdo_elem_info_array(1,3,1, 11,3,3),
     * mdsys.sdo_ordinate_array(50,50,
     * 50,30,
     * 10,30,
     * 10,50,
     * 50,50,
     * 25,35,
     * 35,40))
     */
    public JGeometry polygon1011() throws Exception {
        return new JGeometry(JGeometry.GTYPE_POLYGON, getSRID(), 12.5, 13.6, 
                             14.7, new int[] { 1, 3, 1, 11, 3, 3 }, 
                             new double[] { 50, 50, 50, 30, 10, 30, 10, 50, 50, 
                                            50, 25, 35, 35, 40 });
    }

    /**
     * ??
     *
     * mdsys.sdo_geometry(3,  null,
     * mdsys.sdo_point_type (12.5, 13.6, NULL),
     * mdsys.sdo_elem_info_array(1,3,1, 11,3,3),
     * mdsys.sdo_ordinate_array(50,50,
     * 50,30,
     * 10,30,
     * 10,50,
     * 50,50,
     * 25,35,
     * 35,40))
     */
    public JGeometry polygon1012() throws Exception {
        return new JGeometry(JGeometry.GTYPE_POLYGON, getSRID(), 12.5, 13.6, 
                             0.0, new int[] { 1, 3, 1, 11, 3, 3 }, 
                             new double[] { 50, 50, 50, 30, 10, 30, 10, 50, 50, 
                                            50, 25, 35, 35, 40 });
    }

    /**
     * Just a point geometry. This method can used to save storage if all
     * geometries in a table are Points
     *
     * mdsys.sdo_geometry(1,  null,
     * mdsys.sdo_point_type (12.5, 13.6, NULL),
     * NULL, NULL)
     */
    public JGeometry point() throws Exception {
        return JGeometry.createPoint(new double[] { 12.5, 13.6 }, 2, 
                                     getSRID());
    }
}

