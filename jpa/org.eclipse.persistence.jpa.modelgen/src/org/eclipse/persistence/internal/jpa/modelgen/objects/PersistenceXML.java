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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;

/**
 * A representation of a persistence xml file. 
 * 
 * @author Guy Pelletier, Doug Clarke
 * @since EclipseLink 1.2
 */
public class PersistenceXML { 
    private List<SEPersistenceUnitInfo> persistenceUnitInfos = new ArrayList<SEPersistenceUnitInfo>();
    
    /**
     * INTERNAL:
     */
    public List<SEPersistenceUnitInfo> getPersistenceUnitInfos() {
        return persistenceUnitInfos;
    }
}