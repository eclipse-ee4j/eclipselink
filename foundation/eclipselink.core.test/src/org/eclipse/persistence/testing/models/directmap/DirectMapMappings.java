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
package org.eclipse.persistence.testing.models.directmap;

import java.util.*;

/**
 * Object that will hold our mappings.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date March 04, 2003
 */
public class DirectMapMappings {
    public int id;
    public Hashtable directMap;
    public Hashtable directMapForBatchRead;
    public Hashtable indirectionDirectMap;
    public Hashtable blobDirectMap;
    public HashMap directHashMap;

    public DirectMapMappings() {
        directMap = new Hashtable();
        directMapForBatchRead = new Hashtable();
        indirectionDirectMap = new Hashtable();
        blobDirectMap = new Hashtable();
        directHashMap = new HashMap();
    }
}
