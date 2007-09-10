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

/**
 * A Human object uses mutliple table primary key.
 *
 * @auther Guy Pelletier
 * @version 1.0
 * @date May 28, 2007
 */
public class Human {
    protected int id;
    protected String name;
    protected int kidCount;

    public Human() {
    }

    public int getKidCount() {
        return this.kidCount;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setKidCount(int kidCount) {
        this.kidCount = kidCount;
    }

    public void setName(String name) {
        this.name = name;
    }
}
