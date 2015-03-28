/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.perf.moxy.referenceresolver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "component")
@XmlAccessorType(XmlAccessType.FIELD)
/**
 * A value object class representing Component that has reference to Layer.
 */
public class Component {

    private String name;

    @XmlIDREF
    private Layer layer;

    public Component() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(final Layer layer) {
        this.layer = layer;
    }
}
