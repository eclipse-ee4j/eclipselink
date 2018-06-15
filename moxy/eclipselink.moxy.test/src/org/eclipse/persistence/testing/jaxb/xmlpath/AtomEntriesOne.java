/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 05 November 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlpath;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name = "testObject", namespace = "http://www.w3.org/2005/Atom")
public class AtomEntriesOne {

    @XmlPath("atom:entry/atom:content/atom:entry[@type='application/atom+xml;type=entry;apitype=foo']")
    public ArrayList<Entry> entries = new ArrayList<Entry>();

}
