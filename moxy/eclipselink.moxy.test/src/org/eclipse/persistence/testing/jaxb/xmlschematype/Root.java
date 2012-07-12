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
 *     desmith - July 2012 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import java.math.BigInteger;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Root {

    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger count;
    
    @XmlSchemaType(name = "blah")
    protected BigInteger count2;    
    
    @XmlSchemaType(name = "nonNegativeInteger")
    protected String count3;  
    
    @XmlSchemaType(name = "blah")
    protected String count4;
    
    public boolean equals(Object obj){
    	if(obj instanceof Root){
    	   return count.equals(((Root)obj).count) && count2.equals(((Root)obj).count2) && count3.equals(((Root)obj).count3) && count4.equals(((Root)obj).count4);
    	}
    	return false;
    }
}
