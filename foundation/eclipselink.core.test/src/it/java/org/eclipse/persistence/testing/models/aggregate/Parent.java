/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.models.aggregate;

import java.util.Iterator;

public class Parent {

    protected int id;
    protected Aggregate aggregate;

    public Parent() {
        super();
        this.aggregate = new Aggregate();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Aggregate getAggregate() {
        return aggregate;
    }

    public void setAggregate(Aggregate aggregate) {
        this.aggregate = aggregate;
    }

    public void addChild(Child child) {
        if (child != null && !getAggregate().getChildren().contains(child)) {
            getAggregate().getChildren().add(child);
            child.setParent(this);
        }
    }

    public void removeChild(Child child) {
        if (child != null && getAggregate().getChildren().contains(child)) {
            getAggregate().getChildren().remove(child);
            child.setParent(null);
        }
    }

    public void addRelative(Relative relative) {
        if (relative != null && !getAggregate().getRelatives().contains(relative)) {
            getAggregate().getRelatives().add(relative);
        }
    }

    public void removeRelative(Relative relative) {
        if (relative != null && getAggregate().getRelatives().contains(relative)) {
            getAggregate().getRelatives().remove(relative);
        }
    }

    public void removeAllRelatives() {
        for (Iterator<Relative> iterator = getAggregate().getRelatives().iterator(); iterator.hasNext();) {
            removeRelative(iterator.next());
        }
    }

}
