/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.transparentindirection;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestSystem;

/**
 * @author Guy Pelletier
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
