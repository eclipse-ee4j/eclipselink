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
 *     08/10/2009-2.0 Guy Pelletier 
 *       - 267391: JPA 2.0 implement/extend/use an APT tooling library for MetaModel API canonical classes 
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.modelgen.objects;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitProperty;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXML;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

import org.eclipse.persistence.sessions.Project;

/**
 * A mapping project used to read JPA 2.0 metadata (persistence.xml and its 
 * persistence units) through OX mappings. 
 * 
 * @author Guy Pelletier, Doug Clarke
 * @since EclipseLink 1.2
 */
public class PersistenceXMLMappings {

    /**
     * INTERNAL:
     */
    private static XMLDescriptor buildPersistenceXMLDescriptor(NamespaceResolver resolver) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setNamespaceResolver(resolver);

        descriptor.setJavaClass(PersistenceXML.class);
        descriptor.setDefaultRootElement("persistence");
        
        XMLCompositeCollectionMapping puMapping = new XMLCompositeCollectionMapping();
        puMapping.setAttributeName("persistenceUnitInfos");
        puMapping.setReferenceClass(SEPersistenceUnitInfo.class);
        puMapping.setXPath("persistence-unit");
        descriptor.addMapping(puMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     */
    private static XMLDescriptor buildPUInfoDescriptor(NamespaceResolver resolver) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setNamespaceResolver(resolver);

        descriptor.setJavaClass(SEPersistenceUnitInfo.class);
        
        descriptor.addDirectMapping("persistenceUnitName", "@name");
        
        // For the canonical model generation we don't exclude the unlisted
        // classes unless explicitly set by the user.
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("excludeUnlistedClasses");
        mapping.setXPath("exclude-unlisted-classes/text()");
        mapping.setNullValue(false);
        descriptor.addMapping(mapping);
        
        XMLCompositeDirectCollectionMapping classesMapping = new XMLCompositeDirectCollectionMapping();
        classesMapping.setAttributeName("managedClassNames");
        classesMapping.setXPath("class/text()");
        descriptor.addMapping(classesMapping);

        XMLCompositeDirectCollectionMapping mappingFilesMapping = new XMLCompositeDirectCollectionMapping();
        mappingFilesMapping.setAttributeName("mappingFiles");
        mappingFilesMapping.setXPath("mapping-file/text()");
        descriptor.addMapping(mappingFilesMapping);
        
        XMLCompositeCollectionMapping persistenceUnitPropertiesMapping = new XMLCompositeCollectionMapping();
        persistenceUnitPropertiesMapping.setAttributeName("persistenceUnitProperties");
        persistenceUnitPropertiesMapping.setGetMethodName("getPersistenceUnitProperties");
        persistenceUnitPropertiesMapping.setSetMethodName("setPersistenceUnitProperties");
        persistenceUnitPropertiesMapping.setReferenceClass(SEPersistenceUnitProperty.class);
        persistenceUnitPropertiesMapping.setXPath("properties/property");
        descriptor.addMapping(persistenceUnitPropertiesMapping);

        return descriptor;
    }
    
    /**
     * INTERNAL:
     */
    private static XMLDescriptor buildPUPropertyDescriptor(NamespaceResolver resolver) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setNamespaceResolver(resolver);
        descriptor.setJavaClass(SEPersistenceUnitProperty.class);
        descriptor.addDirectMapping("name", "@name");
        descriptor.addDirectMapping("value", "@value");
        
        return descriptor;
    }
    
    /**
     * INTERNAL:
     */
    public static XMLContext createXMLContext() {
        Project project = new Project();

        NamespaceResolver resolver = new NamespaceResolver();
        resolver.setDefaultNamespaceURI("http://java.sun.com/xml/ns/persistence");

        project.addDescriptor(buildPersistenceXMLDescriptor(resolver));
        project.addDescriptor(buildPUInfoDescriptor(resolver));
        project.addDescriptor(buildPUPropertyDescriptor(resolver));

        return new XMLContext(project);
    }
}

