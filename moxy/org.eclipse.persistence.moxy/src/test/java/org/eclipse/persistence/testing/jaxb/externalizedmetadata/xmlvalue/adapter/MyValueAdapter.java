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
// dmccann - October 27/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlvalue.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

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
