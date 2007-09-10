/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.inheritance;

public class Animal_Matt {
    public int id;
    private String size;

    public Animal_Matt() {
        super();
    }

    public String getSize() {
        return size;
    }

    public void setSize(String theSize) {
        size = theSize;
    }
}