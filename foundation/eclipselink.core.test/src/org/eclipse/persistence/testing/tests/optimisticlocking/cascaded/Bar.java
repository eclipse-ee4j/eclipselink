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
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import java.util.*;
import org.eclipse.persistence.indirection.*;

public class Bar {
    public int id;
    public int version;
    public String name;
    public ValueHolderInterface license;
    public ValueHolderInterface bartenders;

    public Bar() {
        this.name = "";
        this.license = new ValueHolder();
        this.bartenders = new ValueHolder(new Vector());
    }

    public void addBartender(Bartender bartender) {
        getBartenders().addElement(bartender);
        bartender.setBar(this);
    }

    public Vector getBartenders() {
        return (Vector) bartenders.getValue();
    }

    public int getId() {
        return id;
    }

    public License getLicense() {
        return (License) license.getValue();
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public void removeBartender(Bartender bartender) {
        getBartenders().removeElement(bartender);
    }

    public void setBartenders(Vector bartenders) {
        this.bartenders.setValue(bartenders);
    }

    protected void setId(int id) {
        this.id = id;
    }

    public void setLicense(License license) {
        this.license.setValue(license);
    }

    public void setName(String name) {
        this.name = name;
    }
}
