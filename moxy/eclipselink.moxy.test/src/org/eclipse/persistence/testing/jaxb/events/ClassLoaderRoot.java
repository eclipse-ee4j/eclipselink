/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.events;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class ClassLoaderRoot {

    public int beforeUnmarshalCalled = 0;
    public int afterUnmarshalCalled = 0;
    public int beforeMarshalCalled = 0;
    public int afterMarshalCalled = 0;

    void afterMarshal(Marshaller m) {
        afterMarshalCalled++;
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        afterUnmarshalCalled++;
    }

    void beforeMarshal(Marshaller m) {
        beforeMarshalCalled++;
    }

    void beforeUnmarshal(Unmarshaller u, Object parent) {
        beforeUnmarshalCalled++;
    }

}
