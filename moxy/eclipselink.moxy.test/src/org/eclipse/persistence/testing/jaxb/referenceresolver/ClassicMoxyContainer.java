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

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A value object class that represents a typical OXM container, buffed in size to show performance differences during
 * marshalling and unmarshalling, specifically in reference resolving.
 */
@XmlRootElement(name = "componentsOnLayer")
@XmlType(propOrder = {"layers", "components"})
public class ClassicMoxyContainer {

    @XmlElementWrapper(name = "layers")
    public List<Layer> layers = new ArrayList<Layer>(100);

    @XmlElementWrapper(name = "components")
    public List<Component> components = new ArrayList<Component>(200000);

    public static ClassicMoxyContainer createHugeContainer() {
        ClassicMoxyContainer cmc = new ClassicMoxyContainer();

        for (long i = 0; i < 100; i++) {
            Layer layer = new Layer();
            layer.setLayerName("name__" + i);
            layer.setId(String.valueOf(i));
            cmc.layers.add(layer);
        }


        for (int i = 0; i < 200000; i++) {
            Component comp = new Component();
            comp.setLayer(cmc.layers.get(i % 100));
            comp.setName("Component__" + i);
            cmc.components.add(comp);
        }

        Collections.shuffle(cmc.layers); // Avoid sorted array.

        return cmc;
    }

    /**
     * Not used. It's here to conform to WS standards since we use @XmlType(propOrder={"layers", "components"}).
     */
    public String getLayers() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not used. It's here to conform to WS standards since we use @XmlType(propOrder={"layers", "components"}).
     */
    public String getComponents() {
        throw new UnsupportedOperationException();
    }
}
