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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping file
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     12/10/2008-1.1 Michael O'Brien 
 *       - 257606: Add orm.xml schema validation true/(false) flag support in persistence.xml
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * ORM.xml reader.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLEntityMappingsReader {
    public static final String ORM_1_0_XSD = "org/eclipse/persistence/jpa/orm_1_0.xsd";
    public static final String ORM_1_0_NAMESPACE = "http://java.sun.com/xml/ns/persistence/orm";
    public static final String ORM_2_0_XSD = "org/eclipse/persistence/jpa/orm_2_0.xsd";
    public static final String ORM_2_0_NAMESPACE = "http://java.sun.com/xml/ns/persistence/orm";
    public static final String ECLIPSELINK_ORM_XSD = "org/eclipse/persistence/jpa/eclipselink_orm_2_3.xsd";
    public static final String ECLIPSELINK_ORM_NAMESPACE = "http://www.eclipse.org/eclipselink/xsds/persistence/orm";
    
    private static XMLContext m_orm1_0Project;
    private static XMLContext m_orm2_0Project;
    private static XMLContext m_eclipseLinkOrmProject;
    
    private static Schema m_orm1_0Schema;
    private static Schema m_orm2_0Schema;
    private static Schema m_eclipseLinkOrmSchema;

    /**
     * Check the orm.xml to determine which project and schema to use.  
     * EclipseLink ORM is used if input Reader is null
     */
    private static Object[] determineXMLContextAndSchema(String file, Reader input) throws Exception {
        Object[] context = new Object[2];
        if (input == null) {
            context[0] = getEclipseLinkOrmProject();
            context[1] = getEclipseLinkOrmSchema();
            return context;
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        
        // create a SAX parser
        SAXParser parser = factory.newSAXParser();
            
        // create an XMLReader
        XMLReader xmlReader = parser.getXMLReader();

        ORMContentHandler contentHandler = new ORMContentHandler();
        xmlReader.setContentHandler(contentHandler);
        InputSource inputSource = new InputSource(input);
        xmlReader.parse(inputSource);
        
        if (contentHandler.isEclipseLink()) {
            context[0] = getEclipseLinkOrmProject();
            context[1] = getEclipseLinkOrmSchema();
        } else if ((contentHandler.getVersion() == null) || (contentHandler.getVersion().indexOf("2") == -1)) {
            context[0] = getOrm1Project();
            context[1] = getOrm1_0Schema();
        } else {
            context[0] = getOrm2Project();
            context[1] = getOrm2_0Schema();
        }
        return context;
    }
    
    /**
     * INTERNAL:
     */
    protected static XMLEntityMappings read(String mappingFile, Reader reader1, Reader reader2, ClassLoader classLoader, Properties properties) {
        // Get the schema validation flag if present in the persistence unit properties
        boolean validateORMSchema = isORMSchemaValidationPerformed(properties);
        
        // Unmarshall JPA format.
        XMLEntityMappings xmlEntityMappings;
        try {
            // First need to determine which context/schema to use, JPA 1.0, 2.0 or EclipseLink orm (only latest supported)
            Object[] context = determineXMLContextAndSchema(mappingFile, reader1);
            XMLUnmarshaller unmarshaller = ((XMLContext)context[0]).createUnmarshaller();
            if (validateORMSchema) {
                useLocalSchemaForUnmarshaller(unmarshaller, ((Schema)context[1]));
            }
            xmlEntityMappings = (XMLEntityMappings) unmarshaller.unmarshal(reader2);
        } catch (Exception exception) {
            throw ValidationException.errorParsingMappingFile(mappingFile, exception);
        }
        if (xmlEntityMappings != null){
            xmlEntityMappings.setMappingFile(mappingFile);
        }
        return xmlEntityMappings;
    }

    /**
     * INTERNAL:
     */
    public static XMLEntityMappings read(String sourceName, Reader reader, ClassLoader classLoader, Properties properties){
        return read(sourceName, null, reader, classLoader, properties);
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
        InputStreamReader reader3 = null;
        
        try {
            try {
                //Get separate readers as the read method below is coded to seek through both of them
                reader1 = getInputStreamReader(url);
                reader2 = getInputStreamReader(url);
                return read(url.toString(), reader1, reader2, classLoader, properties);
            } catch (UnsupportedEncodingException exception) {
                throw ValidationException.fatalErrorOccurred(exception);
            }
        } finally {
            try {
                if (reader1 != null) {
                    reader1.close();
                }
                
                if (reader2 != null) {
                    reader2.close();
                }

                if (reader3 != null) {
                    reader3.close();
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
    private static void useLocalSchemaForUnmarshaller(XMLUnmarshaller unmarshaller, Schema schema) {
        try {
            unmarshaller.setSchema(schema);
        } catch (UnsupportedOperationException ex) {
            // Some parsers do not support setSchema.  In that case, setup validation another way.
            unmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
        }
    }
    
    /**
     * Load the XML schema from the jar resource.
     */
    protected static Schema loadLocalSchema(String schemaName) throws IOException, SAXException {
        URL url = XMLEntityMappingsReader.class.getClassLoader().getResource(schemaName);
        InputStream schemaStream = url.openStream();
        try {
            StreamSource source = new StreamSource(url.openStream());
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.SCHEMA_URL);
            Schema schema = schemaFactory.newSchema(source);
            return schema;
        } finally {
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
        if (null != value && value.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public static XMLContext getOrm1Project() {
        if (m_orm1_0Project == null) {
            m_orm1_0Project = new XMLContext(new XMLEntityMappingsMappingProject(ORM_1_0_NAMESPACE, ORM_1_0_XSD));
        }
        return m_orm1_0Project;
    }

    public static XMLContext getOrm2Project() {
        if (m_orm2_0Project == null) {
            m_orm2_0Project = new XMLContext(new XMLEntityMappingsMappingProject(ORM_2_0_NAMESPACE, ORM_2_0_XSD));
        }
        return m_orm2_0Project;
    }

    public static XMLContext getEclipseLinkOrmProject() {
        if (m_eclipseLinkOrmProject == null) {
            m_eclipseLinkOrmProject = new XMLContext(new XMLEntityMappingsMappingProject(ECLIPSELINK_ORM_NAMESPACE, ECLIPSELINK_ORM_XSD));
        }
        return m_eclipseLinkOrmProject;
    }

    public static Schema getOrm1_0Schema() throws IOException, SAXException {
        if (m_orm1_0Schema == null) {
            m_orm1_0Schema = loadLocalSchema(ORM_1_0_XSD);
        }
        return m_orm1_0Schema;
    }

    public static Schema getOrm2_0Schema() throws IOException, SAXException {
        if (m_orm2_0Schema == null) {
            m_orm2_0Schema = loadLocalSchema(ORM_2_0_XSD);
        }
        return m_orm2_0Schema;
    }

    public static Schema getEclipseLinkOrmSchema() throws IOException, SAXException {
        if (m_eclipseLinkOrmSchema == null) {
            m_eclipseLinkOrmSchema = loadLocalSchema(ECLIPSELINK_ORM_XSD);
        }
        return m_eclipseLinkOrmSchema;
    }
}
