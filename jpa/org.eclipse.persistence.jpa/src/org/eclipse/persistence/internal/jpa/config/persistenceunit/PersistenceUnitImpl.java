/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier, Doug Clarke - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.persistenceunit;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.config.xml.MappingsImpl;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.jpa.config.Mappings;
import org.eclipse.persistence.jpa.config.PersistenceUnit;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class PersistenceUnitImpl implements PersistenceUnit {

    private SEPersistenceUnitInfo puInfo;

    private List<Mappings> mappings = new ArrayList<Mappings>();

    public PersistenceUnitImpl(String name, ClassLoader cl) {
        puInfo = new SEPersistenceUnitInfo();

        puInfo.setClassLoader(cl == null ? Thread.currentThread().getContextClassLoader() : cl);

        String persistenceFactoryResource = PersistenceUnitImpl.class.getName().replace('.', '/') + ".class";
        URL puURL = PersistenceUnitImpl.class.getClassLoader().getResource(persistenceFactoryResource);
        try{
            puURL = PersistenceUnitProcessor.computePURootURL(puURL, persistenceFactoryResource);
        } catch (URISyntaxException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        puInfo.setPersistenceUnitRootUrl(puURL);
        puInfo.setProperties(new Properties());

        setName(name);
        setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
        setProperty(PersistenceUnitProperties.SESSION_NAME, name);
    }

    public PersistenceUnitImpl(String name) {
        this(name, null);
    }

    @Override
    public Mappings addMappings() {
        Mappings mappings = new MappingsImpl();
        this.mappings.add(mappings);
        return mappings;
    }

    @Override
    public ClassLoader getClassLoader() {
        return puInfo.getClassLoader();
    }

    @Override
    public PersistenceUnitInfo getPersistenceUnitInfo() {
        return puInfo;
    }

    public XMLEntityMappings getMappings() {
        return ((MappingsImpl) mappings.get(0)).getMetadata();
    }

    @Override
    public String getName() {
        return puInfo.getPersistenceUnitName();
    }

    @Override
    public PersistenceUnit setClass(String cls) {
        puInfo.getManagedClassNames().add(cls);
        return this;
    }

    @Override
    public PersistenceUnit setExcludeUnlistedClasses(Boolean excludeUnlistedClasses) {
        puInfo.setExcludeUnlistedClasses(excludeUnlistedClasses);
        return this;
    }

    @Override
    public PersistenceUnit setJarFile(String jarFile) {
        puInfo.getJarFiles().add(jarFile);
        return this;
    }

    @Override
    public PersistenceUnit setJtaDataSource(String jtaDataSource) {
        setProperty(PersistenceUnitProperties.JTA_DATASOURCE, jtaDataSource);
        return this;
    }

    @Override
    public PersistenceUnit setMappingFile(String mappingFile) {
        puInfo.getMappingFileNames().add(mappingFile);
        return this;
    }

    @Override
    public PersistenceUnit setName(String name) {
        puInfo.setPersistenceUnitName(name);
        return this;
    }

    @Override
    public PersistenceUnit setNonJtaDataSource(String nonJtaDataSource) {
        setProperty(PersistenceUnitProperties.NON_JTA_DATASOURCE, nonJtaDataSource);
        return this;
    }

    @Override
    public PersistenceUnit setProperty(String name, Object value) {
        puInfo.getProperties().put(name, value);
        return this;
    }

    @Override
    public PersistenceUnit setProvider(String provider) {
        puInfo.setPersistenceProviderClassName(provider);
        return this;
    }

    @Override
    public PersistenceUnit setSharedCacheMode(String sharedCacheMode) {
        puInfo.setSharedCacheMode(sharedCacheMode);
        return this;
    }

    @Override
    public PersistenceUnit setTransactionType(PersistenceUnitTransactionType transactionType) {
        puInfo.setTransactionType(transactionType);
        return this;
    }

    @Override
    public PersistenceUnit setValidationMode(String validationMode) {
        puInfo.setValidationMode(validationMode);
        return this;
    }

}
