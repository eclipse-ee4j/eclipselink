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

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceXML;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;

/**
 * A mapping project used to read JPA 2.0 metadata (persistence.xml and its 
 * persistence units) through OX mappings. 
 * 
 * @author Guy Pelletier, Doug Clarke
 * @since Eclipselink 2.0
 */
public class PersistenceXMLMappings {

    /**
     * INTERNAL:
     */
    public static XMLContext createXMLContext() {
        Project project = new Project();

        NamespaceResolver resolver = new NamespaceResolver();
        resolver.setDefaultNamespaceURI("http://java.sun.com/xml/ns/persistence");

        project.addDescriptor(createPersistenceXMLDescriptor(resolver));
        project.addDescriptor(createPUInfoDescriptor(resolver));

        return new XMLContext(project);
    }

    /**
     * INTERNAL:
     */
    private static XMLDescriptor createPersistenceXMLDescriptor(NamespaceResolver resolver) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setNamespaceResolver(resolver);

        descriptor.setJavaClass(PersistenceXML.class);
        descriptor.setDefaultRootElement("persistence");

        descriptor.addDirectMapping("m_version", "@version");
        
        XMLCompositeCollectionMapping puMapping = new XMLCompositeCollectionMapping();
        puMapping.setAttributeName("m_persistenceUnitInfos");
        puMapping.setReferenceClass(SEPersistenceUnitInfo.class);
        puMapping.setXPath("persistence-unit");
        descriptor.addMapping(puMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     */
    private static XMLDescriptor createPUInfoDescriptor(NamespaceResolver resolver) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setNamespaceResolver(resolver);

        descriptor.setJavaClass(SEPersistenceUnitInfo.class);
        
        descriptor.addDirectMapping("persistenceUnitName", "@name");
        
        descriptor.addDirectMapping("excludeUnlistedClasses", "exclude-unlisted-classes/text()");
        
        XMLCompositeDirectCollectionMapping classesMapping = new XMLCompositeDirectCollectionMapping();
        classesMapping.setAttributeName("managedClassNames");
        classesMapping.setXPath("class/text()");
        descriptor.addMapping(classesMapping);

        XMLCompositeDirectCollectionMapping mappingFilesMapping = new XMLCompositeDirectCollectionMapping();
        mappingFilesMapping.setAttributeName("mappingFiles");
        mappingFilesMapping.setXPath("mapping-file/text()");
        descriptor.addMapping(mappingFilesMapping);

        return descriptor;
    }
}

