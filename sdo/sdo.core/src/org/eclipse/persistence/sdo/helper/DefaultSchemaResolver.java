/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.sdo.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p><b>Purpose</b>: Default implementation of the org.eclipse.persistence.sdo.helper.SchemaResolver interface
 * <p><b>Responsibilities</b>:<ul>
 * <li> Given the source schema and namespace and schemaLocation values from an import or include return the referenced Schema
 * <li> If a baseSchemaLocation is set it will be prepended to all schemaLocations passed into the resovleSchema methods
 * <li> This implementation will try to open an Inputstream to a URL created from the schemaLocation and return a StreamSource based on that inputstream
 * </ul>
 *
 * @see org.eclipse.persistence.sdo.helper.SchemaResolver
 */
public class DefaultSchemaResolver implements SchemaResolver {
    private String baseSchemaLocation;

    public DefaultSchemaResolver() {
    }

    /**
       * Given the source schema and namespace and schemaLocation values from an import or include return the referenced Schema*
       * @param sourceXSD The Source object of the source schema
       * @param namespace The namespace portion of the import/include
       * @param schemaLocation The schemaLocation portion of the import/include
       * @return Source for the referenced Schema or null if processing the referenced schema should be skipped
       */
    public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
        if ((schemaLocation == null) || schemaLocation.equals("")) {
            AbstractSessionLog.getLog().log(SessionLog.WARNING, "sdo_missing_schemaLocation", new Object[] { namespace });
            return null;
        }

        String baseLocation = getBaseSchemaLocation();
        if (baseLocation != null) {
            schemaLocation = baseLocation + schemaLocation;
        }
        try {
            URL url = new URL(schemaLocation);
            InputStream is = url.openStream();
            return new StreamSource(is);
        } catch (IOException e) {        
            AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, "sdo_error_processing_referenced_schema", new Object[] {e.getClass().getName(), namespace, schemaLocation });
            AbstractSessionLog.getLog().logThrowable(AbstractSessionLog.FINEST, e);
        }
        return null;
    }

    /**
    * Optional baseSchemaLocation can be specified
    * If set this will be prepended to all schemaLocations passed into the resovleSchema methods
    * @param baseSchemaLocation optional baseSchemaLocation
    */
    public void setBaseSchemaLocation(String baseSchemaLocation) {
        this.baseSchemaLocation = baseSchemaLocation;
    }

    public String getBaseSchemaLocation() {
        return baseSchemaLocation;
    }
}