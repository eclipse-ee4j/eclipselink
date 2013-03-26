/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke/tware - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public interface PersistenceContextFactory {
    
    public void close();
    
    public PersistenceContext get(String persistenceUnit, URI defaultURI, String version, Map<String, Object> initializationProperties);

    public Set<String> getPersistenceContextNames();
}
