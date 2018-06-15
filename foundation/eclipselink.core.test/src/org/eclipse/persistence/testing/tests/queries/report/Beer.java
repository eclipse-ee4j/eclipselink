/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries.report;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Beer implements Cloneable, java.io.Serializable {
    protected String key = null;
    protected String brand = null;
    protected java.util.Vector barCollection = null;
    protected Brewer brewer = null;

    public Beer() {
    }

    /**
     * Beer constructor comment.
     */
    public Beer(String brand) {
        super();
        this.brand = brand;
    }

    public void addBar(Bar aBar) {
        getBarCollection().addElement(aBar);
        aBar.getBeerCollection().addElement(this);
    }

    public java.util.Vector getBarCollection() {
        if (barCollection == null) {
            barCollection = new java.util.Vector();
        }
        return barCollection;
    }

    public java.lang.String getBrand() {
        return brand;
    }

    public Brewer getBrewer() {
        return brewer;
    }

    public java.lang.String getKey() {
        return key;
    }

    public void removeBar(Bar aBar) {
        getBarCollection().remove(aBar);
        aBar.getBeerCollection().remove(this);
    }

    public boolean servedAt(Bar aBar) {
        return getBarCollection().contains(aBar);
    }

    public void setBarCollection(java.util.Vector newBarCollection) {
        barCollection = newBarCollection;
    }

    public void setBrand(java.lang.String newBrand) {
        brand = newBrand;
    }

    public void setBrewer(Brewer brewer) {
        this.brewer = brewer;
    }

    public void setKey(java.lang.String newKey) {
        key = newKey;
    }

    public void go() {
        this.printSomething();
    }

    private void printSomething() {
        System.out.println("BEER");
    }

    public Object clone() {
        Beer beer = new Beer();
        beer.key = this.key;
        beer.brand = this.brand;
        return beer;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition table = new TableDefinition();
        table.setName("BEER");
        table.addField("KEY_BEER", String.class);
        table.addField("TXT_BRAND", String.class);
        table.addField("KEY_BREWER", String.class);
        return table;
    }
}
