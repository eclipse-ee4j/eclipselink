/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *     			 http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.tools.schemaframework;

//EclipseLink imports
import java.util.Collection;

import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * Extended SchemaManager to handle the creation of tables for dynamic types.
 * 
 * TODO: Handle the case where the provided session uses an external transaction
 * controller or is from an external connection pool. In these cases a custom
 * direct connection must be created cloning the minimal state needed from the
 * primary session.
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public class DynamicSchemaManager extends SchemaManager {

    public DynamicSchemaManager(DatabaseSession session) {
        super(session);

    }

    /**
     * Create the database tables for one or more entityTypes.
     * 
     * TODO: At present this method will create all tables that do not exist. It
     * needs to be made specific to the entityTypes.
     * 
     * @param entityTypes
     */
    public void createTables(DynamicType... entityTypes) {
        createTables(true, entityTypes);
    }
    
    public void createTables(boolean generateFKConstraints, DynamicType... entityTypes) {
        AbstractSession createSession = getSession();

        TableCreator creator = new DefaultTableGenerator(getSession().getProject(), generateFKConstraints).generateFilteredDefaultTableCreator(createSession);
        creator.setIgnoreDatabaseException(true);
        creator.createTables((DatabaseSession) getSession(), this);
    }
    
    public void createTables(boolean generateFKConstraints, Collection<DynamicType> entityTypes) {
        AbstractSession createSession = getSession();

        TableCreator creator = new DefaultTableGenerator(getSession().getProject(), generateFKConstraints).generateFilteredDefaultTableCreator(createSession);
        creator.setIgnoreDatabaseException(true);
        creator.createTables((DatabaseSession) getSession(), this);
    }

}
