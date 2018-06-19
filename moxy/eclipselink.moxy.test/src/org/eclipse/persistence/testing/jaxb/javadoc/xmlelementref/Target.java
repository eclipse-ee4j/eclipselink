/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementref;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="target")
public class Target {

    @XmlElementRef
    public List<Task> tasks;

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Target)) {
            return false;
        }
        Target t = (Target) obj;

        return (t.tasks.size() == (this.tasks.size()) && t.tasks.get(0).equals(this.tasks.get(0)) && t.tasks.get(1).equals(this.tasks.get(1)));
    }
}
