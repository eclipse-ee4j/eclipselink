/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      dclarke/tware - initial implementation
package org.eclipse.persistence.jpa.rs;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public interface PersistenceContextFactory {

    public void close();

    public void closePersistenceContext(String persistenceUnit);

    public PersistenceContext get(String persistenceUnit, URI defaultURI, String version, Map<String, Object> initializationProperties);

    public Set<String> getPersistenceContextNames();
}
