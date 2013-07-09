/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.4 - initial implementation
 ******************************************************************************/
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
