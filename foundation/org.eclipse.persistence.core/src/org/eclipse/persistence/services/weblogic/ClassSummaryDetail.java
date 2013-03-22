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
 *     @author  mobrien
 *     @since   EclipseLink 1.1 enh# 248748
 *     10/20/2008-1.1M4 Michael O'Brien 
 *       - 248748: Add WebLogic 10.3 specific JMX MBean attributes and functions
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/248748</link>
 *     06/30/2010-2.1.1 Michael O'Brien 
 *       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
 *       Move JMX MBean generic registration code up from specific platforms
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>        
 ******************************************************************************/  
package org.eclipse.persistence.services.weblogic;

import org.eclipse.persistence.services.ClassSummaryDetailBase;

/**
 * The class is used internally by the Portable JMX Framework to convert 
 * model specific classes into Open Types so that the attributes of model class can
 * be exposed by MBeans.
 */
public class ClassSummaryDetail extends ClassSummaryDetailBase {

    static {
        COMPOSITE_TYPE_TYPENAME = "org.eclipse.persistence.services.weblogic";
        COMPOSITE_TYPE_DESCRIPTION = "org.eclipse.persistence.services.weblogic.ClassSummaryDetail";
    }
    
    /**
     * Construct a ClassSummaryDetail instance. The PropertyNames annotation is used 
     * to be able to construct a ClassSummaryDetail instance out of a CompositeData
     * instance. See MXBeans documentation for more details.
     */
    public ClassSummaryDetail(String className, String cacheType, String configuredSize,String currentSize , String parentClassName) {
        super(className, cacheType, configuredSize, currentSize , parentClassName);
    }
}
