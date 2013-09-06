/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Guy Pelletier - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.config.xml;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitMetadata;
import org.eclipse.persistence.jpa.config.PersistenceUnitDefaults;
import org.eclipse.persistence.jpa.config.PersistenceUnitMetadata;

/**
 * JPA scripting API implementation.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class PersistenceUnitMetadataImpl extends MetadataImpl<XMLPersistenceUnitMetadata> implements PersistenceUnitMetadata {

    public PersistenceUnitMetadataImpl() {
        super(new XMLPersistenceUnitMetadata());
    }
    
    public PersistenceUnitMetadata setExcludeDefaultMappings(Boolean excludeDefaultMappings) {
        getMetadata().setExcludeDefaultMappings(excludeDefaultMappings);
        return this;
    }

    public PersistenceUnitDefaults setPersitenceUnitDefault() {
        PersistenceUnitDefaultsImpl defaults = new PersistenceUnitDefaultsImpl();
        getMetadata().setPersistenceUnitDefaults(defaults.getMetadata());
        return defaults;
    }
    
    public PersistenceUnitMetadata setXmlMappingMetadataComplete(Boolean xmlMappingMetadataComplete) {
        getMetadata().setXMLMappingMetadataComplete(xmlMappingMetadataComplete);
        return this;
    }

}
