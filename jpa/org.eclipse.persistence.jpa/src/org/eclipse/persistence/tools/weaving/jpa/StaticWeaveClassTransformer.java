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
 *     04/01/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 2)
 ******************************************************************************/  
package org.eclipse.persistence.tools.weaving.jpa;

import java.io.IOException;
import java.io.Writer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

import javax.persistence.spi.ClassTransformer;

import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.StaticWeaveInfo;
import org.eclipse.persistence.internal.jpa.deployment.ArchiveFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.exceptions.StaticWeaveException;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.jpa.Archive;

/**
* <p>
* <b>Description</b>: This class provides the implementation of class transformer by leveraging on the following existing APIs,
* <ul>
* <li> PersistenceUnitProcessor.processORMetadata() - get class descriptor.
* <li> PersistenceUnitProcessor.buildEntityList() - get entity classes lsit.
* <li> TransformerFactory.createTransformerAndModifyProject - get class transformer.
* </ul>
* <p>
* <b>Responsibilities</b>:
* <ul>
* <li> Create the classtransformer for each persistence unit individually and store them into the list.
* <li> Provide class transfom method to perform weaving function.
* </ul>
*/

public class StaticWeaveClassTransformer {
    private ArrayList<ClassTransformer> classTransformers;
    StaticWeaveInfo info;
    private ClassLoader aClassLoader;
    
    /**
     * Constructs an instance of StaticWeaveClassTransformer.
     */
    public StaticWeaveClassTransformer(URL inputArchiveURL,ClassLoader aclassloader) throws Exception {
        this(inputArchiveURL,null, aclassloader,null,SessionLog.OFF);
    }
    
    /**
     * Constructs an instance of StaticWeaveClassTransformer.
     */
    public StaticWeaveClassTransformer(URL inputArchiveURL, String persistenceXMLLocation, ClassLoader aclassloader, Writer logWriter, int loglevel) throws URISyntaxException,IOException {
        this.aClassLoader = aclassloader;
        this.info = new StaticWeaveInfo(logWriter, loglevel);
        buildClassTransformers(inputArchiveURL, persistenceXMLLocation, aclassloader);
    }

    /**
     * The method performs weaving function on the given class.
     * @return the converted(woven) class
     */
    public byte[] transform(String originalClassName, Class originalClass, byte[] originalClassBytes)throws IllegalClassFormatException{
        byte[] newClassBytes = null;
        for(ClassTransformer transformer : classTransformers){
            newClassBytes=transformer.transform(aClassLoader, originalClassName, originalClass, null, originalClassBytes);
            if(newClassBytes!=null) {
                break;
            };
        }
        return newClassBytes;
    }

    /**
     * The method creates classtransformer list corresponding to each persistence unit. 
     */
    private void buildClassTransformers(URL inputArchiveURL, String persistenceXMLLocation, ClassLoader aclassloader) throws URISyntaxException,IOException{ 
        if (classTransformers!=null) {
            return ;
        } else {
            classTransformers = new ArrayList<ClassTransformer>();
        }
        Archive archive = null;
        try {
            
            archive = (new ArchiveFactoryImpl()).createArchive(inputArchiveURL, persistenceXMLLocation == null ? PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML_DEFAULT : persistenceXMLLocation, null);
            
            List<SEPersistenceUnitInfo> persistenceUnitsList = 
            PersistenceUnitProcessor.processPersistenceArchive(archive, aclassloader);
            if (persistenceUnitsList==null) {
                throw PersistenceUnitLoadingException.couldNotGetUnitInfoFromUrl(inputArchiveURL);
            }
            Map emptyMap = new HashMap(0);
            Iterator<SEPersistenceUnitInfo> persistenceUnitsIterator = persistenceUnitsList.iterator();
            while (persistenceUnitsIterator.hasNext()) {
                SEPersistenceUnitInfo unitInfo = persistenceUnitsIterator.next();
                //build class transformer.
                String puName = unitInfo.getPersistenceUnitName();
                String sessionName = (String)unitInfo.getProperties().get(PersistenceUnitProperties.SESSION_NAME);
                if (sessionName == null) {
                    sessionName = puName;
                }
                EntityManagerSetupImpl emSetupImpl = new EntityManagerSetupImpl(puName, sessionName);
                //indicates that predeploy is used for static weaving, also passes logging parameters
                emSetupImpl.setStaticWeaveInfo(this.info);
                ClassTransformer transformer = emSetupImpl.predeploy(unitInfo, emptyMap);
                if (transformer != null) {
                    classTransformers.add(transformer);
                }
            }
        } catch (ZipException e) {
            throw StaticWeaveException.exceptionOpeningArchive(inputArchiveURL,e);
        } finally {
            if (archive != null) {
                archive.close();
            }
        }
    }    
}
