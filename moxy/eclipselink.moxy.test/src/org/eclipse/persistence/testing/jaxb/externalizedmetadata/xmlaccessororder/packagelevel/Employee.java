/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlaccessororder.packagelevel;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee-type")
public class Employee {
    public String g;
    public String b;
    public String a;
    
    public boolean equals(Object obj){
    	if(obj instanceof Employee){
    		Employee empObj = (Employee)obj;
    		if(!a.equals(empObj.a)){
    			return false;
    		}
    		if(!b.equals(empObj.b)){
    			return false;
    		}
    		if(!g.equals(empObj.g)){
    			return false;
    		}
    		return true;
    	}
    	return false;
    }
}
