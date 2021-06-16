/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.perf.moxy.referenceresolver;

import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
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
