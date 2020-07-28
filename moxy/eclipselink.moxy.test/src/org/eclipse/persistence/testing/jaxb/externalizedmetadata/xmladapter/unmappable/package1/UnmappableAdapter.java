/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package1;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package2.Unmappable;;

public class UnmappableAdapter extends XmlAdapter<String, Unmappable> {

    @Override
    public String marshal(Unmappable v) throws Exception {
        return v != null ? v.getProp() : null;
    }

    @Override
    public Unmappable unmarshal(String v) throws Exception {
        return Unmappable.getInstance(v);
    }
}
