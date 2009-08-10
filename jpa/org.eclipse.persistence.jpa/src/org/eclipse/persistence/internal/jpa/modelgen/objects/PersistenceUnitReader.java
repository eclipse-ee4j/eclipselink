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
    public PersistenceUnitReader(MetadataMirrorFactory factory) throws IOException {
        m_persistenceUnits = new ArrayList<PersistenceUnit>();
        
        FileObject fileObject = getFileObject("META-INF/persistence.xml", factory.getProcessingEnvironment());

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
    public static FileObject getFileObject(String filename,ProcessingEnvironment processingEnv) throws IOException {
        FileObject fileObject = null;
        try {
            // Take location as a parameter -A
            fileObject = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", filename);
        } catch (IOException e) {
            // print a message ...
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

