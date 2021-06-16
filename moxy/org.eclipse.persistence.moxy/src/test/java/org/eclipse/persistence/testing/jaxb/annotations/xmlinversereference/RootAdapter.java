/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 9/20/2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

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
