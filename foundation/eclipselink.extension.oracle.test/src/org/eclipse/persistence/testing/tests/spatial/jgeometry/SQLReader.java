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

import java.util.*;
import oracle.spatial.geometry.JGeometry;

import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.models.spatial.jgeometry.SimpleSpatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.Spatial;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.MyGeometry;
import org.eclipse.persistence.testing.models.spatial.jgeometry.wrapped.WrappedSpatial;

/**
 * Helper class to read and compare SimpleSpatial results read using SQL versus
 * those read using TopLink.
 * 
 * @author Doug Clarke
 * @since Oracle TopLink 10.1.3.1 Preview (build 060803)
 */
public class SQLReader {
    private String sql;
    private List<Spatial> results;

    public SQLReader(Session session, String sql) {
        this.sql = sql;
        readResults(session);
    }

    private void readResults(Session session) {
        DataReadQuery query = new DataReadQuery(getSql());
        query.setIsNativeConnectionRequired(true);
        List<Map> rawResults = (List)session.executeQuery(query);
        this.results = new ArrayList<Spatial>(rawResults.size());

        for (Map rawResult: rawResults) {
            long gid = ((Number)rawResult.get("GID")).longValue();
            Object geom = rawResult.get("GEOMETRY");

            if (geom instanceof MyGeometry) {
                this.results.add(new WrappedSpatial(gid, (MyGeometry)geom));
            } else {
                this.results.add(new SimpleSpatial(gid, (JGeometry)geom));
            }
        }
    }

    public String getSql() {
        return sql;
    }

    public List<Spatial> getResults() {
        return results;
    }

    /**
     * Return NULL if they match or an error message if they do not.
     */
    public String compare(List<Spatial> values) {
        if (getResults() == null && values == null) {
            return null;
        }
        if (getResults() == null || values == null) {
            return "SQL = " + getResults() + " - TopLink = " + values;
        }
        if (getResults().size() != values.size()) {
            return "SQL size = " + getResults().size() + " - TopLink size = " + 
                values.size();
        }

        for (int index = 0; index < getResults().size(); index++) {
            Spatial sql = getResults().get(index);
            Spatial tl = values.get(index);

            if (!compareSimpleSpatial(sql, tl)) {
                return "SQL: " + sql + "does not equal: " + tl;
            }
        }

        return null;
    }

    private boolean compareSimpleSpatial(Spatial ss1, Spatial ss2) {
        if (ss1 == null) {
            return ss2 == null;
        }
        if (ss1.getId() != ss2.getId()) {
            return false;
        }
        if (ss1.getJGeometry() == null || ss1.getJGeometry().getType() == 0) {
            return ss2.getJGeometry() == null || 
                ss2.getJGeometry().getType() == 0;
        }

        if (ss1.getJGeometry().getSRID() != ss2.getJGeometry().getSRID()) {
            return false;
        }
        if (ss1.getJGeometry().getType() != ss2.getJGeometry().getType()) {
            return false;
        }
        if (ss1.getJGeometry().getElemInfo() == null) {
            return ss2.getJGeometry().getElemInfo() == null;
        }
        if (ss1.getJGeometry().getElemInfo().length != 
            ss2.getJGeometry().getElemInfo().length) {
            return false;
        }
        for (int index = 0; index < ss1.getJGeometry().getElemInfo().length; 
             index++) {
            if (ss1.getJGeometry().getElemInfo()[index] != 
                ss2.getJGeometry().getElemInfo()[index]) {
                return false;
            }
        }

        if (ss1.getJGeometry().getOrdinatesArray().length != 
            ss2.getJGeometry().getOrdinatesArray().length) {
            return false;
        }
        for (int index = 0; 
             index < ss1.getJGeometry().getOrdinatesArray().length; index++) {
            if (ss1.getJGeometry().getOrdinatesArray()[index] != 
                ss2.getJGeometry().getOrdinatesArray()[index]) {
                return false;
            }
        }
        return true;
    }
}
