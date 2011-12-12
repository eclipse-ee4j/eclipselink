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
 *     05/05/2011-2.3 Chris Delahunt 
 *       - 344837: Extensibility - Metadata Repository
 ******************************************************************************/  
package org.eclipse.persistence.jpa.metadata;

import java.io.IOException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsReader;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p><b>Purpose</b>: Support reading metadata for a persistence unit in an XML format from a URL and if the property is undefined,
 * it will look for a file.    
 */
public class XMLMetadataSource extends MetadataSourceAdapter {
    
    /**
     * This class returns a Reader for an EclipseLink-ORM.xml.  It will use the 
     * PersistenceUnitProperties.METADATA_SOURCE_XML_URL property if available to create an 
     * InputStreamReader from a URL, and if not available, use the 
     * PersistenceUnitProperties.METADATA_SOURCE_XML_FILE property will be used to get a file
     * resource from the classloader. 
     * It will throw a ValidationException if no reader can be returned. 
     * 
     * @param properties
     * @param classLoader
     * @param log - SessionLog used for status messages.
     * @return Reader - a InputStreamReader with data in the form of an EclipseLink-orm.xml
     * 
     * 
     * @see getEntityMappings
     */
    public Reader getEntityMappingsReader(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {
        InputStreamReader reader = null;
        
        //read from a URL
        String mappingURLName = EntityManagerFactoryProvider.getConfigPropertyAsString(
                PersistenceUnitProperties.METADATA_SOURCE_XML_URL,
                properties);
        if (mappingURLName !=null && mappingURLName.length()!=0) {
            try {
                URL url = new URL(mappingURLName);
                reader = new InputStreamReader(url.openStream());
                
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }
        
        //read a file using the classloader
        if (reader == null) {
            String mappingFileName = EntityManagerFactoryProvider.getConfigPropertyAsString(
                    PersistenceUnitProperties.METADATA_SOURCE_XML_FILE,
                    properties);
            try {
                Enumeration<URL> mappingFileURLs = classLoader.getResources(mappingFileName);
                
                if (!mappingFileURLs.hasMoreElements()){
                    mappingFileURLs = classLoader.getResources("/./" + mappingFileName);
                }
                
                if (mappingFileURLs.hasMoreElements()) {
                    URL nextURL = mappingFileURLs.nextElement();
    
                    if (mappingFileURLs.hasMoreElements()) {
                        // Switched to warning, same file can be on the classpath twice in some deployments,
                        // should not be an error.
                        log.logThrowable(SessionLog.FINER, SessionLog.EJB_OR_METADATA, ValidationException.nonUniqueRepositoryFileName(mappingFileName));
                    }

                    reader = new InputStreamReader(nextURL.openStream());
                } 
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }
        if (reader == null) {
            //being configured to use XMLMetadataSource and not having a source to read from should be an exception
            throw ValidationException.missingXMLMetadataRepositoryConfig();
        }
        return reader;
    }
    
    /**
     * This method is responsible for returning the object representation of the MetadataSource.
     * This implementation makes a call to getEntityMappingsReader to get a Reader which is passed to an 
     * XMLUnmarshaller, and closes the reader in a finally block.  
     * 
     * @return XMLEntityMappings - object representation of the EclipseLink-orm.xml for this repository
     */
    public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {
        Reader reader = getEntityMappingsReader(properties, classLoader, log);
        if (reader == null) {
            return null;
        }
        try {
            return XMLEntityMappingsReader.read(getRepositoryName(), reader, classLoader, properties);
        } finally {
            if (reader!=null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    //ignore so we rethrow original exception if there was one.
                }
            }
        }
    }
    
    /**
     * Used by getEntityMappings when creating the XMLEntityMappings as a way of describing where it was read from.  
     * Currently returns the current class's simple name.  
     * 
     * @return String - repository name to store in the XMLEntityMappings returned from getEntityMappings
     */
    public String getRepositoryName() {
        return getClass().getSimpleName();
    }
}
