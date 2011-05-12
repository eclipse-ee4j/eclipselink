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
 ******************************************************************************/  
package org.eclipse.persistence.jpa.metadata;

import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p><b>Purpose</b>: To provide a trivial implementation of MetadataRepository.
 * You may subclass this class rather than implement the MetadataRepositoryReader
 * interface allowing insulation from future additions to the interface.
 *
 * @see MetadataRepository
 * 
 * @author Chris Delahunt
 * @since EclipseLink 2.3
 */
public class MetadataSourceAdapter implements MetadataSource {

    public XMLEntityMappings getEntityMappings(Map properties, ClassLoader classLoader, SessionLog log) {
        return null;
    }

}
