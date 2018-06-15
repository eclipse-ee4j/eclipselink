/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.jaxb.referenceresolver;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "layer")
@XmlAccessorType(XmlAccessType.FIELD)
/**
 * A value object class representing Layer that is referenced by Component.
 */
public class Layer {

    @XmlID
    @XmlAttribute
    private String id;

    private String layerName;

    @XmlAttribute
    private boolean isTopLayer;


    public Layer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isTopLayer() {
        return isTopLayer;
    }

    public void setTopLayer(boolean isTopLayer) {
        this.isTopLayer = isTopLayer;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }
}
