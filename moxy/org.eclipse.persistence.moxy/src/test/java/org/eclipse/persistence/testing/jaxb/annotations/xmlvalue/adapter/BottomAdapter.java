/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class BottomAdapter extends XmlAdapter<BottomAdapted, Bottom> {

    @Override
    public Bottom unmarshal(BottomAdapted bottomAdapted) throws Exception {
        if(null == bottomAdapted) {
            return null;
        }
        return new Bottom(bottomAdapted.value);
    }

    @Override
    public BottomAdapted marshal(Bottom bottom) throws Exception {
        if(null == bottom) {
            return null;
        }
        BottomAdapted bottomAdapted = new BottomAdapted();
        bottomAdapted.value = bottom.value;
        return bottomAdapted;
    }

}
