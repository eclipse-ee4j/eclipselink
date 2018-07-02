/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     05/05/2011-2.3 Chris Delahunt
//       - 344837: Extensibility - Metadata Repository
//     08/29/2016 Jody Grassel
//       - 500441: Eclipselink core has System.getProperty() calls that are not potentially executed under doPriv()
package org.eclipse.persistence.jpa.metadata;

import java.io.IOException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsReader;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetSystemProperty;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p><b>Purpose</b>: Support reading metadata for a persistence unit in an XML format from a URL and if the property is undefined,
 * it will look for a file.
 */
public class XMLMetadataSource extends MetadataSourceAdapter {

    /**
     * This method returns a Reader for an EclipseLink-ORM.xml.  It will use the
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
     * @see #getEntityMappings
     */
    public Reader getEntityMappingsReader(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {
        InputStreamReader reader = null;

        //read from a URL
        String mappingURLName = (String)getConfigPropertyLogDebug(
                PersistenceUnitProperties.METADATA_SOURCE_XML_URL,
                properties, log);
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
            String mappingFileName = (String)getConfigPropertyLogDebug(
                    PersistenceUnitProperties.METADATA_SOURCE_XML_FILE,
                    properties, log);
            if (mappingFileName != null && mappingFileName.length() > 0) {
                try {
                    URL fileURL = getFileURL(mappingFileName, classLoader, log);
                    if (fileURL != null) {
                        reader = new InputStreamReader(fileURL.openStream());
                    }
                } catch (IOException exception) {
                    throw ValidationException.fileError(exception);
                }
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

    /**
     * PUBLIC: This method is responsible for returning additional persistence
     * unit property overrides. It is called on initial deployment of the
     * persistence unit and when the persistence unit is reloaded to allow
     * customization of the persistence unit above and beyond what is packaged
     * in the persistence.xml and what is code into the application.
     * <p>
     * <b>IMPORTANT</b>: Although any property can be changed using this
     * approach it is important that users of this feature ensure compatible
     * configurations are supplied. As an example; overriding an application to
     * use RESOURCE_LOCAL when it was coded to use JTA would result in changes
     * not be written to the database.
     *
     * PersistenceUnitProperties.METADATA_SOURCE_PROPERTIES_FILE property will be used to get a file
     * resource from the classloader. Properties are read from the file.
     * If the property either not specified or contains an empty string then returns null.
     *
     * @since EclipseLink 2.4
     */
    public Map<String, Object> getPropertyOverrides(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {
        String propertiesFileName = (String)getConfigPropertyLogDebug(
                PersistenceUnitProperties.METADATA_SOURCE_PROPERTIES_FILE,
                properties, log);
        if (propertiesFileName == null || propertiesFileName.length() == 0) {
            return null;
        }

        try {
            URL fileURL = getFileURL(propertiesFileName, classLoader, log);
            if (fileURL != null) {
                Properties propertiesFromFile = new Properties();
                propertiesFromFile.load(fileURL.openStream());
                if (!propertiesFromFile.isEmpty()) {
                    return new HashMap(propertiesFromFile);
                } else {
                    return null;
                }
            } else {
                throw ValidationException.missingPropertiesFileForMetadataRepositoryConfig(propertiesFileName);
            }
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    protected static URL getFileURL(String fileName, ClassLoader classLoader, SessionLog log) throws IOException {
        Enumeration<URL> fileURLs = classLoader.getResources(fileName);

        if (!fileURLs.hasMoreElements()){
            fileURLs = classLoader.getResources("/./" + fileName);
        }

        if (fileURLs.hasMoreElements()) {
            URL nextURL = fileURLs.nextElement();
            if (fileURLs.hasMoreElements()) {
                // Switched to warning, same file can be on the classpath twice in some deployments,
                // should not be an error.
                log.logThrowable(SessionLog.FINER, SessionLog.METADATA, ValidationException.nonUniqueRepositoryFileName(fileName));
            }
            return nextURL;
        } else {
            return null;
        }
    }

    /**
     * Check the provided map for an object with the given name.  If that object is not available, check the
     * System properties.  Log the value returned if logging is enabled
     * @param propertyName
     * @param properties
     * @param log
     * @return
     */
    public Object getConfigPropertyLogDebug(final String propertyName, Map properties, SessionLog log) {
        Object value = null;
        if (properties != null) {
            value = properties.get(propertyName);
        }
        if (value == null) {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                value = AccessController.doPrivileged(new PrivilegedGetSystemProperty(propertyName));
            } else {
                value = System.getProperty(propertyName);
            }
        }
        if ((value != null) && (log !=  null)) {
            log.log(SessionLog.FINEST, SessionLog.PROPERTIES, "property_value_specified", new Object[]{propertyName, value});
        }
        return value;
    }
}
