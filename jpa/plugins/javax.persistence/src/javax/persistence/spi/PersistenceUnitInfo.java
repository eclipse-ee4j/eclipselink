/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     		     Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 * Java(TM) Persistence API, Version 2.0 - EARLY ACCESS
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP).  The code is untested and presumed not to be a  
 * compatible implementation of JSR 317: Java(TM) Persistence API, Version 2.0.   
 * We encourage you to migrate to an implementation of the Java(TM) Persistence 
 * API, Version 2.0 Specification that has been tested and verified to be compatible 
 * as soon as such an implementation is available, and we encourage you to retain 
 * this notice in any implementation of Java(TM) Persistence API, Version 2.0 
 * Specification that you distribute.
 ******************************************************************************/
package javax.persistence.spi;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.persistence.CachingType;
import javax.persistence.ValidationMode;
import javax.sql.DataSource;

/**
 * Interface implemented by the container and used by the persistence provider
 * when creating an EntityManagerFactory.
 */
public interface PersistenceUnitInfo {
    /**
     * @return The name of the persistence unit. Corresponds to the name
     *         attribute in the persistence.xml file.
     */
    public String getPersistenceUnitName();

    /**
     * @return The fully qualified name of the persistence provider
     *         implementation class. Corresponds to the provider element in the
     *         persistence.xml file.
     */
    public String getPersistenceProviderClassName();

    /**
     * @return The transaction type of the entity managers created by the
     *         EntityManagerFactory. The transaction type corresponds to the
     *         transaction-type attribute in the persistence.xml file.
     */
    public PersistenceUnitTransactionType getTransactionType();

    /**
     * @return The JTA-enabled data source to be used by the persistence
     *         provider. The data source corresponds to the jta-data-source
     *         element in the persistence.xml file or is provided at deployment
     *         or by the container.
     */
    public DataSource getJtaDataSource();

    /**
     * @return The non-JTA-enabled data source to be used by the persistence
     *         provider for accessing data outside a JTA transaction. The data
     *         source corresponds to the non-jta-data-source element in the
     *         persistence.xml file or provided at deployment or by the
     *         container.
     */
    public DataSource getNonJtaDataSource();

    /**
     * @return The list of mapping file names that the persistence provider must
     *         load to determine the mappings for the entity classes. The
     *         mapping files must be in the standard XML mapping format, be
     *         uniquely named and be resource-loadable from the application
     *         classpath. Each mapping file name corresponds to a mapping-file
     *         element in the persistence.xml file.
     */
    public List<String> getMappingFileNames();

    /**
     * Returns a list of URLs for the jar files or exploded jar file directories
     * that the persistence provider must examine for managed classes of the
     * persistence unit. Each URL corresponds to a jar-file element in the
     * persistence.xml file. A URL will either be a file: URL referring to a jar
     * file or referring to a directory that contains an exploded jar file, or
     * some other URL from which an InputStream in jar format can be obtained.
     * 
     * @return a list of URL objects referring to jar files or directories.
     */
    public List<URL> getJarFileUrls();

    /**
     * Returns the URL for the jar file or directory that is the root of the
     * persistence unit. (If the persistence unit is rooted in the
     * WEB-INF/classes directory, this will be the URL of that directory.) The
     * URL will either be a file: URL referring to a jar file or referring to a
     * directory that contains an exploded jar file, or some other URL from
     * which an InputStream in jar format can be obtained.
     * 
     * @return a URL referring to a jar file or directory.
     */
    public URL getPersistenceUnitRootUrl();

    /**
     * @return The list of the names of the classes that the persistence
     *         provider must add it to its set of managed classes. Each name
     *         corresponds to a class element in the persistence.xml file.
     */
    public List<String> getManagedClassNames();

    /**
     * @return Whether classes in the root of the persistence unit that have not
     *         been explicitly listed are to be included in the set of managed
     *         classes. This value corresponds to the exclude-unlisted-classes
     *         element in the persistence.xml file.
     */
    public boolean excludeUnlistedClasses();

    /**
     * @return The specification of how the provider must use a second-level
     *         cache for the persistence unit The result of this method
     *         corresponds to the caching element in the persistence.xml file.
     */
    public CachingType getCaching();

    /**
     * @return The validation mode to be used by the persistence provider for
     *         the persistence unit. The validation mode corresponds to the
     *         validation-mode element in the persistence.xml file.
     */
    public ValidationMode getValidationMode();

    /**
     * @return Properties object. Each property corresponds to a property
     *         element in the persistence.xml file
     */
    public Properties getProperties();

    /**
     * @return persistence.xml schema version
     */
    public String PersistenceXMLSchemaVersion();

    /**
     * @return ClassLoader that the provider may use to load any classes,
     *         resources, or open URLs.
     */
    public ClassLoader getClassLoader();

    /**
     * Add a transformer supplied by the provider that will be called for every
     * new class definition or class redefinition that gets loaded by the loader
     * returned by the PersistenceUnitInfo.getClassLoader method. The
     * transformer has no effect on the result returned by the
     * PersistenceUnitInfo.getNewTempClassLoader method. Classes are only
     * transformed once within the same classloading scope, regardless of how
     * many persistence units they may be a part of.
     * 
     * @param transformer
     *            A provider-supplied transformer that the Container invokes at
     *            class-(re)definition time
     */
    public void addTransformer(ClassTransformer transformer);

    /**
     * Return a new instance of a ClassLoader that the provider may use to
     * temporarily load any classes, resources, or open URLs. The scope and
     * classpath of this loader is exactly the same as that of the loader
     * returned by PersistenceUnitInfo.getClassLoader. None of the classes
     * loaded by this class loader will be visible to application components.
     * The provider may only use this ClassLoader within the scope of the
     * createContainerEntityManagerFactory call.
     * 
     * @return Temporary ClassLoader with same visibility as current loader
     */
    public ClassLoader getNewTempClassLoader();
}