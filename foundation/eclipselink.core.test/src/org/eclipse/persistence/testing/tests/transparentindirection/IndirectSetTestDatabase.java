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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.transparentindirection.SetOrder;
import org.eclipse.persistence.testing.models.transparentindirection.AbstractOrder;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectSetProject;

/**
 * Test the IndirectList with assorted DatabaseSessions and UnitsOfWork.
 * this should only be used in jdk1.2+
 * @author: Big Country
 */
public class IndirectSetTestDatabase extends IndirectContainerTestDatabase {

    /**
     * Constructor
     * @param name java.lang.String
     */
    public IndirectSetTestDatabase(String name) {
        super(name);
    }

    protected AbstractOrder buildOrderShell() {
        return new SetOrder();
    }

    protected AbstractOrder buildTestOrderShell(String customerName) {
        return new SetOrder(customerName);
    }

    /**
     * build the TopLink project
     */
    public Project setUpProjectFromCode() {
        return new IndirectSetProject();
    }
}
