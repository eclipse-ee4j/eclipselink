/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.multipletable;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TestSystem;

/**
 * System for the MultipleTableModel
 *
 * @auther Guy Pelletier
 * @version 1.0
 * @date June 17, 2005
 */
public class MultipleTableSystem extends TestSystem {
    public MultipleTableSystem() {
        project = new MultipleTableProject();
    }

    public void addDescriptors(DatabaseSession session) {
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        new MultipleTableTableCreator().replaceTables(session);
    }
}
