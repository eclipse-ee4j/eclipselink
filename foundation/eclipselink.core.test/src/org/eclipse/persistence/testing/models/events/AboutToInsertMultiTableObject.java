/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.events;

/**
 * CR#3237
 * Domain Object for AboutToInsert tests
 * This object maps one object to multiple tables
 */
public class AboutToInsertMultiTableObject {
    private java.lang.Double id;

    public java.lang.Double getId() {
        return id;
    }

    public void setId(java.lang.Double id) {
        this.id = id;
    }
}