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
 *     11/13/2009-2.0 Guy Pelletier 
 *       - 293629: An attribute referenced from orm.xml is not recognized correctly
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.relationships;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Human {

    // Added for bug 293629 (marked as transient in mapping file)
    public boolean isNew() {
        return true;
    }
}
