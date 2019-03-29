/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     08/29/2016 Jody Grassel
//       - 500441: Eclipselink core has System.getProperty() calls that are not potentially executed under doPriv()
package org.eclipse.persistence.tools.profiler;

import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.queries.*;

/**
 * <p><b>Purpose</b>:
 * Provide a very simple low overhead means for measuring query executions, and cache hits.
 * This can be useful for performance analysis in a complex system.
 * This monitor is enabled through the System property "org.eclipse.persistence.querymonitor=true" or
 * through the persistence.xml property eclipselink.profiler=QueryMonitor.
 * It dumps the number of query cache hits, and executions (misses) once every 100s.
 *
 * @author James Sutherland
 * @since TopLink 10.1.3
 */
public class QueryMonitor {

    public static final Map<String, Number> cacheHits = new ConcurrentHashMap<>();
    public static final Map<String, Number> cacheMisses = new ConcurrentHashMap<>();
    public static long dumpTime = System.currentTimeMillis();
    public static Boolean shouldMonitor;

    public static boolean shouldMonitor() {
        if (shouldMonitor == null) {
            shouldMonitor = Boolean.FALSE;
            String property = PrivilegedAccessHelper.getSystemProperty("org.eclipse.persistence.querymonitor");
            if ((property != null) && (property.toUpperCase().equals("TRUE"))) {
                shouldMonitor = Boolean.TRUE;
            }
        }
        return shouldMonitor.booleanValue();
    }

    public static void checkDumpTime() {
        if ((System.currentTimeMillis() - dumpTime) > 100000) {
            dumpTime = System.currentTimeMillis();
            StringWriter writer = new StringWriter();
            writer.write("Query Monitor:");
            writer.write(String.valueOf(dumpTime));
            writer.write("\n");
            writer.write("Query");
            writer.write("\t");
            writer.write("Cache");
            writer.write("\t");
            writer.write("Database");
            writer.write("\n");
            Set<String> queries = new TreeSet<>(cacheMisses.keySet());
            queries.addAll(cacheHits.keySet());
            for (String query : queries) {
                Number hits = cacheHits.get(query);
                if (hits == null) {
                    hits = Integer.valueOf(0);
                }
                Number misses = cacheMisses.get(query);
                if (misses == null) {
                    misses = Integer.valueOf(0);
                }
                writer.write(query);
                writer.write("\t");
                writer.write(hits.toString());
                writer.write("\t");
                writer.write(misses.toString());
                writer.write("\n");
            }
            System.out.println(writer.toString());
        }
    }

    public static void incrementReadObjectHits(ReadObjectQuery query) {
        checkDumpTime();
        String name = query.getReferenceClass().getName() + "-findByPrimaryKey";
        Number hits = cacheHits.get(name);
        if (hits == null) {
            hits = Integer.valueOf(0);
        }
        hits = Integer.valueOf(hits.intValue() + 1);
        cacheHits.put(name, hits);
    }

    public static void incrementReadObjectMisses(ReadObjectQuery query) {
        checkDumpTime();
        String name = query.getReferenceClass().getName() + "-findByPrimaryKey";
        Number misses = cacheMisses.get(name);
        if (misses == null) {
            misses = Integer.valueOf(0);
        }
        misses = Integer.valueOf(misses.intValue() + 1);
        cacheMisses.put(name, misses);
    }

    public static void incrementReadAllHits(ReadAllQuery query) {
        checkDumpTime();
        String name = query.getReferenceClass().getName();
        if (query.getName() == null) {
            name = name + "-findAll";
        } else {
            name = name + "-" + query.getName();
        }
        Number hits = cacheHits.get(name);
        if (hits == null) {
            hits = Integer.valueOf(0);
        }
        hits = Integer.valueOf(hits.intValue() + 1);
        cacheHits.put(name, hits);
    }

    public static void incrementReadAllMisses(ReadAllQuery query) {
        checkDumpTime();
        String name = query.getReferenceClass().getName();
        if (query.getName() == null) {
            name = name + "-findAll";
        } else {
            name = name + "-" + query.getName();
        }
        Number misses = cacheMisses.get(name);
        if (misses == null) {
            misses = Integer.valueOf(0);
        }
        misses = Integer.valueOf(misses.intValue() + 1);
        cacheMisses.put(name, misses);
    }

    public static void incrementInsert(WriteObjectQuery query) {
        checkDumpTime();
        String name = query.getReferenceClass().getName() + "-insert";
        Number misses = cacheMisses.get(name);
        if (misses == null) {
            misses = Integer.valueOf(0);
        }
        misses = Integer.valueOf(misses.intValue() + 1);
        cacheMisses.put(name, misses);
    }

    public static void incrementUpdate(WriteObjectQuery query) {
        checkDumpTime();
        String name = query.getReferenceClass().getName() + "-update";
        Number misses = cacheMisses.get(name);
        if (misses == null) {
            misses = Integer.valueOf(0);
        }
        misses = Integer.valueOf(misses.intValue() + 1);
        cacheMisses.put(name, misses);
    }

    public static void incrementDelete(DeleteObjectQuery query) {
        checkDumpTime();
        String name = query.getReferenceClass().getName() + "-delete";
        Number misses = cacheMisses.get(name);
        if (misses == null) {
            misses = Integer.valueOf(0);
        }
        misses = Integer.valueOf(misses.intValue() + 1);
        cacheMisses.put(name, misses);
    }
}
