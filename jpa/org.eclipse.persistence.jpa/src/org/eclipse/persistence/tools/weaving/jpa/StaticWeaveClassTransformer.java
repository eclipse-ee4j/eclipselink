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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.tools.weaving.jpa;

import java.io.IOException;
import java.io.Writer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipException;

import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.ArchiveFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.exceptions.StaticWeaveException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.helper.JPAConversionManager;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.internal.jpa.weaving.TransformerFactory;
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
    private Writer logWriter;
    private int logLevel = SessionLog.OFF;    
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
        this.logWriter=logWriter;
        this.logLevel=loglevel;
        buildClassTransformers(inputArchiveURL, persistenceXMLLocation ,aclassloader);
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
            Iterator<SEPersistenceUnitInfo> persistenceUnitsIterator = persistenceUnitsList.iterator();
            while (persistenceUnitsIterator.hasNext()) {
                SEPersistenceUnitInfo unitInfo = persistenceUnitsIterator.next();
                unitInfo.setNewTempClassLoader(aclassloader);
                //build class transformer.
                ClassTransformer transformer = buildTransformer(unitInfo,this.logWriter,this.logLevel);
                classTransformers.add(transformer);
            }
        } catch (ZipException e) {
            throw StaticWeaveException.exceptionOpeningArchive(inputArchiveURL,e);
        } finally {
            if (archive != null) {
                archive.close();
            }
        }
    }
    
    /**
     * This method builds the classtransformer for the specified persistence unit.
     */        
    private ClassTransformer buildTransformer(PersistenceUnitInfo unitInfo, Writer logWriter, int logLevel) {
        //persistenceUnitInfo = unitInfo;
        ClassLoader privateClassLoader = unitInfo.getNewTempClassLoader();

        // create server session (it should be done before initializing ServerPlatform)
        ServerSession session = new ServerSession(new Project(new DatabaseLogin()));
        session.setLogLevel(logLevel);
        if(logWriter!=null){
            ((DefaultSessionLog)session.getSessionLog()).setWriter(logWriter);
         }
        
        session.getPlatform().setConversionManager(new JPAConversionManager());

        boolean weaveEager = false;
        String weaveEagerString = (String)unitInfo.getProperties().get(PersistenceUnitProperties.WEAVING_EAGER);
        if (weaveEagerString != null && weaveEagerString.equalsIgnoreCase("true")) {
            weaveEager = true;
        }
        
        // Create an instance of MetadataProcessor for specified persistence unit info
        MetadataProcessor processor = new MetadataProcessor(unitInfo, session, privateClassLoader, true, weaveEager, null);
        
        //bug:299926 - Case insensitive table / column matching with native SQL queries
        EntityManagerSetupImpl.updateCaseSensitivitySettings(unitInfo.getProperties(), processor.getProject(), session);
        // Process the Object/relational metadata from XML and annotations.
        PersistenceUnitProcessor.processORMetadata(processor, false);

        //Collection entities = buildEntityList(persistenceUnitInfo, privateClassLoader);
        Collection entities = PersistenceUnitProcessor.buildEntityList(processor, privateClassLoader);

        boolean weaveLazy = true;
        String weaveL = (String)unitInfo.getProperties().get(PersistenceUnitProperties.WEAVING_LAZY);
        if (weaveL != null && weaveL.equalsIgnoreCase("false")) {
            weaveLazy = false;
        }
        
        boolean weaveChangeTracking = true;
        String weaveCT = (String)unitInfo.getProperties().get(PersistenceUnitProperties.WEAVING_CHANGE_TRACKING);
        if (weaveCT != null && weaveCT.equalsIgnoreCase("false")) {
            weaveChangeTracking = false;
        }
        
        boolean weaveFetchGroups = true;
        String weaveFetchGroupsProperty = (String)unitInfo.getProperties().get(PersistenceUnitProperties.WEAVING_FETCHGROUPS);
        if (weaveFetchGroupsProperty != null && weaveFetchGroupsProperty.equalsIgnoreCase("false")) {
            weaveFetchGroups = false;
        }
        
        boolean weaveFetchInternal = true;
        String weaveInternalProperty = (String)unitInfo.getProperties().get(PersistenceUnitProperties.WEAVING_INTERNAL);
        if (weaveInternalProperty != null && weaveInternalProperty.equalsIgnoreCase("false")) {
            weaveFetchInternal = false;
        }

        // The transformer is capable of altering domain classes to handle a LAZY hint for OneToOne mappings.  It will only
        // be returned if we are meant to process these mappings
        return TransformerFactory.createTransformerAndModifyProject(session, entities, privateClassLoader, weaveLazy, weaveChangeTracking, weaveFetchGroups, weaveFetchInternal);
    }
}
