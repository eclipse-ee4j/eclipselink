/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Denise Smith - June 2012
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.inheritance.simple;

import javax.xml.bind.annotation.XmlValue;

public class Simple {
    @XmlValue 
    String foo;
    
    public boolean equals(Object compareObject){
    	if(compareObject instanceof Simple){
    		if(foo == null){
    			return ((Simple)compareObject).foo == null;
    		}
    		return foo.equals(((Simple)compareObject).foo);
    	}
    	return false;
    }
}
