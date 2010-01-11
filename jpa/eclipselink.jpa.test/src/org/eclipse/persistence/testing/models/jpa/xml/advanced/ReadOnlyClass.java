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
 *     Guy Pelletier (Oracle), February 28, 2007 
 *        - New test file introduced for bug 217880.  
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

// This class is mapped as a read-only descriptor and used for testing.
public class ReadOnlyClass {
    public int id;
    
    public ReadOnlyClass() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
