/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="root")
public class ListenerRootObject {

    public String name;
    
    public ListenerChildObject child;

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
