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
package org.eclipse.persistence.testing.tests.remote;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.indirection.*;

public class Master implements Serializable// maps to a table MASTER 
{ 

    private ValueHolderInterface slaves; // use basic indirection for one-to-many mapping 
    private int primaryKey; 
		private boolean slavesSerialized = false; // unmapped variable added to keep track or serialization for bug 3145211

    public Master(){ 
       slaves = new ValueHolder(new Vector()); 
    } 
    
    public int getId(){
        return primaryKey;
    }
    
    public void setId(int id){
        this.primaryKey = id;
    }
    
    public void addSlave(Slave slave){
        getSlaves().addElement(slave);
				slave.setMaster(this);
    }

    public Vector getSlaves() { 
        return (Vector)slaves.getValue(); 
    } 

		public void setSlavesSerialized(boolean wasSerialized){
			this.slavesSerialized = wasSerialized;
		}

		public boolean slavesSerialized(){
			return slavesSerialized;
		}


} 
