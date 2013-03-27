/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 9/20/2012 - 2.4 - Initial implementation
 ******************************************************************************/
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