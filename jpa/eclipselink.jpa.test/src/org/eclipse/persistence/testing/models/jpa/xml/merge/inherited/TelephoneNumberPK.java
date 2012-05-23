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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

public class TelephoneNumberPK  {
	public String type;
    protected String number;
    private String areaCode;

    public TelephoneNumberPK() {}
    
	public String getAreaCode() { 
        return areaCode; 
    }
    
	public String getNumber() { 
        return number; 
    }

	public String getType() { 
        return type; 
    }
    
    public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
    
	public void setNumber(String number) { 
        this.number = number; 
    }

	public void setType(String type) {
		this.type = type;
	}
    
    public boolean equals(Object anotherTelephoneNumberPK) {
        if (anotherTelephoneNumberPK.getClass() != TelephoneNumberPK.class) {
            return false;
        }
        
        TelephoneNumberPK telephoneNumberPK = (TelephoneNumberPK) anotherTelephoneNumberPK;
        
        return (
            telephoneNumberPK.getAreaCode().equals(getAreaCode()) && 
            telephoneNumberPK.getNumber().equals(getNumber()) &&
            telephoneNumberPK.getType().equals(getType())
        );

    }
}
