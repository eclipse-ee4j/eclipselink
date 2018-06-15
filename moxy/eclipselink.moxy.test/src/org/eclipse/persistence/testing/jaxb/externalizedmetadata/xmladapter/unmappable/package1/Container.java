/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
