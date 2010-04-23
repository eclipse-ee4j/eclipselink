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
package org.eclipse.persistence.sdo.helper;

import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.exceptions.SDOException;
import org.xml.sax.InputSource;

/**
 * <p><b>Purpose</b>: Allow the contained schema resolver to resolve a schema based on a given namespace and schema location, and
 * return either the resolved schema source or null, depending on whether the schema had been processed previously. 
 * <p><b>Responsibilities</b>:<ul>
 * <li> Allow the contained schema resolver to return the referenced Schema given the source schema and namespace and 
 * schemaLocation values from an import or include
 * <li> Keep track of previously processed schemas to avoid multiple processing and infinite looping
 * </ul>
 *
 * @see org.eclipse.persistence.sdo.helper.SchemaResolver
 * @see org.eclipse.persistence.sdo.helper.DefaultSchemaResolver
 */
public class SchemaResolverWrapper {
    private SchemaResolver schemaResolver;
    private List<String> systemIdList;

    /**
     * This constructor sets schemaResolver to the given value.
     * 
     * @param resolver the SchemaResolver implementation that will be used to resolve
     * imports/includes from a give source schema.
     */
    public SchemaResolverWrapper(SchemaResolver resolver) {
        schemaResolver = resolver;
        systemIdList = new ArrayList<String>();
    }

    /**
     * Allow the SchemaResolver implementation to attempt to return the referenced Schema based on 
     * given source schema, namespace and schemaLocation values from an import or include.  If the
     * resolver fails, this method will attemt to resolve the schema 
     * 
     * @param sourceXSD The Source object of the source schema
     * @param namespace The namespace portion of the import/include
     * @param schemaLocation The schemaLocation portion of the import/include
     * @return Source for the referenced Schema or null if processing the referenced
     * schema should be skipped
     */
    public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
        // Add sourceXSD's SystemId to the processed schema list to avoid re-processing
        addSchemaToList(sourceXSD.getSystemId());

        Source schemaSource = schemaResolver.resolveSchema(sourceXSD, namespace, schemaLocation);
        if (schemaSource != null) {
            String sysId = schemaSource.getSystemId();
            if (shouldProcessSchema(sysId)) {
                return schemaSource;
            }
        }
        return null;
    }
    
    /**
     * 
     */
    public Source resolveSchema(String systemId) {
        if(!addSchemaToList(systemId)) {
            return null;
        }
        try {
            InputSource source = schemaResolver.resolveEntity(null, systemId);
            if(source != null) {
                return new StreamSource(source.getCharacterStream());
            }
        } catch(Exception ex) {
            throw SDOException.errorResolvingSchema(ex);
        } 
        return null;
    }

    /**
     * Indicates if the schema represented by the given systemId (schema URL string) should be processed.
     * If systemId exists in the list, it should not be processed; otherwise, it will be added to the 
     * list and true returned to indicate processing is required. 
     *  
     * @param systemId a String that conforms to the URI syntax
     * @return true if systemId is null or does not exist in the list of previously processed schemas, 
     * false otherwise 
     */
    private boolean shouldProcessSchema(String systemId) {
        if (systemId == null) {
            return true;
        }
        return addSchemaToList(systemId);
    }

    /**
     * Add the given SystemId to the list of processed schemas, if it isn't already in the list.  
     * 
     * @param systemId a String that conforms to the URI syntax
     * @return true if systemId was added to the list (hence the associated schema should be processed), 
     * false if the id was not added to the list or systemId is null.
     */
    private boolean addSchemaToList(String systemId) {
        if (systemId == null || systemIdList.contains(systemId)) {
            return false;
        }
        systemIdList.add(systemId);
        return true;
    }

    /**
     * Return the SchemaResolver for this wrapper instance.
     * 
     * @return
     */
    public SchemaResolver getSchemaResolver() {
        return schemaResolver;
    }
}
