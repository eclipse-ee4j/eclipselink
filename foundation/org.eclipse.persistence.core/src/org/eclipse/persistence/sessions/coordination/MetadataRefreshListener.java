/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/19/2012-2.4 Chris Delahunt
 *       - 368490: Add support for Metadata to be refreshed through RCM 
 ******************************************************************************/
package org.eclipse.persistence.sessions.coordination;

import java.util.Map;

public interface MetadataRefreshListener {
    public void triggerMetadataRefresh(Map properties);
}
