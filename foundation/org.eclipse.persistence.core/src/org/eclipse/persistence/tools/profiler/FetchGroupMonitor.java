/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     08/29/2016 Jody Grassel
//       - 500441: Eclipselink core has System.getProperty() calls that are not potentially executed under doPriv()
package org.eclipse.persistence.tools.profiler;

import java.util.*;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * <p><b>Purpose</b>:
 * Provide a very simple low overhead means for measuring fetch group field usage.
 * This can be useful for performance analysis in a complex system.
 * This monitor is enabled through the System property "org.eclipse.persistence.fetchgroupmonitor=true".
 * It dumps the attribute used for a class every time a new attribute is accessed.
 *
 * @author James Sutherland
 * @since TopLink 10.1.3.2
 */
public class FetchGroupMonitor {

    public static Hashtable fetchedAttributes = new Hashtable();
    public static Boolean shouldMonitor;

    public static boolean shouldMonitor() {
        if (shouldMonitor == null) {
            shouldMonitor = Boolean.FALSE;
            String property = PrivilegedAccessHelper.getSystemProperty("org.eclipse.persistence.fetchgroupmonitor");
            if ((property != null) && (property.toUpperCase().equals("TRUE"))) {
                shouldMonitor = Boolean.TRUE;
            }
        }
        return shouldMonitor.booleanValue();
    }

    public static void recordFetchedAttribute(Class domainClass, String attributeName) {
        if (! shouldMonitor()) {
            return;
        }
        synchronized (fetchedAttributes) {
            Set classesFetchedAttributes = (Set) fetchedAttributes.get(domainClass);
            if (classesFetchedAttributes == null) {
                classesFetchedAttributes = new HashSet();
                fetchedAttributes.put(domainClass, classesFetchedAttributes);
            }
            if (!classesFetchedAttributes.contains(attributeName)) {
                classesFetchedAttributes.add(attributeName);
                System.out.println("Fetched attributes: " + domainClass + " - " + classesFetchedAttributes);
            }
        }
    }
}
