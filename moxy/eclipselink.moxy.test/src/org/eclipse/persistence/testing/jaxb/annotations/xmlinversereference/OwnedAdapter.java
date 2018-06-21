/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - Matt MacIvor - 9/20/2012 - 2.4.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OwnedAdapter extends XmlAdapter<Adapted, Owned>{

    @Override
    public Adapted marshal(Owned arg0) throws Exception {
        return new Adapted();
    }

    @Override
    public Owned unmarshal(Adapted arg0) throws Exception {
        Owned owned = new Owned();
        owned.owner = arg0.owner;
        return owned;
    }


}
