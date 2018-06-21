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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmllist;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Hockey {

    @XmlElement
    @XmlList
    public List<String> teams;

    public String toString()
    {
        return "List of teams: " + teams;
    }
    public boolean equals(Object object) {
        Hockey example3 = ((Hockey)object);
        if(example3.teams == null && teams == null) {
            return true;
        } else if(example3.teams == null || teams == null)
        {
            return false;
        }
        if(example3.teams.size() != teams.size()) {
            return false;
        }
        Iterator teams1 = example3.teams.iterator();
        Iterator teams2 = teams.iterator();
        while(teams1.hasNext()) {
            if(!(teams1.next().equals(teams2.next()))) {
                System.out.println("returning false");
                return false;
            }
        }
        return true;
    }
}
