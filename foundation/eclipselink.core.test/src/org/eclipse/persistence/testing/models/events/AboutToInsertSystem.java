/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.events;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;

public class AboutToInsertSystem extends TestSystem {
    public AboutToInsertSystem() {
        project = new AboutToInsertProject();
    }

    public void addDescriptors(DatabaseSession session) {
        Vector descriptors = new Vector();
        if (project == null) {
            project = new AboutToInsertProject();
        }
        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        (new AboutToInsertProjectTableCreator()).replaceTables(session);
    }

    public void populate(DatabaseSession session) {
    }
}
