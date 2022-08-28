/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     tware - test for bug 293827
package org.eclipse.persistence.testing.models.jpa.validationfailed;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Map;

import static jakarta.persistence.CascadeType.ALL;

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
    public Map<Integer, MultipleWritableMapValue> getMap() {
        return map;
    }
    public void setMap(Map<Integer, MultipleWritableMapValue> map) {
        this.map = map;
    }
}
