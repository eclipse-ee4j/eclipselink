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
package org.eclipse.persistence.testing.tests.queries.report;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * Insert the type's description here.
 * Creation date: (3/2/2003 8:51:02 AM)
 * @author: Administrator
 */
public class Brewer implements Cloneable, java.io.Serializable {
    protected String key = null;
    protected String name = null;

    public Brewer() {
    }

    /**
     * Bar constructor comment.
     */
    public Brewer(String name) {
        super();
        this.name = name;
    }

    public java.lang.String getKey() {
        return key;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setKey(java.lang.String newKey) {
        key = newKey;
    }

    public void setName(java.lang.String newName) {
        name = newName;
    }

    private void printSomething() {
        System.out.println("BREWER");
    }

    public void go() {
        this.printSomething();
    }

    public Object clone() {
        Brewer brewer = new Brewer();
        brewer.key = this.key;
        brewer.name = this.name;
        return brewer;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition table = new TableDefinition();
        table.setName("BREWER");
        table.addField("KEY_BREWER", String.class);
        table.addField("TXT_NAME", String.class);
        return table;
    }
}
