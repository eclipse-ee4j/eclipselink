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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping file
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     12/10/2008-1.1 Michael O'Brien 
 *       - 257606: Add orm.xml schema validation true/(false) flag support in persistence.xml
 *       
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.xml.sax.SAXException;

/**
 * ORM.xml reader.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLEntityMappingsReader {
    public static final String ORM_1_0_XSD = "org/eclipse/persistence/jpa/orm_1_0.xsd";
    public static final String ORM_1_0_NAMESPACE = "http://java.sun.com/xml/ns/persistence/orm";
    public static final String ECLIPSELINK_ORM_XSD_VERSION = "1.1";
    public static final String ECLIPSELINK_ORM_XSD_NAME = "eclipselink_orm_1_1.xsd";
    public static final String ECLIPSELINK_ORM_XSD ="xsd/" + ECLIPSELINK_ORM_XSD_NAME; 
    public static final String ECLIPSELINK_ORM_NAMESPACE = "http://www.eclipse.org/eclipselink/xsds/persistence/orm";
    
    private static XMLContext m_orm1_0Project;
    private static XMLContext m_eclipseLinkOrmProject;

    /**
     * INTERNAL:
     */
    protected static XMLEntityMappings read(URL mappingFileUrl, Reader reader1, Reader reader2, 
            ClassLoader classLoader, Properties properties) {
        // We are going to go through this method twice - once for NoServerPlatform and then for the actual ServerPlatfom implementation
        // Get the schema validation flag if present in the persistence unit properties
        boolean validateORMSchema = isORMSchemaValidationPerformed(properties);
        
        // -------------- Until bug 218047 is fixed. -----------------
        if (m_orm1_0Project == null) {
            m_orm1_0Project = new XMLContext(new XMLEntityMappingsMappingProject(ORM_1_0_NAMESPACE, ORM_1_0_XSD));
            m_eclipseLinkOrmProject = new XMLContext(new XMLEntityMappingsMappingProject(ECLIPSELINK_ORM_NAMESPACE, ECLIPSELINK_ORM_XSD));
        }
        
        // Unmarshall JPA format.
        XMLEntityMappings xmlEntityMappings;
        
        try {
        	XMLUnmarshaller unmarshaller = m_orm1_0Project.createUnmarshaller();
            useLocalSchemaForUnmarshaller(unmarshaller, ORM_1_0_XSD, validateORMSchema);
            xmlEntityMappings = (XMLEntityMappings) unmarshaller.unmarshal(reader1);
        } catch (Exception e) {
        	try {
        		XMLUnmarshaller unmarshaller = m_eclipseLinkOrmProject.createUnmarshaller();
                useLocalSchemaForUnmarshaller(unmarshaller, ECLIPSELINK_ORM_XSD, validateORMSchema);
                xmlEntityMappings = (XMLEntityMappings) unmarshaller.unmarshal(reader2);
        	} catch (Exception ee) {
        		throw ValidationException.errorParsingMappingFile(mappingFileUrl, ee);
            }
        }
        
        return xmlEntityMappings;
    }
   
    /**
     * Gets a reader from given URL.
     */
    private static InputStreamReader getInputStreamReader(URL url) throws IOException {
        java.net.URLConnection cnx1 = url.openConnection();
        //set to false to prevent locking of jar files on Windows. EclipseLink issue 249664
        cnx1.setUseCaches(false);
        return new InputStreamReader(cnx1.getInputStream(), "UTF-8");
    }

    public static XMLEntityMappings read(URL url, ClassLoader classLoader) throws IOException {
        return read(url, classLoader, null);
    }    

    /**
     * INTERNAL:
     * @param url
     * @param classLoader
     * @param properties - PersistenceUnitInfo properties on the project
     * @return
     * @throws IOException
     */
    public static XMLEntityMappings read(URL url, ClassLoader classLoader, Properties properties) throws IOException {
        InputStreamReader reader1 = null;
        InputStreamReader reader2 = null;
        
        try {
            try {
                //Get separate readers as the read method below is coded to seek through both of them
                reader1 = getInputStreamReader(url);
                reader2 = getInputStreamReader(url);
            } catch (UnsupportedEncodingException exception) {
                throw ValidationException.fatalErrorOccurred(exception);
            }

            XMLEntityMappings entityMappings = read(url, reader1, reader2, classLoader, properties);
            // Setting the mapping file here is very important! Do not remove.
            entityMappings.setMappingFile(url);
            return entityMappings;
        } finally {
            try {
                if (reader1 != null) {
                    reader1.close();
                }
                
                if (reader2 != null) {
                    reader2.close();
                }
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }
    }
    
    /**
     * This method allows you to set an XML schema on a given unmarshaller.  It will get the schema from the same
     * classloader that loaded this class and hence works for the case where the schema is shipped as part of EclipseLink
     * @param unmarshaller
     * @param schemaName
     * @param validateORMSchema
     * @throws IOException
     * @throws SAXException
     */
    private static void useLocalSchemaForUnmarshaller(XMLUnmarshaller unmarshaller, String schemaName, boolean validateORMSchema) throws IOException, SAXException{
        URL url = XMLEntityMappingsReader.class.getClassLoader().getResource(schemaName);
        InputStream schemaStream = url.openStream();
        StreamSource source = new StreamSource(url.openStream());
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
        Schema schema = schemaFactory.newSchema(source);
        try{
            // There is no need to set the schema if we are not validating
            if(validateORMSchema) {
                unmarshaller.setSchema(schema);
            }
        } catch (UnsupportedOperationException ex){
            // some parsers do not support setSchema.  In that case, setup validation another way
            if(validateORMSchema) {                
                unmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
            } else {
                // No validation is the default on a new XMLUnmarshaller
                unmarshaller.setValidationMode(XMLUnmarshaller.NONVALIDATING);
            }
        } finally{
            schemaStream.close();
        }
    }
    
    /**
     * INTERNAL:
     * Return whether the schema validation flag in the Persistence Unit 
     * eclipselink.orm.validate.schema is set to true or false.<br>
     * The default value is false.
     * @param properties - PersistenceUnitInfo properties on the project
     * @return
     */
    private static boolean isORMSchemaValidationPerformed(Properties properties) {
        // Get property from persistence.xml (we are not yet parsing sessions.xml)
       String value = EntityManagerFactoryProvider.getConfigPropertyAsString(
                PersistenceUnitProperties.ORM_SCHEMA_VALIDATION,
                properties,
                "false");
       // A true validation property value will override the default of false or NONVALIDATING
       if(null != value && value.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }            
    }
}
