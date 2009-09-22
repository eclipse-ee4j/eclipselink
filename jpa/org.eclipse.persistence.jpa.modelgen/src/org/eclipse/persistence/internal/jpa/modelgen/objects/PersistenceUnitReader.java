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
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnit;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXML;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXMLMappings;
import org.eclipse.persistence.oxm.XMLContext;

import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML;
import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML_DEFAULT;

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
    public FileObject getFileObject(String filename, ProcessingEnvironment processingEnv) throws IOException {
        return processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", filename);
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
    public List<PersistenceUnit> getPersistenceUnits() {
        return persistenceUnits;
    }
    
    /**
     * INTERNAL:
     */
    protected void initPersistenceUnits(MetadataMirrorFactory factory) {
        ProcessingEnvironment processingEnv = factory.getProcessingEnvironment();
        
        for (String optionKey : processingEnv.getOptions().keySet()) {
            processingEnv.getMessager().printMessage(Kind.OTHER, "Found Option : " + optionKey + ", with value: " + processingEnv.getOptions().get(optionKey));            
        }
        
        String filename = CanonicalModelProperties.getOption(ECLIPSELINK_PERSISTENCE_XML, ECLIPSELINK_PERSISTENCE_XML_DEFAULT, processingEnv.getOptions());        
        HashSet<String> persistenceUnitList = getPersistenceUnitList(processingEnv);
        
        try {
            FileObject fileObject = getFileObject(filename, processingEnv);        
            InputStream inStream = null;
                
            try {
                inStream = fileObject.openInputStream();
                XMLContext context = PersistenceXMLMappings.createXMLContext();
                PersistenceXML persistenceXML = (PersistenceXML) context.createUnmarshaller().unmarshal(inStream);
    
                for (SEPersistenceUnitInfo puInfo : persistenceXML.getPersistenceUnitInfos()) {
                    // If no persistence unit list has been specified or one
                    // has been specified and this persistence unit info's name
                    // appears in that list then add it.
                    if (persistenceUnitList == null || persistenceUnitList.contains(puInfo.getPersistenceUnitName())) {
                        persistenceUnits.add(new PersistenceUnit(puInfo, factory, this));
                    }
                }
            } finally {
                if (inStream != null) {
                    inStream.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to load persistence.xml : " + e.getLocalizedMessage());
        } 
    }
}

