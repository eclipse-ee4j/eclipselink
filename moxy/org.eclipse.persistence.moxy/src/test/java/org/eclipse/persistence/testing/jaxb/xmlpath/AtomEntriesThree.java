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
//  - rbarkhouse - 05 November 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlpath;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name = "testObject", namespace = "http://www.w3.org/2005/Atom")
public class AtomEntriesThree {

    @XmlPath("atom:entry/atom:content/atom:entry")
    public ArrayList<Entry> entries = new ArrayList<Entry>();

}
