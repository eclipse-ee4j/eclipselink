/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/10/2009-2.0 Guy Pelletier 
 *       - 267391: JPA 2.0 implement/extend/use an APT tooling library for MetaModel API canonical classes 
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.modelgen.objects;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnit;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXML;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXMLMappings;
import org.eclipse.persistence.oxm.XMLContext;

/**
 * Used to read persistence units through the java annotation processing API. 
 * 
 * @author Guy Pelletier, Peter Krogh
 * @since Eclipselink 2.0
 */
public class PersistenceUnitReader {
    protected List<PersistenceUnit> m_persistenceUnits;
    
    /**
     * INTERNAL:
     */
    public PersistenceUnitReader(MetadataMirrorFactory factory, String persistenceXMLPath) throws IOException {
        m_persistenceUnits = new ArrayList<PersistenceUnit>();
        
        FileObject fileObject;
        
        if (persistenceXMLPath == null) {
            factory.getProcessingEnvironment().getMessager().printMessage(Kind.NOTE, "Reading default META_INF/persistence xml");
            fileObject = getFileObject("META-INF/persistence.xml", factory.getProcessingEnvironment());
        } else {
            String persistenceXMLLocation;
            persistenceXMLPath = persistenceXMLPath.replace("\\", "/");
            
            if (persistenceXMLPath.endsWith("/")) {
                persistenceXMLLocation = persistenceXMLPath + "persistence.xml";
            } else {
                persistenceXMLLocation = persistenceXMLPath + "/persistence.xml";
            }
            
            factory.getProcessingEnvironment().getMessager().printMessage(Kind.NOTE, "Reading specified persistence xml off class path: " + persistenceXMLLocation);
            fileObject = getFileObject(persistenceXMLLocation, StandardLocation.CLASS_PATH, factory.getProcessingEnvironment());
            
            if (fileObject == null) {
                factory.getProcessingEnvironment().getMessager().printMessage(Kind.NOTE, "Reading specified persistence xml off src path: " + persistenceXMLLocation);
                fileObject = getFileObject(persistenceXMLLocation, StandardLocation.SOURCE_PATH, factory.getProcessingEnvironment());
                
                if (fileObject == null) {
                    factory.getProcessingEnvironment().getMessager().printMessage(Kind.NOTE, "Reading specified persistence xml off annotation processor path: " + persistenceXMLLocation);
                    fileObject = getFileObject(persistenceXMLLocation, StandardLocation.ANNOTATION_PROCESSOR_PATH, factory.getProcessingEnvironment());
                    
                    if (fileObject == null) {
                        //factory.getProcessingEnvironment().getMessager().printMessage(Kind.NOTE, "Reading specified persistence xml off source output path: " + persistenceXMLLocation);
                        //fileObject = getFileObject(persistenceXMLLocation, StandardLocation.SOURCE_OUTPUT, factory.getProcessingEnvironment());
                         
                        if (fileObject == null) {
                            factory.getProcessingEnvironment().getMessager().printMessage(Kind.NOTE, "Reading specified persistence xml off platform class path: " + persistenceXMLLocation);
                            fileObject = getFileObject(persistenceXMLLocation, StandardLocation.PLATFORM_CLASS_PATH, factory.getProcessingEnvironment());
                        }
                    }
                }
            }
            
        }
        
        //FileObject fileObject = getFileObject("META-INF/persistence.xml", factory.getProcessingEnvironment());

        if (fileObject != null) {
            InputStream in = fileObject.openInputStream();
            XMLContext context = PersistenceXMLMappings.createXMLContext();
            PersistenceXML persistenceXML = (PersistenceXML) context.createUnmarshaller().unmarshal(in);
            in.close();
    
            if (! persistenceXML.getVersion().equals("1.0")) {
                for (SEPersistenceUnitInfo puInfo : persistenceXML.getPersistenceUnitInfos()) {
                    m_persistenceUnits.add(new PersistenceUnit(puInfo, factory));
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    public static FileObject getFileObject(String filename, ProcessingEnvironment processingEnv) {
        return getFileObject(filename, StandardLocation.CLASS_OUTPUT, processingEnv);
    }
    
    /**
     * INTERNAL:
     */
    public static FileObject getFileObject(String filename, StandardLocation standardLocation, ProcessingEnvironment processingEnv) {
        FileObject fileObject = null;
        try {
            fileObject = processingEnv.getFiler().getResource(standardLocation, "", filename);
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Kind.NOTE, "File was not found: " + filename);
            return null;
        }
        
        return fileObject;
    }
    
    /**
     * INTERNAL:
     */
    public List<PersistenceUnit> getPersistenceUnits() {
        return m_persistenceUnits;
    }
}

