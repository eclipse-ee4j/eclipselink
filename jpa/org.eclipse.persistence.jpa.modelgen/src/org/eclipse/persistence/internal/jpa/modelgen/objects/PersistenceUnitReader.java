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
 *     08/10/2009-2.0 Guy Pelletier 
 *       - 267391: JPA 2.0 implement/extend/use an APT tooling library for MetaModel API canonical classes
 *     11/20/2009-2.0 Guy Pelletier/Mitesh Meswani 
 *       - 295376: Improve usability of MetaModel generator
 *     04/29/2010-2.0.3 Guy Pelletier 
 *       - 311020: Canonical model generator should not throw an exception when no persistence.xml is found
 *     06/01/2010-2.1 Guy Pelletier 
 *       - 315195: Add new property to avoid reading XML during the canonical model generation
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.modelgen.objects;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnit;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXML;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXMLMappings;
import org.eclipse.persistence.oxm.XMLContext;

import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML;
import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML_DEFAULT;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_LOAD_XML;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_LOAD_XML_DEFAULT;

/**
 * Used to read persistence units through the java annotation processing API. 
 * 
 * @author Guy Pelletier, Peter Krogh
 * @since EclipseLink 1.2
 */
public class PersistenceUnitReader {
    protected MetadataMirrorFactory factory;
    protected ProcessingEnvironment processingEnv;
    
    /**
     * INTERNAL:
     */
    public PersistenceUnitReader(MetadataMirrorFactory factory) throws IOException {
        this.factory = factory;
        processingEnv = factory.getProcessingEnvironment();
        
        // As a performance enhancement to avoid reloading and merging XML
        // metadata for every compile round, the user may choose to turn off
        // the XML loading by setting the load XML flag to false. 
        if (Boolean.valueOf(CanonicalModelProperties.getOption(CANONICAL_MODEL_LOAD_XML, CANONICAL_MODEL_LOAD_XML_DEFAULT, processingEnv.getOptions()))) {
            // after initializing our member variables, initialize the pu's.
            initPersistenceUnits();
        }
    }
    
    /**
     * INTERAL:
     * Close the given input stream.
     */
    protected void closeInputStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected FileObject getFileObject(String filename, ProcessingEnvironment processingEnv) throws IOException {
        return processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", filename);
    }
    
    /**
     * INTERNAL:
     * Return an input stream for the given filename.
     */
    protected InputStream getInputStream(String filename, boolean loadingPersistenceXML) {
        InputStream inputStream = null;
        
        try {
            FileObject fileObject = getFileObject(filename, processingEnv);
            inputStream = fileObject.openInputStream();
        } catch (IOException ioe) {
            // If we can't find the persistence.xml from the class output
            // we'll try from the current directory using regular IO.
            try {
                inputStream = new FileInputStream(filename);
            } catch (IOException e) {
                if (loadingPersistenceXML) {
                    // If loading the persistence.xml, log a BIG warning message.
                    processingEnv.getMessager().printMessage(Kind.NOTE, "The persistence xml file ["+filename+"] was not found. NO GENERATION will occur!! Please ensure a persistence xml file is available either from the CLASS_OUTPUT directory [META-INF/persistence.xml] or using the eclipselink.persistencexml property to specify its location. ");
                } else {
                    // For any other mapping file log a message.
                    processingEnv.getMessager().printMessage(Kind.NOTE, "File was not found: " + filename); 
                }
            }
        }   
        
        return inputStream;
    }
    
    /**
     * INTERNAL:
     * This method will look for an process the -A eclipselink.persistenceunits
     * option. This list is treated as an include/filter list and if it is not
     * specified all persistence units are processed.
     */
    protected HashSet<String> getPersistenceUnitList(ProcessingEnvironment processingEnv ) {
        String persistenceUnits = processingEnv.getOptions().get(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_UNITS);
        HashSet<String> persistenceUnitList = null;
        
        if (persistenceUnits != null) {
            persistenceUnitList = new HashSet<String>();
            StringTokenizer st = new StringTokenizer(persistenceUnits, ",");
        
            while (st.hasMoreTokens()) {
                persistenceUnitList.add(((String) st.nextToken()).trim());
            }
        }
        
        return persistenceUnitList;
    }
    
    /**
     * INTERNAL:
     */
    public Collection<PersistenceUnit> getPersistenceUnits() {
        return factory.getPersistenceUnits();
    }
    
    /**
     * INTERNAL:
     */
    protected void initPersistenceUnits() {
        for (String optionKey : processingEnv.getOptions().keySet()) {
            processingEnv.getMessager().printMessage(Kind.OTHER, "Found Option : " + optionKey + ", with value: " + processingEnv.getOptions().get(optionKey));
        }
        
        String filename = CanonicalModelProperties.getOption(ECLIPSELINK_PERSISTENCE_XML, ECLIPSELINK_PERSISTENCE_XML_DEFAULT, processingEnv.getOptions());
        HashSet<String> persistenceUnitList = getPersistenceUnitList(processingEnv);

        InputStream inStream = null;

        try {
            XMLContext context = PersistenceXMLMappings.createXMLContext();
            inStream = getInputStream(filename, true);
            
            // If the persistence.xml was not found, then there is nothing to do.
            if (inStream != null) {
                PersistenceXML persistenceXML = (PersistenceXML) context.createUnmarshaller().unmarshal(inStream);

                for (SEPersistenceUnitInfo puInfo : persistenceXML.getPersistenceUnitInfos()) {
                    // If no persistence unit list has been specified or one
                    // has been specified and this persistence unit info's name
                    // appears in that list then add it.
                    if (persistenceUnitList == null || persistenceUnitList.contains(puInfo.getPersistenceUnitName())) {
                        factory.addPersistenceUnit(puInfo, new PersistenceUnit(puInfo, factory, this));
                    }
                }
            }
        } finally {
            closeInputStream(inStream);
        }
    }
}

