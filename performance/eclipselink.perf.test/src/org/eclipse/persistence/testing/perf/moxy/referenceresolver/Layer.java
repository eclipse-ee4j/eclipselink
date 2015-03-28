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
