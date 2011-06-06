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
package org.eclipse.persistence.testing.tests.queries.report;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Person implements Cloneable, java.io.Serializable {
    protected String key = null;
    protected String name = null;
    protected Beer faviouriteBeer = null;

    public Person() {
    }

    /**
     * Bar constructor comment.
     */
    public Person(String name) {
        super();
        this.name = name;
    }

    public java.lang.String getKey() {
        return key;
    }

    public Beer getFaviouriteBeer() {
        return faviouriteBeer;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setKey(java.lang.String newKey) {
        key = newKey;
    }

    public void setFaviouriteBeer(Beer beer) {
        this.faviouriteBeer = beer;
    }

    public void setName(java.lang.String newName) {
        name = newName;
    }

    private void printSomething() {
        System.out.println("PERSON");
    }

    public void go() {
        this.printSomething();
    }

    public Object clone() {
        Person person = new Person();
        person.key = this.key;
        person.name = this.name;
        person.faviouriteBeer = (Beer)this.faviouriteBeer.clone();
        return person;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition table = new TableDefinition();
        table.setName("PERSON");
        table.addField("KEY_PERSON", String.class);
        table.addField("TXT_NAME", String.class);
        table.addField("KEY_BEER", String.class);
        return table;
    }
}
