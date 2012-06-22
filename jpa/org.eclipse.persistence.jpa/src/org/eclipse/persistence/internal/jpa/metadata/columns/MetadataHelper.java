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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.metadata.columns;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * This class only contains the common helper methods that can be accessed at
 * package-private level.
 * 
 * @author Kyle Chen
 * @since TopLink 11g
 */
final class MetadataHelper {
    /**
     * INTERNAL:
     * Helper method to return a field name from a candidate field name and a 
     * default field name.
     * 
     * Requires the context from where this method is called to output the 
     * correct logging message when defaulting the field name.
     *
     * In some cases, both the name and defaultName could be "" or null,
     * therefore, don't log a message and return name.
     */
    static String getName(String name, String defaultName, String context, MetadataLogger logger, String location) {
        return org.eclipse.persistence.internal.jpa.metadata.MetadataHelper.getName(name, defaultName, context, logger, location);
    }
    
    /**
     * INTERNAL:
     * Helper method to return a string value if specified, otherwise returns
     * the default value. 
     */
    static Integer getValue(Integer value, Integer defaultValue) {
        return org.eclipse.persistence.internal.jpa.metadata.MetadataHelper.getValue(value, defaultValue);
    }
    
    /**
     * INTERNAL:
     * Helper method to return a string value if specified, otherwise returns
     * the default value.
     */
    static String getValue(String value, String defaultValue) {
        return org.eclipse.persistence.internal.jpa.metadata.MetadataHelper.getValue(value, defaultValue);
    }
}
