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
 *     05/05/2011-2.3 Chris Delahunt 
 *       - 344837: Extensibility - Metadata Repository
 ******************************************************************************/  
package org.eclipse.persistence.jpa.metadata;

import java.util.Properties;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p><b>Purpose</b>: Interface used to support extensible metadata and mappings by a persistence unit.
 * A MetadataRepository will need to be registered with a persistence unit.  
 */
public interface MetadataRepositoryReader {

    /**
     * PUBLIC:
     * This is used to return the object representation of the mappings used by EclipseLink from the repository.
     * It is called on initial deployment and when the EntityManagerFactoryImpl is reloaded.  
     */
    public XMLEntityMappings getEntityMappings(Properties properties, ClassLoader classLoader, SessionLog log);
}
