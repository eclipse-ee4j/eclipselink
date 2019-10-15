/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
package org.eclipse.persistence.testing.models.jpa.xml.advanced.additionalcriteria;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

public class Nut {
    public Integer id;
    public Integer size;
    public String color;

    public String getColor() {
        return color;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSize() {
        return size;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

}
