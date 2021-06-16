/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.events;

import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlTransient;

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
