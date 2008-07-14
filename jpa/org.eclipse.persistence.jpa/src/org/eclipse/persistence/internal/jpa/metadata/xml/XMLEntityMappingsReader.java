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
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping file
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.xml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

/**
 * ORM.xml reader.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class XMLEntityMappingsReader {
    public static final String ORM_XSD = "javax/persistence/orm_1_0.xsd";
    public static final String ORM_NAMESPACE = "http://java.sun.com/xml/ns/persistence/orm";
    public static final String ECLIPSELINK_ORM_XSD = "xsd/eclipselink_orm_1_0.xsd";
    public static final String ECLIPSELINK_ORM_NAMESPACE = "http://www.eclipse.org/eclipselink/xsds/persistence/orm";
    
    //private static XMLContext m_xmlContext;
    private static XMLContext m_ormProject;
    private static XMLContext m_eclipseLinkOrmProject;
    
    /**
     * INTERNAL:
     */
    protected static XMLEntityMappings read(Reader reader1, Reader reader2, ClassLoader classLoader) {
        // -------------- Until bug 218047 is fixed. -----------------
        if (m_ormProject == null) {
            m_ormProject = new XMLContext(new XMLEntityMappingsMappingProject(ORM_NAMESPACE, ORM_XSD));
            m_eclipseLinkOrmProject = new XMLContext(new XMLEntityMappingsMappingProject(ECLIPSELINK_ORM_NAMESPACE, ECLIPSELINK_ORM_XSD));
        }
        
        // Unmarshall JPA format.
        XMLEntityMappings xmlEntityMappings;
        
        try {
            XMLUnmarshaller unmarshaller = m_ormProject.createUnmarshaller();
            unmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
            xmlEntityMappings = (XMLEntityMappings) unmarshaller.unmarshal(reader1);
        } catch (Exception e) {
            try {
                XMLUnmarshaller unmarshaller = m_eclipseLinkOrmProject.createUnmarshaller();
                unmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
                xmlEntityMappings = (XMLEntityMappings) unmarshaller.unmarshal(reader2);
            } catch (Exception ee) {
                throw new RuntimeException(ee);
            }
        }
        
        return xmlEntityMappings;
        
        // ---------- When bug 218047 is fixed. -----------------
        /*
        if (m_xmlContext == null) {
            List<Project> projects = new ArrayList<Project>();
            projects.add(new XMLEntityMappingsMappingProject(ORM_NAMESPACE, ORM_XSD));
            projects.add(new XMLEntityMappingsMappingProject(ECLIPSELINK_ORM_NAMESPACE, ECLIPSELINK_ORM_XSD));
            
            m_xmlContext = new XMLContext(projects);
        }
        
        // Unmarshall JPA format.
        XMLUnmarshaller unmarshaller = m_xmlContext.createUnmarshaller();
        unmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
        return (XMLEntityMappings) unmarshaller.unmarshal(reader);
        */
    }
    
    /**
     * INTERNAL:
     */
    public static XMLEntityMappings read(URL url, ClassLoader classLoader) throws IOException {
        InputStreamReader reader1 = null;
        InputStreamReader reader2 = null;
        
        try {
            try {
                reader1 = new InputStreamReader(url.openStream(), "UTF-8");
                reader2 = new InputStreamReader(url.openStream(), "UTF-8");
            } catch (UnsupportedEncodingException exception) {
                throw ValidationException.fatalErrorOccurred(exception);
            }

            XMLEntityMappings entityMappings = read(reader1, reader2, classLoader);
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
}
