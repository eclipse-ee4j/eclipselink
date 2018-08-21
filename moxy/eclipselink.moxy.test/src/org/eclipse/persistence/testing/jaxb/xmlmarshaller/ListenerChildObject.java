/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;

public class ListenerChildObject {

    @XmlTransient
    public Marshaller beforeMarshalMarshaller;

    @XmlTransient
    public Marshaller afterMarshalMarshaller;

    private void beforeMarshal(Marshaller marshaller) {
        this.beforeMarshalMarshaller = marshaller;
    }

    private void afterMarshal(Marshaller marshaller) {
        this.afterMarshalMarshaller = marshaller;
    }

    @XmlTransient
    public Unmarshaller beforeUnmarshalUnmarshaller;

    @XmlTransient
    public Object beforeUnmarshalParent;

    @XmlTransient
    public Unmarshaller afterUnmarshalUnmarshaller;

    @XmlTransient
    public Object afterUnmarshalParent;

    private void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {
        beforeUnmarshalUnmarshaller = unmarshaller;
        beforeUnmarshalParent = parent;
    }

    private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        afterUnmarshalUnmarshaller = unmarshaller;
        afterUnmarshalParent = parent;
    }

}
