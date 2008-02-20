/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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

import java.net.URI;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.logging.AbstractSessionLog;

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
     * Given the source schema and namespace and schemaLocation values from an import
     * or include return the referenced Schema.
     * 
     * @param sourceXSD The Source object of the source schema
     * @param namespace The namespace portion of the import/include
     * @param schemaLocation The schemaLocation portion of the import/include
     * @return Source for the referenced Schema or null if processing the referenced
     * schema should be skipped
     */
    public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
        try {
            URL schemaUrl = null;
	        if (getBaseSchemaLocation() != null) {
            	// Attempt to resolve the schema location against the base location
                schemaUrl = new URI(getBaseSchemaLocation()).resolve(schemaLocation).toURL();
	        } else {
            	schemaUrl = new URL(schemaLocation);
	        }
	        return new StreamSource(schemaUrl.toExternalForm());
        } catch (Exception e) {        
            AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, "sdo_error_processing_referenced_schema", new Object[] {e.getClass().getName(), namespace, schemaLocation });
            AbstractSessionLog.getLog().logThrowable(AbstractSessionLog.FINEST, e);
        }
        return null;
    }

    /**
    * Optional baseSchemaLocation can be specified
    * If set, all schemaLocations passed into the resolveSchema methods will be resolved
    * against this base location according to the java.net.URI API
    * 
    * @param baseSchemaLocation optional baseSchemaLocation
    */
    public void setBaseSchemaLocation(String baseSchemaLocation) {
        this.baseSchemaLocation = baseSchemaLocation;
    }

    public String getBaseSchemaLocation() {
        return baseSchemaLocation;
    }
}