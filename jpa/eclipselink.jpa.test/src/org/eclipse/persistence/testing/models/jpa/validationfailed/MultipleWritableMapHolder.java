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
//     tware - test for bug 293827
package org.eclipse.persistence.testing.models.jpa.validationfailed;

import static javax.persistence.CascadeType.ALL;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_MWMAPHOLDER")
public class MultipleWritableMapHolder {

    @Id
    private int id = 0;

    @OneToMany(mappedBy="holder", cascade=ALL)
    @MapKeyColumn(name="VALUE")
    private Map<Integer, MultipleWritableMapValue> map = null;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Map getMap() {
        return map;
    }
    public void setMap(Map map) {
        this.map = map;
    }
}
