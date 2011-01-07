/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.Type;
import java.util.Map;
import javax.xml.namespace.QName;

/**
 * <p><b>Purpose</b>: Default implementation of the org.eclipse.persistence.sdo.helper.SchemaLocationResolver interface
 * By default set a Map keyed on QName of types and value is the schemaLocation
 *
 * @see org.eclipse.persistence.sdo.helper.SchemaLocationResolver
 */
public class DefaultSchemaLocationResolver implements SchemaLocationResolver {
    private Map schemaLocationMap;

    public DefaultSchemaLocationResolver(Map schemaLocationMap) {
        this.schemaLocationMap = schemaLocationMap;
    }

    /**
     * Return the value for the schemaLocation attribute of the generated Import
     * @param sourceType the source type
     * @param targetType the target type
     * @return the value for the schemaLocation attribute of the generated Import
     */
    public String resolveSchemaLocation(Type sourceType, Type targetType) {
        QName qname = new QName(targetType.getURI(), targetType.getName());
        String schemaLocation = (String)schemaLocationMap.get(qname);
        return schemaLocation;
    }

    /**
     * Set the map of schemaLocations keyed on QName of the target SDO Type
     * @param schemaLocations Map keyed on QName of the target SDO Type
     */
    public void setMap(Map schemaLocations) {
        schemaLocationMap = schemaLocations;
    }
}
