/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.testing.models.inheritance.Computer;

public class Mainframe extends Computer {
    public int numberOfProcessors;

    public String getComputerType() {
        return "MF";
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();
        definition.setName("INH_MF");
        definition.addIdentityField("MF_ID", Integer.class);
        definition.addField("procs", Integer.class);
        return definition;
    }
}
