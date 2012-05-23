/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.exceptions.XMLParseException;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.PersistenceContentHandler;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.XMLException;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.XMLExceptionHandler;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.jpa.Archive;
import org.eclipse.persistence.jpa.ArchiveFactory;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_EMBEDDABLE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ENTITY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_STATIC_METAMODEL;

/**
 * INTERNAL:
 * Utility Class that deals with persistence archives for EJB 3.0
 * Provides functions like searching for persistence archives, processing 
 * persistence.xml and searching for Entities in a Persistence archive
 */
public class PersistenceUnitProcessor {    
    /**
     * Passed to processORMetadata method to indicate processing mode.
     * ALL used when independent persistence unit is created.
     * COMPOSITE_MEMBER_INITIAL, COMPOSITE_MEMBER_MIDDLE and COMPOSITE_MEMBER_FINAL used for a composite member persistence unit.
     */
    public enum Mode {
        ALL,
        COMPOSITE_MEMBER_INITIAL,
        COMPOSITE_MEMBER_MIDDLE,
        COMPOSITE_MEMBER_FINAL
    }
    
    /**
     * Cache the ArchiveFactory used to derive Archives.  This allows applications
     * to set the ArchiveFactory
     */
    public static ArchiveFactory ARCHIVE_FACTORY = null;
    /**
     * Entries in a zip file are directory entries using slashes to separate 
     * them. Build a class name using '.' instead of slash and removing the 
     * '.class' extension.
     */
    public static String buildClassNameFromEntryString(String classEntryString){
        String classNameForLoader = classEntryString;
        if (classEntryString.endsWith(".class")){
            classNameForLoader = classNameForLoader.substring(0, classNameForLoader.length() - 6);
            classNameForLoader = classNameForLoader.replace("/", ".");              
        }
        return classNameForLoader;
    }
    
    /**
     * Build a set that contains all the class names at a URL.
     * @return a Set of class name strings
     */
    public static Set<String> buildClassSet(PersistenceUnitInfo persistenceUnitInfo, Map properties){
        Set<String> set = new HashSet<String>();
        set.addAll(persistenceUnitInfo.getManagedClassNames());
        ClassLoader loader = persistenceUnitInfo.getClassLoader();
        Iterator i = persistenceUnitInfo.getJarFileUrls().iterator();
        while (i.hasNext()) {
            set.addAll(getClassNamesFromURL((URL)i.next(), loader, properties));
        }
        if (!persistenceUnitInfo.excludeUnlistedClasses()){
            set.addAll(getClassNamesFromURL(persistenceUnitInfo.getPersistenceUnitRootUrl(), loader, properties));
        }
        // No longer need to add classes from XML, as temp class loader is only used for sessions.xml.
        return set;
    }
    
    /**
     * Create a list of the entities that will be deployed. This list is built 
     * from the information provided in the PersistenceUnitInfo argument.
     * The list contains Classes specified in the PersistenceUnitInfo's class 
     * list and also files that are annotated with @Entity and @Embeddable in
     * the jar files provided in the persistence info. This list of classes will 
     * used to build a deployment project and to decide what classes 
     * to weave.
     */
    public static Collection<MetadataClass> buildEntityList(MetadataProcessor processor, ClassLoader loader) {
        ArrayList<MetadataClass> entityList = new ArrayList<MetadataClass>();
        for (String className : processor.getProject().getWeavableClassNames()) {
            entityList.add(processor.getMetadataFactory().getMetadataClass(className));
        }        
        return entityList;
    }
    
    /**
     * Determine the URL path to the persistence unit 
     * @param pxmlURL - Encoded URL containing the pu
     * @return
     * @throws IOException
     */
    public static URL computePURootURL(URL pxmlURL, String descriptorLocation) throws IOException, URISyntaxException {
        StringTokenizer tokenizer = new StringTokenizer(descriptorLocation, "/\\");
        int descriptorDepth = tokenizer.countTokens() - 1;
        URL result;
        String protocol = pxmlURL.getProtocol();
        if("file".equals(protocol)) { // NOI18N
            StringBuffer path = new StringBuffer();
            boolean firstElement = true;
            for (int i=0;i<descriptorDepth;i++){
                if (!firstElement){
                    path.append("/"); // 315097 URL use standard separators
                }
                path.append("..");
                firstElement = false;
            }
            // e.g. file:/tmp/META-INF/persistence.xml
            // 210280: any file url will be assumed to always reference a file (not a directory)
            result = new URL(pxmlURL, path.toString()); // NOI18N
        } else if("jar".equals(protocol)) { // NOI18N
            // e.g. jar:file:/tmp/a_ear/b.jar!/META-INF/persistence.xml
            JarURLConnection conn =
                    JarURLConnection.class.cast(pxmlURL.openConnection());
            result = conn.getJarFileURL();
        } else if("zip".equals(protocol)) { // NOI18N
            // e.g. zip:/tmp/a_ear/b.jar!/META-INF/persistence.xml
            // stolen from java.net.JarURLConnection.parseSpecs method
            String spec = pxmlURL.getFile();
            int separator = spec.lastIndexOf("!/");
            if (separator == -1) {
                separator = spec.length() - 1;
            }
            result = new URL("file", "", spec.substring(0, separator++));
        } else if("wsjar".equals(protocol)) { // NOI18N
            // e.g. wsjar:file:/tmp/a_ear/b.jar!/META-INF/persistence.xml
            // but WS gives use jar:file:..., so we need to match it.
            String spec = pxmlURL.getFile();
            int separator = spec.lastIndexOf("!/");
            if (separator == -1) {
                separator = spec.length();
            } else {
                separator = separator + 2;
            }
            result = new URL("jar", "", spec.substring(0, separator));
        } else if ("bundleentry".equals(protocol)) {
            // mkeith - add bundle protocol cases
            result = new URL("bundleentry://" + pxmlURL.getAuthority());
        } else if ("bundleresource".equals(protocol)) {           
            result = new URL("bundleresource://" + pxmlURL.getAuthority());
        } else {
            StringBuffer path = new StringBuffer();
            for (int i=0;i<descriptorDepth;i++){
                path.append("../"); // 315097 URL use standard separators
            }
            // some other protocol
            result = new URL(pxmlURL, path.toString()); // NOI18N
        }
        result = fixUNC(result);
        return result;
    }


    /**
     * This method fixes incorrect authority attribute
     * that is set by JDK when UNC is used in classpath.
     * See JDK bug #6585937 and GlassFish issue #3209 for more details.
     */
    private static URL fixUNC(URL url) throws URISyntaxException, MalformedURLException, UnsupportedEncodingException
    {
        String protocol = url.getProtocol();
        if (!"file".equalsIgnoreCase(protocol)) {
            return url;
        }
        String authority= url.getAuthority();
        String file = url.getFile();
        if (authority != null) {
            AbstractSessionLog.getLog().finer(
                    "fixUNC: before fixing: url = " + url + ", authority = " + authority + ", file = " + file);
            assert(url.getPort() == -1);

            // See GlassFish issue https://glassfish.dev.java.net/issues/show_bug.cgi?id=3209 and
            // JDK issue http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6585937
            // When there is UNC path in classpath, the classloader.getResource
            // returns a file: URL with an authority component in it.
            // The URL looks like this:
            // file://ahost/afile.
            // Interestingly, authority and file components for the above URL
            // are either "ahost" and "/afile" or "" and "//ahost/afile" depending on
            // how the URL is obtained. If classpath is set as a jar with UNC,
            // the former is true, if the classpath is set as a directory with UNC,
            // the latter is true.
            String prefix = "";
            if (authority.length() > 0) {
                prefix = "////";
            } else if (file.startsWith("//")) {
                prefix = "//";
            }
            file = prefix.concat(authority).concat(file);
            url = new URL(protocol, null, file);
            AbstractSessionLog.getLog().finer(
                    "fixUNC: after fixing: url = " + url + ", authority = " + url.getAuthority() + ", file = " + url.getFile());
        }
        return url;
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
     * representing the root of those files. It is the caller's responsibility
     * to close all the archives.
     * 
     * @param loader the class loader to get the class path from
     */
    public static Set<Archive> findPersistenceArchives(ClassLoader loader){
        // allow alternate persistence location to be specified via system property.  This will allow persistence units
        // with alternate persistence xml locations to be weaved
        String descriptorLocation = System.getProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML_DEFAULT);
        return findPersistenceArchives(loader, descriptorLocation);
    }
    
    /**
     * Return a list of Archives representing the root of the persistence descriptor. 
     * It is the caller's responsibility to close all the archives.
     * 
     * @param loader the class loader to get the class path from
     */
    public static Set<Archive> findPersistenceArchives(ClassLoader loader, String descriptorPath){
        Archive archive = null;

        Set<Archive> archives = new HashSet<Archive>();

        // See if we are talking about an embedded descriptor
        int splitPosition = descriptorPath.indexOf("!/");

        try {
            // If not embedded descriptor then just use the regular descriptor path
            if (splitPosition == -1) {
                Enumeration<URL> resources = loader.getResources(descriptorPath);
                while (resources.hasMoreElements()){

                    URL descUrl = resources.nextElement();
                    URL puRootUrl = computePURootURL(descUrl, descriptorPath);
                    archive = PersistenceUnitProcessor.getArchiveFactory(loader).createArchive(puRootUrl, descriptorPath, null);
    
                   // archive = new BundleArchive(puRootUrl, descUrl);
                    if (archive != null){
                        archives.add(archive);
                    }
                }
            } else {
                // It is an embedded archive, so split up the parts
                String jarPrefixPath = descriptorPath.substring(0, splitPosition);
                String descPath = descriptorPath.substring(splitPosition+2);
                // TODO This causes the bundle to be resolved (not what we want)!
                URL prefixUrl = loader.getResource(jarPrefixPath);
                archive = PersistenceUnitProcessor.getArchiveFactory(loader).createArchive(prefixUrl, descPath, null);

                if (archive != null){
                    archives.add(archive);
                }
            } 
        } catch (Exception ex){
            //clean up first
            for (Archive a : archives){
                a.close();
            }
            throw PersistenceUnitLoadingException.exceptionSearchingForPersistenceResources(loader, ex);
        }
        return archives;
    }

    /**
     * Return a list of Archives representing the root of the persistence descriptor. 
     * It is the caller's responsibility to close all the archives.
     * 
     * @param loader the class loader to get the class path from
     */
    public static Set<Archive> findPersistenceArchives(ClassLoader loader, String descriptorPath, List<URL> jarFileUrls) {
        Archive archive = null;

        Set<Archive> archives = new HashSet<Archive>();

        // See if we are talking about an embedded descriptor
        // If not embedded descriptor then just use the regular descriptor path
        int splitPosition = descriptorPath.indexOf("!/");
        if (splitPosition != -1) {
            // It is an embedded archive, so split up the parts
            descriptorPath = descriptorPath.substring(splitPosition+2);
        }

        try {            
            for(int i=0; i < jarFileUrls.size(); i++) {
                URL puRootUrl = jarFileUrls.get(i);
                archive = PersistenceUnitProcessor.getArchiveFactory(loader).createArchive(puRootUrl, descriptorPath, null);

                // archive = new BundleArchive(puRootUrl, descUrl);
                if (archive != null){
                    archives.add(archive);
                }
            } 
        } catch (Exception ex){
            //clean up first
            for (Archive a : archives){
                a.close();
            }
            throw PersistenceUnitLoadingException.exceptionSearchingForPersistenceResources(loader, ex);
        }
        return archives;
    }

    public static Set<SEPersistenceUnitInfo> getPersistenceUnits(ClassLoader loader, Map m, List<URL> jarFileUrls) {
        String descriptorPath = (String) m.get(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML);
        if(descriptorPath == null) {
            descriptorPath = System.getProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML_DEFAULT);
        }
        Set<Archive> archives = findPersistenceArchives(loader, descriptorPath, jarFileUrls);
        Set<SEPersistenceUnitInfo> puInfos = new HashSet();
        try {
            for(Archive archive : archives) {
                List<SEPersistenceUnitInfo> puInfosFromArchive = getPersistenceUnits(archive, loader);
                puInfos.addAll(puInfosFromArchive);
            }
        } finally {
            for(Archive archive : archives) {
                archive.close();
            }
        }
        return puInfos;
    }
    
    public static ArchiveFactory getArchiveFactory(ClassLoader loader){
        if (ARCHIVE_FACTORY != null){
            return ARCHIVE_FACTORY;
        }
        
        ArchiveFactory factory = null;
        String factoryClassName = System.getProperty(SystemProperties.ARCHIVE_FACTORY, null);
        
        if (factoryClassName == null) {
            return new ArchiveFactoryImpl();
        } else {
            try {
                if (loader != null) {
                    Class archiveClass = loader.loadClass(factoryClassName);
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                        try {
                            factory = (ArchiveFactory)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(archiveClass));
                        } catch (PrivilegedActionException exception) {
                            throw PersistenceUnitLoadingException.exceptionCreatingArchiveFactory(factoryClassName, exception);
                        }
                    } else {
                        factory = (ArchiveFactory)PrivilegedAccessHelper.newInstanceFromClass(archiveClass);
                    }
                }
            } catch (ClassNotFoundException cnfe) {
                throw PersistenceUnitLoadingException.exceptionCreatingArchiveFactory(factoryClassName, cnfe);
            } catch (IllegalAccessException iae) {
                throw PersistenceUnitLoadingException.exceptionCreatingArchiveFactory(factoryClassName, iae);
            } catch (InstantiationException ie) {
                throw PersistenceUnitLoadingException.exceptionCreatingArchiveFactory(factoryClassName, ie);
            }
        }
        
        return factory;
    }
    
    public static Set<String> getClassNamesFromURL(URL url, ClassLoader loader, Map properties) {
        Set<String> classNames = new HashSet<String>();
        Archive archive = null;
        try {
            archive = PersistenceUnitProcessor.getArchiveFactory(loader).createArchive(url, properties);

            if (archive != null) {
                for (Iterator<String> entries = archive.getEntries(); entries.hasNext();) {
                    String entry = entries.next();
                    if (entry.endsWith(".class")){ // NOI18N
                        classNames.add(buildClassNameFromEntryString(entry));
                    }
                }
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("url = [" + url + "]", e);  // NOI18N
        } catch (IOException e) {
            throw new RuntimeException("url = [" + url + "]", e);  // NOI18N
        } finally {
            if (archive != null) {
                archive.close();
            }
        }
        return classNames;
    }
    
    /**
     * Return if a given class is annotated with @Embeddable.
     */
    public static MetadataAnnotation getEmbeddableAnnotation(MetadataClass candidateClass){
        return candidateClass.getAnnotation(JPA_EMBEDDABLE);
    }
    
    /**
     * Return if a given class is annotated with @Entity.
     */
    public static MetadataAnnotation getEntityAnnotation(MetadataClass candidateClass){
        return candidateClass.getAnnotation(JPA_ENTITY);
    }
    
    /**
     * Get a list of persistence units from the file or directory at the given 
     * url. PersistenceUnits are built based on the presence of a persistence descriptor
     * *
     * @param archive The url of a jar file or directory to check
     */
    public static List<SEPersistenceUnitInfo> getPersistenceUnits(Archive archive, ClassLoader loader){
        return processPersistenceArchive(archive, loader);
    }
    
    /**
     * Return the @StaticMetamodel annotation on the given class.
     */
    public static MetadataAnnotation getStaticMetamodelAnnotation(MetadataClass candidateClass){
        return candidateClass.getAnnotation(JPA_STATIC_METAMODEL);
    }
    
    /**
     * Return if a given class is annotated with @Embeddable.
     */
    public static boolean isEmbeddable(MetadataClass candidateClass) {
        return candidateClass.isAnnotationPresent(JPA_EMBEDDABLE);
    }
    
    /**
     * Return if a given class is annotated with @Entity.
     */
    public static boolean isEntity(MetadataClass candidateClass){
        return candidateClass.isAnnotationPresent(JPA_ENTITY);
    }
    
    /**
     * Return if a given class is annotated with @StaticMetamodel.
     */
    public static boolean isStaticMetamodelClass(MetadataClass candidateClass) {
        return candidateClass.isAnnotationPresent(JPA_STATIC_METAMODEL);
    }
    
    /**
     * Load the given class name with the given class loader.
     */
    public static Class loadClass(String className, ClassLoader loader, boolean throwExceptionIfNotFound, MetadataProject project) {
        Class candidateClass = null;
        
        try {
            candidateClass = loader.loadClass(className);
        } catch (ClassNotFoundException exc){
            if (throwExceptionIfNotFound){
                throw PersistenceUnitLoadingException.exceptionLoadingClassWhileLookingForAnnotations(className, exc);
            } else {
                AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, "persistence_unit_processor_error_loading_class", exc.getClass().getName(), exc.getLocalizedMessage() , className);
            }
        } catch (NullPointerException npe) {
            // Bug 227630: If any weavable class is not found in the temporary 
            // classLoader - disable weaving 
            AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, AbstractSessionLog.WEAVER, "persistence_unit_processor_error_loading_class_weaving_disabled", loader, project.getPersistenceUnitInfo().getPersistenceUnitName(), className);
            // Disable weaving (for 1->1 and many->1)only if the classLoader 
            // returns a NPE on loadClass()
            project.disableWeaving();
        } catch (Exception exception){
            AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, AbstractSessionLog.WEAVER, "persistence_unit_processor_error_loading_class", exception.getClass().getName(), exception.getLocalizedMessage() , className);
        } catch (Error error){
            AbstractSessionLog.getLog().log(AbstractSessionLog.WARNING, AbstractSessionLog.WEAVER, "persistence_unit_processor_error_loading_class", error.getClass().getName(), error.getLocalizedMessage() , className);
            throw error;
        }
        
        return candidateClass;
    }

    /**
     * Process the Object/relational metadata from XML and annotations
     */
    public static void processORMetadata(MetadataProcessor processor, boolean throwExceptionOnFail, Mode mode) {
        if (mode == Mode.ALL || mode == Mode.COMPOSITE_MEMBER_INITIAL) {
            // DO NOT CHANGE the order of invocation of various methods.
    
            // 1 - Load the list of mapping files for the persistence unit. Need to 
            // do this before we start processing entities as the list of entity 
            // classes depend on metadata read from mapping files.
            processor.loadMappingFiles(throwExceptionOnFail);
        }
    
        // 2 - Process each XML entity mappings file metadata (except for
        // the actual classes themselves). This method is also responsible
        // for handling any XML merging.
        processor.processEntityMappings(mode);

        // 3 - Process the persistence unit classes (from XML and annotations)
        // and their metadata now.
        processor.processORMMetadata(mode);        
    }

    /**
     * Go through the jar file for this PersistenceUnitProcessor and process any 
     * XML provided in it.
     */
    public static List<SEPersistenceUnitInfo> processPersistenceArchive(Archive archive, ClassLoader loader){
        URL puRootURL = archive.getRootURL();
        try {
            return processPersistenceXML(puRootURL, archive.getDescriptorStream(), loader);
          } catch (Exception e) {
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
        
        XMLReader xmlReader = null;
        SAXParser sp = null;
        XMLExceptionHandler xmlErrorHandler = new XMLExceptionHandler();
        // 247735 - remove the validation of XML.  

        // create a SAX parser
        try {
            sp = spf.newSAXParser();
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
            info.setClassLoader(loader);
            info.setNewTempClassLoader(loader);
        }
        return myContentHandler.getPersistenceUnits();
    }
    
    public static void setArchiveFactory(ArchiveFactory factory){
        ARCHIVE_FACTORY = factory;
    }
    
    /**
     * Build the unique persistence name by concatenating the decoded URL with the persistence unit name.
     * A decoded URL is required while persisting on a multi-bytes OS.  
     * @param URL
     * @param puName
     * @return String
     */
   public static String buildPersistenceUnitName(URL url, String puName){
       String fullPuName = null;
       try {
           // append the persistence unit name to the decoded URL
           fullPuName = URLDecoder.decode(url.toString(), "UTF8")+"_"+puName;
       } catch (UnsupportedEncodingException e) {
           throw PersistenceUnitLoadingException.couldNotBuildPersistenceUntiName(e,url.toString(),puName);
       }
       return fullPuName;
   }
   
}
