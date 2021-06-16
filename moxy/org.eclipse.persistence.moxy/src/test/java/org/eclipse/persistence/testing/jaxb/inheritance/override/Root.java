/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.inheritance.override;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {

    protected Subclass subclass;

    public Subclass getSubclass() {
        return subclass;
    }

    public void setSubclass(Subclass subclass) {
        this.subclass = subclass;
    }

    @Override
    public boolean equals(Object obj) {
        return this.subclass.equals(((Root)obj).getSubclass());
    }

    @Override
    public int hashCode() {
        return subclass != null ? subclass.hashCode() : 0;
    }
}
