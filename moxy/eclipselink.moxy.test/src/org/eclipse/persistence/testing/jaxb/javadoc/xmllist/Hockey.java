/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/ 
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
