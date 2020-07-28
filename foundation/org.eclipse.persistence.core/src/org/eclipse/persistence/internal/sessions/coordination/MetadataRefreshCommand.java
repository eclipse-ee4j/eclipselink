/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     01/19/2012-2.4 Chris Delahunt
//       - 368490: Add support for Metadata to be refreshed through RCM
package org.eclipse.persistence.internal.sessions.coordination;

import java.util.Map;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.coordination.Command;
import org.eclipse.persistence.sessions.coordination.MetadataRefreshListener;


/**
 * <p>
 * <b>Purpose</b>: A Command implementation used to signal JPA EntityManagerFactory to refresh its
 * metadata.
 * <p>
 * @author Chris Delahunt
 * @since Eclipselink 2.3
 */
public class MetadataRefreshCommand extends Command {
    protected Map properties;

    public MetadataRefreshCommand(Map properties) {
        super();
        this.properties = properties;
    }

    @Override
    public void executeWithSession(AbstractSession session) {
        MetadataRefreshListener listener = session.getRefreshMetadataListener();
        if (listener != null) {
            listener.triggerMetadataRefresh(properties);
        }
    }

}
