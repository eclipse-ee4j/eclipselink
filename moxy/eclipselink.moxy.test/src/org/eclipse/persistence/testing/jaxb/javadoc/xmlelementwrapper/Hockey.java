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
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementwrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "NHL-Team")
public class Hockey {

    @XmlElement (name = "team")
    @XmlElementWrapper (name = "canadian")
    public List<String> team;


    public List getTeam(){
        if (team == null){
            team = new ArrayList();
        }
        return team;
    }

    public String toString()
    {
        return "List of teams: " + team;
    }
    public boolean equals(Object object) {
        Hockey h = ((Hockey)object);
        if(h.team == null && team == null) {
            return true;
        } else if(h.team == null || team == null)
        {
            return false;
        }
        if(h.team.size() != team.size()) {
            return false;
        }
        Iterator teams1 = h.team.iterator();
        Iterator teams2 = team.iterator();
        while(teams1.hasNext()) {
            if(!(teams1.next().equals(teams2.next()))) {
                System.out.println("returning false");
                return false;
            }
        }
        return true;
    }
}
