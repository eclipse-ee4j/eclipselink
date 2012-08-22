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
package org.eclipse.persistence.sdo.helper;

import java.net.URI;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.xml.sax.InputSource;

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
            String baseLoc = getBaseSchemaLocation();
            if (baseLoc == null) {
                // No base location, use schema location

                URI schemaUri = new URI(schemaLocation);
                if(!(schemaUri.isAbsolute()) && sourceXSD.getSystemId() != null) {
                    baseLoc = sourceXSD.getSystemId();
                    schemaUrl = new URI(baseLoc).resolve(schemaUri).toURL();
                } else {
                    // No base location, use schema location
                    schemaUrl = new URI(schemaLocation).toURL();
                }
            } else {
                // May need to resolve against the base location
                URI schemaUri = new URI(schemaLocation);
                // If the schema location is absolute, ignore base location
                if (schemaUri.isAbsolute()) {
                    schemaUrl = schemaUri.toURL();
                } else {
                    // Attempt to resolve the schema location against the base location
                    URI baseUri = new URI(baseLoc);
                    // Handle 'jar:' case - we will need to try and create the schema URL manually
                    if (baseUri.isOpaque() && baseUri.getScheme().equals("jar")) {
                        // Example - jar:file:/C:/schema.jar!/my.xsd
                        // Strip off anything after the last '/' character (base location could represent a file)
                        schemaUrl = new URI(baseLoc.substring(0, baseLoc.lastIndexOf("/") + 1) + schemaLocation).toURL();
                    } else {
                        schemaUrl = new URI(baseLoc).resolve(schemaUri).toURL();
                    }
                }                
            }
            return new StreamSource(schemaUrl.toExternalForm());
        } catch (Exception e) {
            AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, "sdo_error_processing_referenced_schema", new Object[] { e.getClass().getName(), namespace, schemaLocation });
            AbstractSessionLog.getLog().logThrowable(AbstractSessionLog.FINEST, e);
        }
        return null;
    }

    /**
     * Satisfy EntityResolver interface implementation.
     * Allow resolution of external entities.
     * 
     * @param publicId
     * @param systemId
     * @return null
     */
    public InputSource resolveEntity(String publicId, String systemId) {
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
