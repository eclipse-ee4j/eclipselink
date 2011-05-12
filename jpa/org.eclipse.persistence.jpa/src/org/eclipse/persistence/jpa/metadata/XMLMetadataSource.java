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
 * <p><b>Purpose</b>: Support reading metadata for a persistence unit in an XML format from a file
 * or URL.  If undefined, it will look for  repository-orm.xml as a URL and then as a file.  
 */
public class XMLMetadataSource extends MetadataSourceAdapter {
    public Reader getEntityMappingsStream(Map properties, ClassLoader classLoader, SessionLog log) {
        InputStreamReader reader = null;
        
        //read from a URL
        String mappingURLName = EntityManagerFactoryProvider.getConfigPropertyAsString(
                PersistenceUnitProperties.METADATA_SOURCE_XML_URL,
                properties);
        if (mappingURLName !=null && !mappingURLName.isEmpty()) {
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
                        log.logThrowable(SessionLog.FINER, ValidationException.nonUniqueRepositoryFileName(mappingFileName));
                    }

                    reader = new InputStreamReader(nextURL.openStream());
                } 
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }
        if (reader == null) {
            throw ValidationException.missingXMLMetadataRepositoryConfig();
        }
        return reader;
    }
    
    public XMLEntityMappings getEntityMappings(Map properties, ClassLoader classLoader, SessionLog log) {
        Reader reader = getEntityMappingsStream(properties, classLoader, log);
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
    
    public String getRepositoryName() {
        return getClass().getSimpleName();
    }
}
