/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/19/2012-2.4 Chris Delahunt
//       - 368490: Add support for Metadata to be refreshed through RCM
package org.eclipse.persistence.sessions.coordination;

import java.util.Map;

public interface MetadataRefreshListener {
    public void triggerMetadataRefresh(Map properties);
}
