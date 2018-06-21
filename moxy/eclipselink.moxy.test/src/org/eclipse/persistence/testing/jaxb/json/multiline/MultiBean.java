/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
//    Martin Grebac - 2.6
package org.eclipse.persistence.testing.jaxb.json.multiline;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@SuppressWarnings("EqualsAndHashcode")
public class MultiBean {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof MultiBean) &&
                (this.getName().equals(((MultiBean)obj).getName())));
    }
}
