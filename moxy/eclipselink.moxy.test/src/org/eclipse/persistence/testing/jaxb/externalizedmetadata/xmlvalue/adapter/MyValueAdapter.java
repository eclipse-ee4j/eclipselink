/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - October 27/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapts from True/False to T/F.
 *
 */
public class MyValueAdapter extends XmlAdapter<String, Boolean> {

    public Boolean unmarshal(String v) throws Exception {
        return new Boolean(v.equals("T") ? "true" : "false");
    }

    public String marshal(Boolean v) throws Exception {
        return (v ? "T" : "F");
    }
}
