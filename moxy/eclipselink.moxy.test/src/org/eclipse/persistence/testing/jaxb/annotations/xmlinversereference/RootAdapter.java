/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 9/20/2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RootAdapter<T extends Linkable> extends XmlAdapter<T, T> {

    @Override
    public T marshal(T v) throws Exception {
        if (v != null) {
            v.setLink("MARSHALLED LINK");
        }
        return v;
    }

    @Override
    public T unmarshal(T v) throws Exception {
        if (v != null) {
            v.setLink("UNMARSHALLED LINK");
        }
        return v;
    }

}
