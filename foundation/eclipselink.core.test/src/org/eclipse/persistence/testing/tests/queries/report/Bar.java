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

public class Bar implements Cloneable, java.io.Serializable {
    protected String key = null;
    protected String name = null;
    protected java.util.Vector beerCollection = null;
    protected Person brewer = null;

    public Bar() {
    }

    /**
     * Bar constructor comment.
     */
    public Bar(String name) {
        super();
        this.name = name;
    }

    public void addBeer(Beer aBeer) {
        getBeerCollection().addElement(aBeer);
        aBeer.getBarCollection().addElement(this);
    }

    public java.util.Vector getBeerCollection() {
        if (beerCollection == null) {
            beerCollection = new java.util.Vector();
        }
        return beerCollection;
    }

    public java.lang.String getKey() {
        return key;
    }

    public Person getBrewer() {
        return brewer;
    }

    public java.lang.String getName() {
        return name;
    }

    public void removeBeer(Beer aBeer) {
        getBeerCollection().remove(aBeer);
        aBeer.getBarCollection().remove(this);
    }

    public boolean servesBeer(Beer aBeer) {
        return getBeerCollection().contains(aBeer);
    }

    public void setBeerCollection(java.util.Vector newBeerCollection) {
        beerCollection = newBeerCollection;
    }

    public void setKey(java.lang.String newKey) {
        key = newKey;
    }

    public void setBrewer(Person brewer) {
        this.brewer = brewer;
    }

    public void setName(java.lang.String newName) {
        name = newName;
    }

    private void printSomething() {
        System.out.println("BAR");
    }

    public void go() {
        this.printSomething();
    }

    public Object clone() {
        Bar bar = new Bar();
        bar.key = this.key;
        bar.name = this.name;
        for (int index = 0; index < this.beerCollection.size(); ++index) {
            bar.addBeer((Beer)((Beer)this.beerCollection.get(index)).clone());
        }
        return bar;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition table = new TableDefinition();
        table.setName("BAR");
        table.addField("KEY_BAR", String.class);
        table.addField("TXT_NAME", String.class);
        table.addField("KEY_PERSON", String.class);
        return table;
    }

    public static TableDefinition beerTableDefinition() {
        TableDefinition table = new TableDefinition();
        table.setName("BAR_BEER");
        table.addField("KEY_BAR", String.class);
        table.addField("KEY_BEER", String.class);
        return table;
    }
}
