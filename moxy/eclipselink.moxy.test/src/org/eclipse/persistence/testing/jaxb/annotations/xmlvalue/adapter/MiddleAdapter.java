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

public class MiddleAdapter extends XmlAdapter<MiddleAdapted, Middle> {

    @Override
    public Middle unmarshal(MiddleAdapted middleAdapted) throws Exception {
        if(null == middleAdapted) {
            return null;
        }
        return new Middle(middleAdapted.value);
    }

    @Override
    public MiddleAdapted marshal(Middle middle) throws Exception {
        if(null == middle) {
            return null;
        }
        MiddleAdapted middleAdapted = new MiddleAdapted();
        middleAdapted.value = middle.value;
        return middleAdapted;
    }

}
