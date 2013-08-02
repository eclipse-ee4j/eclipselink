/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * 		dclarke/tware - initial implementation
 *      gonural - version based persistence context
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.jpa.Archive;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.features.FeatureSetPreV2;
import org.eclipse.persistence.jpa.rs.features.FeatureSetV2;
import org.eclipse.persistence.jpa.rs.resources.common.AbstractResource;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;

/**
 * Manages the PersistenceContexts that are used by a JPA-RS deployment.  Provides a single point to bootstrap
 * and look up PersistenceContexts 
 * @author tware
 *
 */
public class PersistenceFactoryBase implements PersistenceContextFactory {
    protected Map<String, Set<PersistenceContext>> dynamicPersistenceContexts = new HashMap<String, Set<PersistenceContext>>();

    /**
     * Bootstrap a PersistenceContext based on an pre-existing EntityManagerFactory
     * @param name
     * @param emf
     * @param baseURI
     * @param replace
     * @return
     */
    public PersistenceContext bootstrapPersistenceContext(String name, EntityManagerFactory emf, URI baseURI, String version, boolean replace) {
        PersistenceContext persistenceContext = new PersistenceContext(name, (EntityManagerFactoryImpl) emf, baseURI);
        persistenceContext.setBaseURI(baseURI);
        persistenceContext.setVersion(version);
        if (persistenceContext.isVersionGreaterOrEqualTo(AbstractResource.SERVICE_VERSION_2_0)) {
            persistenceContext.setSupportedFeatureSet(new FeatureSetV2());
        } else {
            persistenceContext.setSupportedFeatureSet(new FeatureSetPreV2());
        }
        return persistenceContext;
    }

    /**
     * Stop the factory.  Remove all the PersistenceContexts.
     */
    public void close() {
        synchronized (this) {
            for (String key : dynamicPersistenceContexts.keySet()) {
                Set<PersistenceContext> contextSet = dynamicPersistenceContexts.get(key);
                if (contextSet != null) {
                    for (PersistenceContext context : contextSet) {
                        context.stop();
                    }
                }
            }
            dynamicPersistenceContexts.clear();
        }
    }

    /**
     * Close the PersistenceContext of a given name and clean it out of our list of PersistenceContexts
     * @param name
     */
    public void closePersistenceContext(String name) {
        synchronized (this) {
            Set<PersistenceContext> contextSet = dynamicPersistenceContexts.get(name);
            if (contextSet != null) {
                for (PersistenceContext context : contextSet) {
                    context.stop();
                }
            }
            dynamicPersistenceContexts.remove(name);
        }
    }

    /**
     * Provide an initial set of properties for bootstrapping PersistenceContexts.
     * @param dcl
     * @param originalProperties
     * @return
     */
    protected static Map<String, Object> createProperties(DynamicClassLoader dcl, Map<String, ?> originalProperties) {
        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
        properties.put(PersistenceUnitProperties.WEAVING, "static");

        // For now we'll copy the connection info from admin PU
        for (Map.Entry<String, ?> entry : originalProperties.entrySet()) {
            if (entry.getKey().startsWith("javax") || entry.getKey().startsWith("eclipselink.log") || entry.getKey().startsWith("eclipselink.target-server")) {
                properties.put(entry.getKey(), entry.getValue());
            }
        }
        return properties;
    }

    public PersistenceContext get(String persistenceUnitName, URI defaultURI, String version, Map<String, Object> initializationProperties) {
        PersistenceContext persistenceContext = getDynamicPersistenceContext(persistenceUnitName, version);

        if (persistenceContext == null) {
            try {
                DynamicClassLoader dcl = new DynamicClassLoader(Thread.currentThread().getContextClassLoader());
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
                if (initializationProperties != null) {
                    properties.putAll(initializationProperties);
                }

                EntityManagerFactoryImpl factory = (EntityManagerFactoryImpl) Persistence.createEntityManagerFactory(persistenceUnitName, properties);
                ClassLoader sessionLoader = factory.getServerSession().getLoader();
                if (!DynamicClassLoader.class.isAssignableFrom(sessionLoader.getClass())) {
                    properties = new HashMap<String, Object>();
                    dcl = new DynamicClassLoader(sessionLoader);
                    properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
                    if (initializationProperties != null) {
                        properties.putAll(initializationProperties);
                    }
                    factory.refreshMetadata(properties);
                }

                if (factory != null) {
                    persistenceContext = bootstrapPersistenceContext(persistenceUnitName, factory, defaultURI, version, true);
                    Set<PersistenceContext> persistenceContextSet = getDynamicPersistenceContextSet(persistenceUnitName);
                    if (persistenceContext != null) {
                        if (persistenceContextSet == null) {
                            persistenceContextSet = new HashSet<PersistenceContext>();
                        }
                        persistenceContextSet.add(persistenceContext);
                        synchronized (this) {
                            dynamicPersistenceContexts.put(persistenceUnitName, persistenceContextSet);
                        }
                    }
                }
            } catch (Exception e) {
                JPARSLogger.exception("exception_creating_persistence_context", new Object[] { persistenceUnitName, e.toString() }, e);
            }
        }

        if ((persistenceContext != null) && (!persistenceContext.isWeavingEnabled())) {
            JPARSLogger.error("weaving_required_for_relationships", new Object[] {});
            throw JPARSException.invalidConfiguration();
        }

        return persistenceContext;
    }

    public Set<String> getPersistenceContextNames() {
        Set<String> contextNames = new HashSet<String>();
        try {
            Set<Archive> archives = PersistenceUnitProcessor.findPersistenceArchives();
            for (Archive archive : archives) {
                List<SEPersistenceUnitInfo> infos = PersistenceUnitProcessor.processPersistenceArchive(archive, Thread.currentThread().getContextClassLoader());
                for (SEPersistenceUnitInfo info : infos) {
                    if (!info.getPersistenceUnitName().equals("jpa-rs")) {
                        if (EntityManagerSetupImpl.mustBeCompositeMember(info)) {
                            continue;
                        }
                        contextNames.add(info.getPersistenceUnitName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        contextNames.addAll(dynamicPersistenceContexts.keySet());
        return contextNames;
    }

    public PersistenceContext getDynamicPersistenceContext(String name, String version) {
        synchronized (this) {
            Set<PersistenceContext> persistenceContextSet = dynamicPersistenceContexts.get(name);
            if (persistenceContextSet != null) {
                for (PersistenceContext persistenceContext : persistenceContextSet) {
                    if ((name != null) && (version != null)) {
                        if ((name.equals(persistenceContext.getName())) && (version.equals(persistenceContext.getVersion()))) {
                            return persistenceContext;
                        }
                    } else if (((version == null) && (persistenceContext.getVersion() == null)) &&
                            ((name != null) && (name.equals(persistenceContext.getName())))) {
                        return persistenceContext;
                    }
                }
            }
        }
        return null;
    }

    private Set<PersistenceContext> getDynamicPersistenceContextSet(String name) {
        synchronized (this) {
            return dynamicPersistenceContexts.get(name);
        }
    }
}
