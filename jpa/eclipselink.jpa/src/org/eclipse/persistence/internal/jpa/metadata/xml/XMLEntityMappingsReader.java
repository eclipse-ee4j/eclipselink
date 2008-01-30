/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import java.net.URL;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.exceptions.XMLMarshalException;


import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

import org.eclipse.persistence.sessions.Project;

/**
 * ORM.xml reader.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLEntityMappingsReader {
	private static final String ORM_SCHEMA = "xsd/orm_1_0.xsd";
    private static Project project;

    /**
     * INTERNAL:
     */
    protected static XMLEntityMappings read(Reader reader, ClassLoader classLoader) {
        StringWriter writer;
        Document document;
        
        try {
            writer = new StringWriter(4096);
            char[] c = new char[4096];
            int r = 0;
            
            while ((r = reader.read(c)) != -1) {
                writer.write(c, 0, r);
            }
            
        	XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
            XMLParser parser = xmlPlatform.newXMLParser();
            parser.setNamespaceAware(true);
            parser.setWhitespacePreserving(false);
            parser.setValidationMode(XMLParser.SCHEMA_VALIDATION);
            XMLSchemaResolver xmlSchemaResolver = new XMLSchemaResolver();
            URL eclipseLinkSchemaURL = xmlSchemaResolver.resolveURL(ORM_SCHEMA);
            parser.setEntityResolver(xmlSchemaResolver);
            parser.setXMLSchema(eclipseLinkSchemaURL);

            try {
                document = parser.parse(new StringReader(writer.toString()));
            } catch (Exception parseException) {
            	parseException.printStackTrace();
            	throw parseException;
            }
        } catch (Exception exception) {
            throw XMLMarshalException.unmarshalException(exception);
        }

        if (project == null) {
            project = new XMLEntityMappingsMappingProject();
        }

        // Marshall JPA format.
        XMLLogin xmlLogin = new XMLLogin();
        xmlLogin.setDatasourcePlatform(new org.eclipse.persistence.oxm.platform.DOMPlatform());
        project.setDatasourceLogin(xmlLogin);
        XMLContext context = new XMLContext(project);
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        
        return (XMLEntityMappings) unmarshaller.unmarshal(document);
    }
    
    /**
     * INTERNAL:
     */
    public static XMLEntityMappings read(InputStream inputStream, ClassLoader classLoader) {
        InputStreamReader reader = null;
        try {
            try {
                reader = new InputStreamReader(inputStream, "UTF-8");
            } catch (UnsupportedEncodingException exception) {
                throw ValidationException.fatalErrorOccurred(exception);
            }

            return read(reader, classLoader);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    private static class XMLSchemaResolver implements EntityResolver {
        private static final String SCHEMA_DIR = "xsd/";
        private static final String ORM_SCHEMA = "orm_1_0.xsd";

        /**
         * INTERNAL:
         */
        public XMLSchemaResolver() {
            super();
        }

        /**
         * INTERNAL:
         * Resolve the XSD.
         */
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if (ORM_SCHEMA.equals(systemId)) {
                URL url = resolveURL(SCHEMA_DIR + ORM_SCHEMA);
                if (null == url) {
                    return null;
                }
                
                return new InputSource(url.openStream());
            }
            
            return null;
        }

        /**
         * INTERNAL:
         * Return the URL for the resource.
         */
        public URL resolveURL(String resource) {
            // The xsd is always in the toplink.jar, use our class loader.
            return getClass().getClassLoader().getResource(resource);
        }
    }
}
