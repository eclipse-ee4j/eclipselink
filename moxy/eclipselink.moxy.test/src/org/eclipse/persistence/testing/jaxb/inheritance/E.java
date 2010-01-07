/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.inheritance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "e-type")
@XmlAccessorType(XmlAccessType.FIELD)
public class E extends D {
	private int eee;

	public int getEee() {
		return eee;
	}

	public void setEee(int eee) {
		this.eee = eee;
	}
	
	public boolean equals(Object obj) {
        if(!(obj instanceof E)) {
	        return false;
	    }
	    E objE = (E)obj;
	    if(getEee() != objE.getEee()){
	    	return false;
	    }
	    if(getDdd() != objE.getDdd()){
	    	return false;
	    }
	    if(getCcc() != objE.getCcc()){
	    	return false;
	    }
	    if(getBbb() != objE.getBbb()){
	    	return false;
	    }
	    if(getAaa() != objE.getAaa()){
	    	return false;
	    }
	    
	    return true;
    }
}
