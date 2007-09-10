/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.transparentindirection;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestSystem;

/**
 * @auther Guy Pelletier
 * @version 1.0
 * @date March 21, 2005
 */
public class BidirectionalRelationshipSystem extends TestSystem {
    public BidirectionalRelationshipSystem() {
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = new BidirectionalRelationshipProject(session);
        }

        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        new BidirectionalRelationshipTableCreator().replaceTables(session);
    }
}