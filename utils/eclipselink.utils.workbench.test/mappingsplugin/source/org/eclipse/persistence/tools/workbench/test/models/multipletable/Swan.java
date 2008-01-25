/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.multipletable;

/**
 * A Swan object uses mutliple table foreign key.
 *
 * @auther Guy Pelletier
 * @version 1.0
 * @date May 28, 2007
 */
public class Swan {
    protected int id;
    protected String name;
    protected int cygnetCount;

    public Swan() {
    }

    public int getCygnetCount() {
        return this.cygnetCount;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setCygnetCount(int cygnetCount) {
        this.cygnetCount = cygnetCount;
    }

    public void setName(String name) {
        this.name = name;
    }
}
