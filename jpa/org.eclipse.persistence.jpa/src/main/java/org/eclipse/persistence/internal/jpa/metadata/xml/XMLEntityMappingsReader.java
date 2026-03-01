/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2024 IBM and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping file
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
//     12/10/2008-1.1 Michael O'Brien
//       - 257606: Add orm.xml schema validation true/(false) flag support in persistence.xml
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     02/14/2013-2.5 Guy Pelletier
//       - 338610: JPA 2.1 Functionality for Java EE 7 (JSR-338)
//     09/06/2017-2.7 Jody Grassel
//       - 521954: Eclipselink is not able to parse ORM XML files using the 2.2 schema
//     02/10/2022-4.0 Oracle
//       - JPA 3.1 UUID Support

package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.XMLHelper;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
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
    public static final String ORM_2_1_XSD = "org/eclipse/persistence/jpa/orm_2_1.xsd";
    public static final String ORM_2_1_NAMESPACE = "http://xmlns.jcp.org/xml/ns/persistence/orm";
    public static final String ORM_2_2_XSD = "org/eclipse/persistence/jpa/orm_2_2.xsd";
    public static final String ORM_2_2_NAMESPACE = "http://xmlns.jcp.org/xml/ns/persistence/orm";
    public static final String ORM_3_0_XSD = "org/eclipse/persistence/jpa/orm_3_0.xsd";
    public static final String ORM_3_0_NAMESPACE = "https://jakarta.ee/xml/ns/persistence/orm";
    public static final String ORM_3_1_XSD = "org/eclipse/persistence/jpa/orm_3_1.xsd";
    public static final String ORM_3_1_NAMESPACE = "https://jakarta.ee/xml/ns/persistence/orm";
    public static final String ORM_3_2_XSD = "org/eclipse/persistence/jpa/orm_3_2.xsd";
    public static final String ORM_3_2_NAMESPACE = "https://jakarta.ee/xml/ns/persistence/orm";
    public static final String ECLIPSELINK_ORM_XSD = "org/eclipse/persistence/jpa/eclipselink_orm_5_0.xsd";
    public static final String ECLIPSELINK_ORM_NAMESPACE = "http://www.eclipse.org/eclipselink/xsds/persistence/orm";

    private static XMLContext m_orm1_0Project;
    private static XMLContext m_orm2_0Project;
    private static XMLContext m_orm2_1Project;
    private static XMLContext m_orm2_2Project;
    private static XMLContext m_orm3_0Project;
    private static XMLContext m_orm3_1Project;
    private static XMLContext m_orm3_2Project;
    private static XMLContext m_eclipseLinkOrmProject;

    private static Schema m_orm1_0Schema;
    private static Schema m_orm2_0Schema;
    private static Schema m_orm2_1Schema;
    private static Schema m_orm2_2Schema;
    private static Schema m_orm3_0Schema;
    private static Schema m_orm3_1Schema;
    private static Schema m_orm3_2Schema;
    private static Schema m_eclipseLinkOrmSchema;

    private XMLEntityMappingsReader() {
    }

    /**
     * Check the orm.xml to determine which project and schema to use.
     * EclipseLink ORM is used if input Reader is null
     */
    private static Object[] determineXMLContextAndSchema(String file, Reader input, boolean validateSchema) throws Exception {
        Object[] context = new Object[2];
        if (input == null) {
            // No input, default it to the eclipselink orm project.
            context[0] = getEclipseLinkOrmProject();
            if (validateSchema) {
                context[1] = getEclipseLinkOrmSchema();
            }
        } else {
            SAXParserFactory factory = XMLHelper.createParserFactory(false);

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
                if (validateSchema) {
                    context[1] = getEclipseLinkOrmSchema();
                }
            } else if (contentHandler.getVersion() == null || contentHandler.getVersion().contains("1.")) {
                context[0] = getOrm1_0Project();
                if (validateSchema) {
                    context[1] = getOrm1_0Schema();
                }
            } else if (contentHandler.getVersion().contains("2.0")) {
                context[0] = getOrm2_0Project();
                if (validateSchema) {
                    context[1] = getOrm2_0Schema();
                }
            } else if (contentHandler.getVersion().contains("2.1")) {
                context[0] = getOrm2_1Project();
                if (validateSchema) {
                    context[1] = getOrm2_1Schema();
                }
            } else if (contentHandler.getVersion().contains("2.2")) {
                context[0] = getOrm2_2Project();
                if (validateSchema) {
                    context[1] = getOrm2_2Schema();
                }
            } else if (contentHandler.getVersion().contains("3.0")) {
                context[0] = getOrm3_0Project();
                if (validateSchema) {
                    context[1] = getOrm3_0Schema();
                }
            } else if (contentHandler.getVersion().contains("3.1")) {
                context[0] = getOrm3_1Project();
                if (validateSchema) {
                    context[1] = getOrm3_1Schema();
                }
            } else {
                context[0] = getOrm3_2Project();
                if (validateSchema) {
                    context[1] = getOrm3_2Schema();
                }
            }
        }

        return context;
    }

    /**
     * @return the Eclipselink orm project.
     */
    public static XMLContext getEclipseLinkOrmProject() {
        if (m_eclipseLinkOrmProject == null) {
            m_eclipseLinkOrmProject = new XMLContext(new XMLEntityMappingsMappingProject(ECLIPSELINK_ORM_NAMESPACE, ECLIPSELINK_ORM_XSD));
        }

        return m_eclipseLinkOrmProject;
    }

    /**
     * @return the Eclipselink orm schema.
     */
    public static Schema getEclipseLinkOrmSchema() throws IOException, SAXException {
        if (m_eclipseLinkOrmSchema == null) {
            m_eclipseLinkOrmSchema = loadLocalSchema(ECLIPSELINK_ORM_XSD);
        }

        return m_eclipseLinkOrmSchema;
    }

    /**
     * Gets a reader from given URL.
     */
    private static InputStreamReader getInputStreamReader(URL url) throws IOException {
        java.net.URLConnection cnx1 = url.openConnection();
        //set to false to prevent locking of jar files on Windows. EclipseLink issue 249664
        cnx1.setUseCaches(false);
        return new InputStreamReader(cnx1.getInputStream(), StandardCharsets.UTF_8);
    }

    /**
     * @return the JPA 1.0 orm project.
     */
    public static XMLContext getOrm1_0Project() {
        if (m_orm1_0Project == null) {
            m_orm1_0Project = new XMLContext(new XMLEntityMappingsMappingProject(ORM_1_0_NAMESPACE, ORM_1_0_XSD));
        }

        return m_orm1_0Project;
    }

    /**
     * @return the JPA 1.0 orm schema.
     */
    public static Schema getOrm1_0Schema() throws IOException, SAXException {
        if (m_orm1_0Schema == null) {
            m_orm1_0Schema = loadLocalSchema(ORM_1_0_XSD);
        }

        return m_orm1_0Schema;
    }

    /**
     * @return the JPA 2.0 orm project.
     */
    public static XMLContext getOrm2_0Project() {
        if (m_orm2_0Project == null) {
            m_orm2_0Project = new XMLContext(new XMLEntityMappingsMappingProject(ORM_2_0_NAMESPACE, ORM_2_0_XSD));
        }

        return m_orm2_0Project;
    }

    /**
     * @return the JPA 2.0 orm schema.
     */
    public static Schema getOrm2_0Schema() throws IOException, SAXException {
        if (m_orm2_0Schema == null) {
            m_orm2_0Schema = loadLocalSchema(ORM_2_0_XSD);
        }

        return m_orm2_0Schema;
    }

    /**
     * @return the JPA 2.1 orm project.
     */
    public static XMLContext getOrm2_1Project() {
        if (m_orm2_1Project == null) {
            m_orm2_1Project = new XMLContext(new XMLEntityMappingsMappingProject(ORM_2_1_NAMESPACE, ORM_2_1_XSD));
        }

        return m_orm2_1Project;
    }

    /**
     * @return the JPA 2.1 orm schema.
     */
    public static Schema getOrm2_1Schema() throws IOException, SAXException {
        if (m_orm2_1Schema == null) {
            m_orm2_1Schema = loadLocalSchema(ORM_2_1_XSD);
        }

        return m_orm2_1Schema;
    }

    /**
     * @return the JPA 2.2 orm project.
     */
    public static XMLContext getOrm2_2Project() {
        if (m_orm2_2Project == null) {
            m_orm2_2Project = new XMLContext(new XMLEntityMappingsMappingProject(ORM_2_2_NAMESPACE, ORM_2_2_XSD));
        }

        return m_orm2_2Project;
    }

    /**
     * @return the JPA 2.2 orm schema.
     */
    public static Schema getOrm2_2Schema() throws IOException, SAXException {
        if (m_orm2_2Schema == null) {
            m_orm2_2Schema = loadLocalSchema(ORM_2_2_XSD);
        }

        return m_orm2_2Schema;
    }

    /**
     * @return the JPA 3.0 orm project.
     */
    public static XMLContext getOrm3_0Project() {
        if (m_orm3_0Project == null) {
            m_orm3_0Project = new XMLContext(new XMLEntityMappingsMappingProject(ORM_3_0_NAMESPACE, ORM_3_0_XSD));
        }

        return m_orm3_0Project;
    }

    /**
     * @return the JPA 3.0 orm schema.
     */
    public static Schema getOrm3_0Schema() throws IOException, SAXException {
        if (m_orm3_0Schema == null) {
            m_orm3_0Schema = loadLocalSchema(ORM_3_0_XSD);
        }

        return m_orm3_0Schema;
    }

    /**
     * @return the JPA 3.1 orm project.
     */
    public static XMLContext getOrm3_1Project() {
        if (m_orm3_1Project == null) {
            m_orm3_1Project = new XMLContext(new XMLEntityMappingsMappingProject(ORM_3_1_NAMESPACE, ORM_3_1_XSD));
        }

        return m_orm3_1Project;
    }

    /**
     * @return the JPA 3.1 orm schema.
     */
    public static Schema getOrm3_1Schema() throws IOException, SAXException {
        if (m_orm3_1Schema == null) {
            m_orm3_1Schema = loadLocalSchema(ORM_3_1_XSD);
        }

        return m_orm3_1Schema;
    }

    /**
     * @return the JPA 3.1 orm project.
     */
    public static XMLContext getOrm3_2Project() {
        if (m_orm3_2Project == null) {
            m_orm3_2Project = new XMLContext(new XMLEntityMappingsMappingProject(ORM_3_2_NAMESPACE, ORM_3_2_XSD));
        }

        return m_orm3_2Project;
    }

    /**
     * @return the JPA 3.1 orm schema.
     */
    public static Schema getOrm3_2Schema() throws IOException, SAXException {
        if (m_orm3_2Schema == null) {
            m_orm3_2Schema = loadLocalSchema(ORM_3_2_XSD);
        }

        return m_orm3_2Schema;
    }

    /**
     * Free the project and schema objects to avoid holding onto the memory.
     * This can be done post-deployment to conserve memory.
     */
    public static void clear() {
        m_orm1_0Project = null;
        m_orm2_0Project = null;
        m_orm2_1Project = null;
        m_orm2_2Project = null;
        m_orm3_0Project = null;
        m_orm3_1Project = null;
        m_orm3_2Project = null;
        m_eclipseLinkOrmProject = null;

        m_orm1_0Schema = null;
        m_orm2_0Schema = null;
        m_orm2_1Schema = null;
        m_orm2_2Schema = null;
        m_orm3_0Schema = null;
        m_orm3_1Schema = null;
        m_orm3_2Schema = null;
        m_eclipseLinkOrmSchema = null;
    }

    /**
     * INTERNAL:
     * Return whether the schema validation flag in the Persistence Unit
     * eclipselink.orm.validate.schema is set to true or false.<br>
     * The default value is false.
     * @param properties - PersistenceUnitInfo properties on the project
     */
    private static boolean isORMSchemaValidationPerformed(Map<?, ?> properties) {
        // Get property from persistence.xml (we are not yet parsing sessions.xml)
        String value = EntityManagerFactoryProvider.getConfigPropertyAsString(PersistenceUnitProperties.ORM_SCHEMA_VALIDATION, properties, "false");
        // A true validation property value will override the default of false or NONVALIDATING
        return (value != null && value.equalsIgnoreCase("true"));
    }

    /**
     * Load the XML schema from the jar resource.
     */
    protected static Schema loadLocalSchema(String schemaName) throws IOException, SAXException {
        URL url = XMLEntityMappingsReader.class.getResource("/" + schemaName);

        try (InputStream schemaStream = url.openStream()) {
            StreamSource source = new StreamSource(schemaStream);
            SchemaFactory schemaFactory = XMLHelper.createSchemaFactory(XMLConstants.SCHEMA_URL, false);
            Schema schema = schemaFactory.newSchema(source);
            return schema;
        }
    }

    /**
     * INTERNAL:
     */
    public static XMLEntityMappings read(String sourceName, Reader reader, ClassLoader classLoader, Map<?, ?> properties){
        return read(sourceName, null, reader, classLoader, properties);
    }

    /**
     * INTERNAL:
     */
    protected static XMLEntityMappings read(String mappingFile, Reader reader1, Reader reader2, ClassLoader classLoader, Map<?, ?> properties) {
        // Get the schema validation flag if present in the persistence unit properties
        boolean validateORMSchema = isORMSchemaValidationPerformed(properties);

        // Unmarshall JPA format.
        XMLEntityMappings xmlEntityMappings;

        try {
            // First need to determine which context/schema to use, JPA 1.0, 2.0 or EclipseLink orm (only latest supported)
            Object[] context = determineXMLContextAndSchema(mappingFile, reader1, validateORMSchema);
            XMLUnmarshaller unmarshaller = ((XMLContext)context[0]).createUnmarshaller();

            EntityResolver resolver = new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) {
                    System.out.println("pId: " + publicId);
                    System.out.println("sId: " + systemId);
                    String name =  systemId.contains("eclipselink_") ? ECLIPSELINK_ORM_XSD : systemId;
                    int idx = systemId.lastIndexOf('/');
                    name = idx < 0 ? name : name.substring(idx + 1);
                    InputStream resource = XMLEntityMappingsReader.class.getResourceAsStream("/org/eclipse/persistence/jpa/" + name);
                    return resource == null ? null : new InputSource(resource);
                }
            };

            unmarshaller.setEntityResolver(resolver);
            if (validateORMSchema) {
                useLocalSchemaForUnmarshaller(unmarshaller, ((Schema)context[1]));
            }

            xmlEntityMappings = (XMLEntityMappings) unmarshaller.unmarshal(reader2);
        } catch (Exception exception) {
            throw ValidationException.errorParsingMappingFile(mappingFile, exception);
        }

        if (xmlEntityMappings != null) {
            xmlEntityMappings.setMappingFile(mappingFile);
        }

        return xmlEntityMappings;
    }

    /**
     * INTERNAL:
     * @param properties - PersistenceUnitInfo properties on the project
     */
    public static XMLEntityMappings read(URL url, ClassLoader classLoader, Map<?, ?> properties) throws IOException {
        //Get separate readers as the read method below is coded to seek through both of them
        try (InputStreamReader reader1 = getInputStreamReader(url);
             InputStreamReader reader2 = getInputStreamReader(url)) {
            return read(url.toString(), reader1, reader2, classLoader, properties);
        } catch (UnsupportedEncodingException exception) {
            throw ValidationException.fatalErrorOccurred(exception);
        } catch (IOException exception) {
            throw ValidationException.fileError(exception);
        }
    }

    /**
     * This method allows you to set an XML schema on a given unmarshaller. It
     * will get the schema from the same classloader that loaded this class and
     * hence works for the case where the schema is shipped as part of EclipseLink
     *
     */
    private static void useLocalSchemaForUnmarshaller(XMLUnmarshaller unmarshaller, Schema schema) {
        try {

            unmarshaller.setErrorHandler(new ErrorHandler() {
                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    if (exception.getException() instanceof EclipseLinkException) {
                        throw (EclipseLinkException) exception.getCause();
                    }
                }
            });

            unmarshaller.setSchema(schema);
        } catch (UnsupportedOperationException ex) {
            // Some parsers do not support setSchema.  In that case, setup validation another way.
            unmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
        }
    }
}
