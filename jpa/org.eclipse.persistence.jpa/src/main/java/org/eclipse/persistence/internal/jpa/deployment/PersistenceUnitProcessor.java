/*
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2024 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     07/08/2014-2.5 Jody Grassel (IBM Corporation)
//       - 439163: JSE Bootstrapping does not handle "wsjar" URLs referencing war-contained resources
//     08/29/2016 Jody Grassel
//       - 500441: Eclipselink core has System.getProperty() calls that are not potentially executed under doPriv()
//     11/23/2017: Scott Marlow
//       - 414974: allow eclipselink.archive.factory to be specified as an integration property of PersistenceProvider.createContainerEntityManagerFactory(PersistenceUnitInfo, Map)
//     09/07/2018-3.0 Dmitry Polienko
//       - 326728: Fix persistence root calculation for WAR files
package org.eclipse.persistence.internal.jpa.deployment;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_CONVERTER;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_EMBEDDABLE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ENTITY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_MAPPED_SUPERCLASS;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_STATIC_METAMODEL;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jakarta.persistence.spi.PersistenceUnitInfo;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.jpa.exceptions.PersistenceUnitLoadingException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.jpa.exceptions.XMLParseException;
import org.eclipse.persistence.internal.helper.XMLHelper;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.PersistenceContentHandler;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.XMLException;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.XMLExceptionHandler;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetSystemProperty;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.jpa.Archive;
import org.eclipse.persistence.jpa.ArchiveFactory;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

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

    /** Path to application classes directory in WAR file. */
    private static final String WEBINF_CLASSES_STR = "WEB-INF/classes/";

    /** Length of application classes directory path String. */
    private static final int WEBINF_CLASSES_LEN = WEBINF_CLASSES_STR.length();

    /**
     * Default constructor.
     */
    protected PersistenceUnitProcessor() {
    }

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
        Set<String> set = new HashSet<>(persistenceUnitInfo.getManagedClassNames());
        ClassLoader loader = persistenceUnitInfo.getClassLoader();
        Iterator<URL> i = persistenceUnitInfo.getJarFileUrls().iterator();
        while (i.hasNext()) {
            set.addAll(getClassNamesFromURL(i.next(), loader, properties));
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
        ArrayList<MetadataClass> entityList = new ArrayList<>();
        for (String className : processor.getProject().getWeavableClassNames()) {
            entityList.add(processor.getMetadataFactory().getMetadataClass(className));
        }
        return entityList;
    }

    /**
     * Determine the URL path to the persistence unit
     * @param pxmlURL - URL of a resource belonging to the PU (obtained for
     * {@code descriptorLocation} via {@code Classloader.getResource(String)}).
     * @param descriptorLocation - the name of the resource.
     * @return The URL of the PU root containing the resource.
     * @throws ValidationException if the resolved root doesn't conform to the
     * JPA specification (8.2)
     */
    public static URL computePURootURL(URL pxmlURL, String descriptorLocation) throws IOException, URISyntaxException {
        StringTokenizer tokenizer = new StringTokenizer(descriptorLocation, "/\\");
        int descriptorDepth = tokenizer.countTokens() - 1;
        URL result;
        String protocol = pxmlURL.getProtocol();
        if("file".equals(protocol)) { // NOI18N
            if (pxmlURL.getPath().endsWith(descriptorLocation)) {
                StringBuilder path = new StringBuilder();
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
            } else {
                result = new URL(pxmlURL.toString());
            }
        } else if("zip".equals(protocol) ||
                  "jar".equals(protocol) ||
                  "wsjar".equals(protocol)) {
            // e.g. file:/foo/bar.jar!/META-INF/persistence.xml
            // "zip:" URLs require additional handling.
            String spec = "zip".equals(protocol)
                ? "file:" + pxmlURL.getFile()
                : pxmlURL.getFile();

            // Warning: if we ever support nested archive URLs here, make sure
            // that we get the entry in the *innermost* archive.
            int separator = spec.lastIndexOf("!/");

            // It could be possible for a "zip:" or "wsjar:" URL to not have
            // an entry! In that case we take the root of the archive.
            String file = separator == -1 ? spec : spec.substring(0, separator);
            String entry = separator == -1 ? "" : spec.substring(separator + 2);

            // The jar file or directory whose META-INF directory contains
            // the persistence.xml file is termed the root of the persistence
            // unit. (JPA Spec, 8.2)
            if (!entry.endsWith(descriptorLocation)) {
                // Shouldn't happen unless we have a particularly tricky
                // classloader - which we're not obligated to support.
                throw ValidationException.invalidPersistenceRootUrl(pxmlURL, descriptorLocation);
            }

            String rootEntry = entry.substring(0, entry.length() - descriptorLocation.length());

            // "wsjar:" URLs always have an entry for historical reasons.
            result = !rootEntry.isEmpty() || "wsjar".equals(protocol)
                ? new URL("jar:" + file + "!/" + rootEntry)
                : new URL(file);

            // Since EclipseLink is a reference implementation, let's validate
            // the produced root!
            if (!isValidRootInArchive(file, rootEntry)) {
                throw ValidationException.invalidPersistenceRootUrl(pxmlURL, descriptorLocation);
            }

        } else if ("bundleentry".equals(protocol)) {
            // mkeith - add bundle protocol cases
            result = new URL("bundleentry://" + pxmlURL.getAuthority());
        } else if ("bundleresource".equals(protocol)) {
            result = new URL("bundleresource://" + pxmlURL.getAuthority());
        } else {
            if (pxmlURL.getPath().endsWith(descriptorLocation)) {
                StringBuilder path = new StringBuilder();
                path.append("../".repeat(Math.max(0, descriptorDepth))); // 315097 URL use standard separators
                // some other protocol
                result = new URL(pxmlURL, path.toString()); // NOI18N
            } else {
                // some other protocol
                result = new URL(pxmlURL.toString());
            }
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
            if (!authority.isEmpty()) {
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
        String descriptorLocation = PrivilegedAccessHelper.getSystemProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML_DEFAULT);

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

        Set<Archive> archives = new HashSet<>();

        // See if we are talking about an embedded descriptor
        int splitPosition = descriptorPath.indexOf("!/");

        try {
            // If not embedded descriptor then just use the regular descriptor path
            if (splitPosition == -1) {
                Enumeration<URL> resources = loader.getResources(descriptorPath);
                while (resources.hasMoreElements()){

                    URL descUrl = resources.nextElement();
                    if (descUrl == null) continue;

                    URL puRootUrl = computePURootURL(descUrl, descriptorPath);

                    archive = PersistenceUnitProcessor.getArchiveFactory(loader).createArchive(puRootUrl, descriptorPath, null);
                    if (archive == null) continue;

                    // archive = new BundleArchive(puRootUrl, descUrl);
                    archives.add(archive);
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
    public static Set<Archive> findPersistenceArchives(ClassLoader loader, String descriptorPath, List<URL> jarFileUrls, Map properties) {
        Archive archive = null;

        Set<Archive> archives = new HashSet<>();

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
                archive = PersistenceUnitProcessor.getArchiveFactory(loader, properties).createArchive(puRootUrl, descriptorPath, null);

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
            descriptorPath = PrivilegedAccessHelper.getSystemProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML_DEFAULT);
        }
        Set<Archive> archives = findPersistenceArchives(loader, descriptorPath, jarFileUrls, m);
        Set<SEPersistenceUnitInfo> puInfos = new HashSet<>();
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
        return getArchiveFactory(loader, null);
    }

    public static ArchiveFactory getArchiveFactory(ClassLoader loader, Map properties){
        if (ARCHIVE_FACTORY != null){
            return ARCHIVE_FACTORY;
        }

        ArchiveFactory factory = null;
        String factoryClassName = PrivilegedAccessHelper.shouldUsePrivilegedAccess()
                ? AccessController.doPrivileged(new PrivilegedGetSystemProperty(SystemProperties.ARCHIVE_FACTORY))
                : System.getProperty(SystemProperties.ARCHIVE_FACTORY);

        if (factoryClassName == null && properties != null) {
            Object name = properties.get(SystemProperties.ARCHIVE_FACTORY);
            if(name instanceof String) {
                factoryClassName = (String) name;
            }
        }

        if (factoryClassName == null) {
            return new ArchiveFactoryImpl();
        } else {
            try {
                if (loader != null) {
                    Class<?> archiveClass = loader.loadClass(factoryClassName);
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
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException cnfe) {
                throw PersistenceUnitLoadingException.exceptionCreatingArchiveFactory(factoryClassName, cnfe);
            }
        }

        return factory;
    }

    public static Set<String> getClassNamesFromURL(URL url, ClassLoader loader, Map properties) {
        Set<String> classNames = new HashSet<>();
        Archive archive = null;
        try {
            archive = PersistenceUnitProcessor.getArchiveFactory(loader, properties).createArchive(url, properties);

            if (archive != null) {
                for (Iterator<String> entries = archive.getEntries(); entries.hasNext();) {
                    String entry = entries.next();
                    if (entry.endsWith(".class")){ // NOI18N
                        classNames.add(buildClassNameFromEntryString(entry));
                    }
                }
            }
        } catch (URISyntaxException | IOException e) {
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
    public static MetadataAnnotation getConverterAnnotation(MetadataClass candidateClass){
        return candidateClass.getAnnotation(JPA_CONVERTER);
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
     * Return if a given class is annotated with @Entity.
     */
    public static MetadataAnnotation getMappedSuperclassAnnotation(MetadataClass candidateClass){
        return candidateClass.getAnnotation(JPA_MAPPED_SUPERCLASS);
    }

    /**
     * Return the @StaticMetamodel annotation on the given class.
     */
    public static MetadataAnnotation getStaticMetamodelAnnotation(MetadataClass candidateClass){
        return candidateClass.getAnnotation(JPA_STATIC_METAMODEL);
    }

    /**
     * Return if a given class is annotated with @Converter.
     */
    public static boolean isConverter(MetadataClass candidateClass) {
        return candidateClass.isAnnotationPresent(JPA_CONVERTER);
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
     * Return if a given class is annotated with @MappedSuperclass.
     */
    public static boolean isMappedSuperclass(MetadataClass candidateClass){
        return candidateClass.isAnnotationPresent(JPA_MAPPED_SUPERCLASS);
    }

    /**
     * Load the given class name with the given class loader.
     */
    public static Class<?> loadClass(String className, ClassLoader loader, boolean throwExceptionIfNotFound, MetadataProject project) {
        Class<?> candidateClass = null;

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
        try (InputStream descriptorStream = archive.getDescriptorStream()) {
            return processPersistenceXML(puRootURL, descriptorStream, loader);
          } catch (Exception e) {
            throw PersistenceUnitLoadingException.exceptionLoadingFromUrl(puRootURL.toString(), e);
        }
    }

    /**
     * Build a persistence.xml file into a SEPersistenceUnitInfo object.
     * May eventually change this to use OX mapping as well.
     */
    private static List<SEPersistenceUnitInfo> processPersistenceXML(URL baseURL, InputStream input, ClassLoader loader){
        SAXParserFactory spf = XMLHelper.createParserFactory(false);
        spf.setValidating(true);

        XMLReader xmlReader = null;
        SAXParser sp = null;
        XMLExceptionHandler xmlErrorHandler = new XMLExceptionHandler();

        // create a SAX parser
        try {
            sp = spf.newSAXParser();

            try {
                sp.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", XMLConstants.W3C_XML_SCHEMA_NS_URI);
            } catch (SAXException x) {
                AbstractSessionLog.getLog().log(SessionLog.FINE, SessionLog.JPA, "jaxp_sec_prop_not_supported", new Object[] {"http://java.sun.com/xml/jaxp/properties/schemaLanguage"});
                spf.setValidating(false);
                sp = spf.newSAXParser();
            }

        } catch (ParserConfigurationException | SAXException exc){
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

        EntityResolver resolver = new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) {
                int idx = systemId.lastIndexOf('/');
                String name = idx < 0 ? systemId : systemId.substring(idx + 1);
                InputStream resource = PersistenceUnitProcessor.class.getResourceAsStream("/org/eclipse/persistence/jpa/" + name);
                return resource == null ? null : new InputSource(resource);
            }
        };

        xmlReader.setEntityResolver(resolver);
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

    private static final String PU_NAME_SEPARATOR = "_";
    private static final String PU_HASH_SEPARATOR = "?";

    /**
     * Build the unique persistence name by concatenating the decoded URL with the persistence unit name.
     * A decoded URL is required while persisting on a multi-bytes OS.
     *
     * @param rootURL root {@link URL} of the persistence unit
     * @param puName name of the persistence unit
     * @param hash programmatically defined persistence unit hash
     *             or {@code null} when {@code persistence.xml} is source of the persistence unit
     * @return unique persistence unit name
     */
   public static String buildPersistenceUnitName(URL rootURL, String puName, String hash) {
       String urlPrefix = URLDecoder.decode(rootURL.toString(), StandardCharsets.UTF_8);
       StringBuilder fullPuName = new StringBuilder(
               urlPrefix.length()
                       + PU_NAME_SEPARATOR.length()
                       + puName.length()
                       + (hash != null ? (PU_HASH_SEPARATOR.length() + hash.length()) : 0))
               .append(urlPrefix)
               .append(PU_NAME_SEPARATOR)
               .append(puName);
       if (hash != null) {
           fullPuName.append(PU_HASH_SEPARATOR)
                   .append(hash);
       }
       return fullPuName.toString();
   }

    /**
     * @param file archive file URL. In case of a nested archive, this is
     * the URL of the innermost archive.
     * @param rootEntry a directory entry in the archive (or an empty string).
     * In case of a nested archive, this is the entry in the innermost archive.
     * @return true if the file-entry pair can be a persistence root according
     * to JPA Spec (8.2).
     */
    private static boolean isValidRootInArchive(String file, String rootEntry) {
        String extension = file.substring(Math.max(0, file.length() - 4));
        if (extension.equalsIgnoreCase(".jar")) {
            // For a JAR, the root can only be the archive itself.
            return rootEntry.isEmpty();
        } else if (extension.equalsIgnoreCase(".war")) {
            // For a WAR, the root can be:
            // 1. WEB-INF/classes
            // 2. One of a JARs inside the WEB-INF/lib
            // In the second case rootEntry is the entry in the innermost
            // archive, and file is the URL of that archive. Since
            // the innermost archive has to be a JAR (according to JPA Spec),
            // this case is handled by the previous branch.
            return rootEntry.equals(WEBINF_CLASSES_STR) || rootEntry.isEmpty();
        } else {
            return false;
        }
    }
}
