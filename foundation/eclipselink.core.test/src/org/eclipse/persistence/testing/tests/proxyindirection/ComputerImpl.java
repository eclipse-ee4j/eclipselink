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
package org.eclipse.persistence.testing.tests.proxyindirection;

public class ComputerImpl implements Computer {
    public int id;
	public String description;
	public String serialNumber;
    
    public ComputerImpl() {
        setSerialNumber(new String());
        setDescription(new String());
    }
    
    public int getId() {
        return id;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
	public String toString() 
	{
		return "Computer(" + description + "--" + serialNumber + ")" ;
	}
}
