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
 *     01/12/2009-1.1 Daniel Lo
 *       - 247041: Null element inserted in the ArrayList 
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving;

import java.util.ArrayList;
import java.util.List;


public interface Equipment {

    public long getEntityId();

    public String getId();
    public void setId( String id );
    
    public List<Port> getPorts();
    public void setPorts( ArrayList<org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.Port> ports );

	public Port removePort(int i);
	public void addPort(Port p3);
}
