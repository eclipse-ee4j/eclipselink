/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.profiler;

import java.util.*;

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
            String property = System.getProperty("org.eclipse.persistence.fetchgroupmonitor");
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
