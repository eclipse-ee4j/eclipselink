/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlvalue.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

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
