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
import org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnit;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXML;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXMLMappings;
import org.eclipse.persistence.oxm.XMLContext;

import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_PERSISTENCE_XML_FILE;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_PERSISTENCE_XML_FILE_DEFAULT;

/**
 * Used to read persistence units through the java annotation processing API. 
 * 
 * @author Guy Pelletier, Peter Krogh
 * @since EclipseLink 1.2
 */
public class PersistenceUnitReader {
    protected List<PersistenceUnit> persistenceUnits;
    
    /**
     * INTERNAL:
     */
    public PersistenceUnitReader(MetadataMirrorFactory factory) throws IOException {
        persistenceUnits = new ArrayList<PersistenceUnit>();
        
        initPersistenceUnits(factory);
    }
    
    /**
     * INTERNAL:
     */
    public FileObject getFileObject(String filename, ProcessingEnvironment processingEnv) {
        FileObject fileObject = null;
        try {
            fileObject = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", filename);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Kind.NOTE, "File was not found: " + filename);
            return null;
        }
        
        return fileObject;
    }
    
    /**
     * INTERNAL:
     */
    public List<PersistenceUnit> getPersistenceUnits() {
        return persistenceUnits;
    }
    
    /**
     * INTERNAL:
     */
    protected void initPersistenceUnits(MetadataMirrorFactory factory) {
        ProcessingEnvironment processingEnv = factory.getProcessingEnvironment();
        String filename = CanonicalModelProperties.getOption(CANONICAL_MODEL_PERSISTENCE_XML_FILE, CANONICAL_MODEL_PERSISTENCE_XML_FILE_DEFAULT, processingEnv.getOptions());        
        FileObject fileObject = getFileObject(filename, processingEnv);
        
        if (fileObject != null) {
            try {
                InputStream inStream = null;
                
                try {
                    inStream = fileObject.openInputStream();
                    XMLContext context = PersistenceXMLMappings.createXMLContext();
                    PersistenceXML persistenceXML = (PersistenceXML) context.createUnmarshaller().unmarshal(inStream);
    
                    if (! persistenceXML.getVersion().equals("1.0")) {
                        for (SEPersistenceUnitInfo puInfo : persistenceXML.getPersistenceUnitInfos()) {
                            persistenceUnits.add(new PersistenceUnit(puInfo, factory, this));
                        }
                    }
                } finally {
                    if (inStream != null) {
                        inStream.close();
                    }
                }
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Kind.NOTE, "Unable to load persistence xml: " + fileObject.getName());
            } 
        } else {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Unable to load persistence xml.");
        }
    }
}

