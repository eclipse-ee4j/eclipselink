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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementwrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import jakarta.xml.bind.annotation.XmlRootElement;

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
