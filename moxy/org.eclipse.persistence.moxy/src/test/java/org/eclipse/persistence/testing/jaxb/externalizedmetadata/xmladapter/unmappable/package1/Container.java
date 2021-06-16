/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package1;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package2.Unmappable;


public class Container {

    private Unmappable containerProperty;

    public Unmappable getContainerProperty() {
        return containerProperty;
    }

    public void setContainerProperty(Unmappable containerProperty) {
        this.containerProperty = containerProperty;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Container) && ((Container)obj).getContainerProperty().equals(this.containerProperty);
    }
}
