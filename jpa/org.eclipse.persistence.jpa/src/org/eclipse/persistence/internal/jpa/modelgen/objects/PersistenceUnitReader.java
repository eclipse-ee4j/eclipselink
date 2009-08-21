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
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnit;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXML;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXMLMappings;
import org.eclipse.persistence.oxm.XMLContext;

import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.PERSISTENCE_XML_FILE;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.PERSISTENCE_XML_FILE_DEFAULT;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.PERSISTENCE_XML_LOCATION;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.PERSISTENCE_XML_LOCATION_DEFAULT;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.PERSISTENCE_XML_PACKAGE;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.PERSISTENCE_XML_PACKAGE_DEFAULT;

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
    public PersistenceUnitReader(MetadataMirrorFactory factory) throws IOException {
        m_persistenceUnits = new ArrayList<PersistenceUnit>();
        
        initPersistenceUnits(factory);
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
    public static FileObject getFileObject(String pkg, String filename, StandardLocation standardLocation, ProcessingEnvironment processingEnv) {
        FileObject fileObject = null;
        try {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Package: " + pkg);
            processingEnv.getMessager().printMessage(Kind.NOTE, "Filename: " + filename);
            
            fileObject = processingEnv.getFiler().getResource(standardLocation, pkg, filename);
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
    
    /**
     * INTERNAL:
     */
    protected void initPersistenceUnits(MetadataMirrorFactory factory) throws IOException {
        ProcessingEnvironment processingEnv = factory.getProcessingEnvironment();
        Map<String, String> options = processingEnv.getOptions();
        String pkg = CanonicalModelProperties.getOption(PERSISTENCE_XML_PACKAGE, PERSISTENCE_XML_PACKAGE_DEFAULT, options);
        String stdLocation = CanonicalModelProperties.getOption(PERSISTENCE_XML_LOCATION, PERSISTENCE_XML_LOCATION_DEFAULT, options);
        String filename = CanonicalModelProperties.getOption(PERSISTENCE_XML_FILE, PERSISTENCE_XML_FILE_DEFAULT, options);
        
        FileObject fileObject = null;
        if (stdLocation.equals(CanonicalModelProperties.LOCATION.CP.name())) {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Reading off class path ... ");
            fileObject = getFileObject(pkg, filename, StandardLocation.CLASS_PATH, processingEnv);
        } else if (stdLocation.equals(CanonicalModelProperties.LOCATION.SP.name())) {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Reading off source path ... ");
            fileObject = getFileObject(pkg, filename, StandardLocation.SOURCE_PATH, processingEnv);
        } else if (stdLocation.equals(CanonicalModelProperties.LOCATION.APP.name())) {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Reading off annotation processor path ... ");
            fileObject = getFileObject(pkg, filename, StandardLocation.ANNOTATION_PROCESSOR_PATH, processingEnv);
        } else if (stdLocation.equals(CanonicalModelProperties.LOCATION.SO.name())) {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Reading off source output ... ");
            fileObject = getFileObject(pkg, filename, StandardLocation.SOURCE_OUTPUT, processingEnv);
        } else if (stdLocation.equals(CanonicalModelProperties.LOCATION.PCP.name())) {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Reading off platform class path ... ");
            fileObject = getFileObject(pkg, filename, StandardLocation.PLATFORM_CLASS_PATH, processingEnv);
        } else if (stdLocation.equals(CanonicalModelProperties.LOCATION.CO.name())) {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Reading off class output ... ");
            fileObject = getFileObject(pkg, filename, StandardLocation.CLASS_OUTPUT, processingEnv);
        }
        
        if (fileObject != null) {
            InputStream inStream = fileObject.openInputStream();
            XMLContext context = PersistenceXMLMappings.createXMLContext();
            PersistenceXML persistenceXML = (PersistenceXML) context.createUnmarshaller().unmarshal(inStream);
            inStream.close();
    
            if (! persistenceXML.getVersion().equals("1.0")) {
                for (SEPersistenceUnitInfo puInfo : persistenceXML.getPersistenceUnitInfos()) {
                    m_persistenceUnits.add(new PersistenceUnit(puInfo, factory));
                }
            }
        } else {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Unable to load persistence xml.");
        }
    }
}

