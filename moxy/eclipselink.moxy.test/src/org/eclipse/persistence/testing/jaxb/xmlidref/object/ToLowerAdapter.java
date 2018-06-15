/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 19 July 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlidref.object;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ToLowerAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) throws Exception {
        return v.toLowerCase();
    }

    @Override
    public String marshal(String v) throws Exception {
        return v.toUpperCase();
    }

}
