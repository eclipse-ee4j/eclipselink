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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment;

import java.net.URL;
import java.net.URISyntaxException;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.exceptions.XMLParseException;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.PersistenceContentHandler;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.XMLException;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.XMLExceptionHandler;
import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * INTERNAL:
 * Utility Class that deals with persistence archives for EJB 3.0
 * Provides functions like searching for persistence archives, processing 
 * persistence.xml and searching for Entities in a Persistence archive
 */
public class PersistenceUnitProcessor  {
    /**
     * Entries in a zip file are directory entries using slashes to separate 
     * them. Build a class name using '.' instead of slash and removing the 
     * '.class' extension.
     */
    public static String buildClassNameFromEntryString(String classEntryString){
        String classNameForLoader = classEntryString;
        if (classEntryString.endsWith(".class")){
            classNameForLoader = classNameForLoader.substring(0, classNameForLoader.length() - 6);;
            classNameForLoader = classNameForLoader.replace("/", ".");              
        }
        return classNameForLoader;
    }
    
    /**
     * Build a set that contains all the class names at a URL.
     * @return a Set of class name strings
     */
    public static Set<String> buildClassSet(PersistenceUnitInfo persistenceUnitInfo, ClassLoader loader){
        Set<String> set = new HashSet<String>();
        set.addAll(persistenceUnitInfo.getManagedClassNames());
        Iterator i = persistenceUnitInfo.getJarFileUrls().iterator();
        while (i.hasNext()) {
            set.addAll(getClassNamesFromURL((URL)i.next()));
        }
        if (!persistenceUnitInfo.excludeUnlistedClasses()){
            set.addAll(getClassNamesFromURL(persistenceUnitInfo.getPersistenceUnitRootUrl()));
        }
        set.addAll(buildPersistentClassSetFromXMLDocuments(persistenceUnitInfo, loader));        
        return set;
    }
    
    /**
     * Create a list of the entities that will be deployed. This list is built 
     * from the information provided in the PersistenceUnitInfo argument.
     * The list contains Classes specified in the PersistenceUnitInfo's class 
     * list and also files that are annotated with @Entity and @Embeddable in
     * the jar files provided in the persistence info. This list of classes will 
     * used by TopLink to build a deployment project and to decide what classes 
     * to weave.
     */
    public static Collection<Class> buildEntityList(MetadataProject project, ClassLoader loader) {
        ArrayList<Class> entityList = new ArrayList<Class>();
        for (String className : project.getWeavableClassNames()) {
            try {
                Class entityClass = loader.loadClass(className);
                entityList.add(entityClass);
            } catch (ClassNotFoundException exc) {
                AbstractSessionLog.getLog().log(SessionLog.CONFIG, "exception_loading_entity_class", className, exc);
            }
        }
        
        return entityList;
    }

    /**
     * Return a Set<String> of the classnames represented in the mapping files 
     * specified in info.
     */
    private static Set<String> buildPersistentClassSetFromXMLDocuments(PersistenceUnitInfo info, ClassLoader loader){
        // Build a MetadataProcessor to search the mapped classes in orm xml 
        // documents. We hand in a null session since none of the functionality 
        // required uses a session. (At least we hope not)
        MetadataProcessor processor = new MetadataProcessor(info, null, loader, false);
        // Read the mapping files.
        processor.readMappingFiles(false);
        // Return the class set.
        return processor.getPersistenceUnitClassSetFromMappingFiles();
    }

    public static URL computePURootURL(URL pxmlURL) throws IOException {
        String protocol = pxmlURL.getProtocol();
        if("file".equals(protocol)) { // NOI18N
            // e.g. file:/tmp/META-INF/persistence.xml
            assert(new File(pxmlURL.getFile()).isFile());
            return new URL(pxmlURL, ".."); // NOI18N
        } else if("jar".equals(protocol)) { // NOI18N
            // e.g. jar:file:/tmp/a_ear/b.jar!/META-INF/persistence.xml
            JarURLConnection conn =
                    JarURLConnection.class.cast(pxmlURL.openConnection());
            assert(conn.getJarEntry().getName().equals(
                    "META-INF/persistence.xml")); // NOI18N
            return conn.getJarFileURL();
        } else {
            // some other protocol,
            // e.g. bundleresource://21/META-INF/persistence.xml
            return new URL(pxmlURL, "../"); // NOI18N
        }
    }

    /**
     * Search the classpath for persistence archives. A persistence archive is 
     * defined as any part of the class path that contains a META-INF directory 
     * with a persistence.xml file in it. Return a list of the URLs of those 
     * files. Use the current thread's context classloader to get the classpath. 
     * We assume it is a URL class loader.
     */
    public static Set<Archive> findPersistenceArchives(){
        ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();
        return findPersistenceArchives(threadLoader);
    }

    /**
     * Search the classpath for persistence archives. A persistence archive is 
     * defined as any part of the class path that contains a META-INF directory 
     * with a persistence.xml file in it. Return a list of {@link Archive} 
     * representing the root of those files.
     * @param loader the class loader to get the class path from
     */
    public static Set<Archive> findPersistenceArchives(ClassLoader loader){
        Set<Archive> pars = new HashSet<Archive>();
        try {
            Enumeration<URL> resources = loader.getResources("META-INF/persistence.xml");
            while (resources.hasMoreElements()){
                URL pxmlURL = resources.nextElement();
                URL puRootURL = computePURootURL(pxmlURL);
                Archive archive = new ArchiveFactoryImpl().createArchive(puRootURL);
                pars.add(archive);
            }
        } catch (java.io.IOException exc){
            throw PersistenceUnitLoadingException.exceptionSearchingForPersistenceResources(loader, exc);
        } catch (URISyntaxException exc) {
            throw PersistenceUnitLoadingException.exceptionSearchingForPersistenceResources(loader, exc);
        }
        
        return pars;
    }

    public static Set<String> getClassNamesFromURL(URL url) {
        Set<String> classNames = new HashSet<String>();
        Archive archive = null;
        try {
            archive = new ArchiveFactoryImpl().createArchive(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException("url = [" + url + "]", e);  // NOI18N
        } catch (IOException e) {
            throw new RuntimeException("url = [" + url + "]", e);  // NOI18N
        }
        
        for (Iterator<String> entries = archive.getEntries(); entries.hasNext();) {
            String entry = entries.next();
            if (entry.endsWith(".class")){ // NOI18N
                 classNames.add(buildClassNameFromEntryString(entry));
            }
        }
        return classNames;
    }
    
    /**
     * Get a list of persitence units from the file or directory at the given 
     * url. PersistenceUnits are built based on the presence of persistence.xml 
     * in a META-INF directory at the base of the URL.
     * @param archive The url of a jar file or directory to check
     */
    public static List<SEPersistenceUnitInfo> getPersistenceUnits(Archive archive, ClassLoader loader){
        return processPersistenceArchive(archive, loader);
    }
    
    /**
     * Return if a given class is annotated with @Embeddable.
     */
    public static boolean isEmbeddable(Class candidateClass){
        return candidateClass.isAnnotationPresent(javax.persistence.Embeddable.class);
    }
    
    /**
     * Return if a given class is annotated with @Entity.
     */
    public static boolean isEntity(Class candidateClass){
        return candidateClass.isAnnotationPresent(javax.persistence.Entity.class);
    }
    
    /**
     * Load the given class name with the given class loader.
     */
    public static Class loadClass(String className, ClassLoader loader, boolean throwExceptionIfNotFound) {
        Class candidateClass = null;
        
        try {
            candidateClass = loader.loadClass(className);
        } catch (ClassNotFoundException exc){
            if (throwExceptionIfNotFound){
                throw PersistenceUnitLoadingException.exceptionLoadingClassWhileLookingForAnnotations(className, exc);
            } else {
                AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, "persistence_unit_processor_error_loading_class", exc.getClass().getName(), exc.getLocalizedMessage() , className);
            }
        } catch (Exception exception){
            AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, "persistence_unit_processor_error_loading_class", exception.getClass().getName(), exception.getLocalizedMessage() , className);
        }
        
        return candidateClass;
    }

    /**
     * Process the Object/relational metadata from XML and annotations
     */
    public static void processORMetadata(MetadataProcessor processor, boolean throwExceptionOnFail) {
        // DO NOT CHANGE the order of invocation of various methods.

        // Build the list of mapping files and read them. Need to do this 
    	// before we start processing entities as the list of entity classes 
    	// depend on metadata read from mapping files.
        processor.readMappingFiles(throwExceptionOnFail);

        // Process each XML entity mappings file.
        processor.processEntityMappings();

        // Process the actual PU classes metadata from annotations.
        // Process the remaing persistence unit classes not defined in those 
        // classes not defined in XML.
        processor.processAnnotations();        
    }

    /**
     * Go through the jar file for this PeristeneUnitProcessor and process any 
     * XML provided in it.
     */
    public static List<SEPersistenceUnitInfo> processPersistenceArchive(Archive archive, ClassLoader loader){
        URL puRootURL = archive.getRootURL();
        try {
            InputStream pxmlStream = archive.getEntry("META-INF/persistence.xml"); // NOI18N
            return processPersistenceXML(puRootURL, pxmlStream, loader);
        } catch (IOException e) {
            throw PersistenceUnitLoadingException.exceptionLoadingFromUrl(puRootURL.toString(), e);
        }
    }

    /**
     * Build a persistence.xml file into a SEPersistenceUnitInfo object.
     * May eventually change this to use OX mapping as well.
     */
    private static List<SEPersistenceUnitInfo> processPersistenceXML(URL baseURL, InputStream input, ClassLoader loader){
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        spf.setValidating(true);
        
        XMLReader xmlReader = null;
        SAXParser sp = null;
        XMLExceptionHandler xmlErrorHandler = new XMLExceptionHandler();

        // create a SAX parser
        try {
            sp = spf.newSAXParser();
	        sp.setProperty(MetadataConstants.SCHEMA_LANGUAGE, MetadataConstants.XML_SCHEMA);
	    } catch (javax.xml.parsers.ParserConfigurationException exc){
	    	throw XMLParseException.exceptionCreatingSAXParser(baseURL, exc);
	    } catch (org.xml.sax.SAXException exc){
	    	throw XMLParseException.exceptionCreatingSAXParser(baseURL, exc);
	    }
	        
	    // create an XMLReader
	    try {
            xmlReader = sp.getXMLReader();
	        xmlReader.setErrorHandler(xmlErrorHandler);
        } catch (org.xml.sax.SAXException exc){
        	throw XMLParseException.exceptionCreatingXMLReader(baseURL, exc);
        }
       
        // attempt to load the schema from the classpath
        URL schemaURL = loader.getResource(MetadataConstants.PERSISTENCE_SCHEMA_NAME);
        if (schemaURL != null) {
            try {
            	sp.setProperty(MetadataConstants.JAXP_SCHEMA_SOURCE, schemaURL.toString());
            } catch (org.xml.sax.SAXException exc){
            	throw XMLParseException.exceptionSettingSchemaSource(baseURL, schemaURL, exc);
            }
        }

        PersistenceContentHandler myContentHandler = new PersistenceContentHandler();
        xmlReader.setContentHandler(myContentHandler);

        InputSource inputSource = new InputSource(input);
        try{
            xmlReader.parse(inputSource);
        } catch (IOException exc){
            throw PersistenceUnitLoadingException.exceptionProcessingPersistenceXML(baseURL, exc);
        } catch (org.xml.sax.SAXException exc){
        	// XMLErrorHandler will handle SAX exceptions
        }
        
        // handle any parse exceptions
        XMLException xmlError = xmlErrorHandler.getXMLException();
        if (xmlError != null) {
            throw PersistenceUnitLoadingException.exceptionProcessingPersistenceXML(baseURL, xmlError);
        }

        Iterator<SEPersistenceUnitInfo> persistenceInfos = myContentHandler.getPersistenceUnits().iterator();
        while (persistenceInfos.hasNext()){
            SEPersistenceUnitInfo info = persistenceInfos.next();
            info.setPersistenceUnitRootUrl(baseURL);           
        }
        return myContentHandler.getPersistenceUnits();
    }
    
    /**
     * Build the unique persistence name by concatenating the decoded URL wiht persistence unit name.
     * Decoding url is required while persistence on multiple-bytes OS.  
     * @param URL
     * @param puName
     * @return String
     */
   public static String buildPersistenceUnitName(URL url, String puName){
       String fullPuName = null;
       try {
           fullPuName = URLDecoder.decode(url.toString(), "UTF8")+puName;
       } catch (UnsupportedEncodingException e) {
           throw PersistenceUnitLoadingException.couldNotBuildPersistenceUntiName(e,url.toString(),puName);
       }
       return fullPuName;
   }

}
