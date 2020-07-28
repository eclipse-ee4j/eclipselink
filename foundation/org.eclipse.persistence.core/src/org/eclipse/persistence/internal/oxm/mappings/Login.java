/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm.mappings;

import org.eclipse.persistence.core.sessions.CoreLogin;
import org.eclipse.persistence.internal.core.databaseaccess.CorePlatform;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;

public interface Login<PLATFORM extends CorePlatform> extends CoreLogin<PLATFORM> {

    public DocumentPreservationPolicy getDocumentPreservationPolicy();

    public boolean hasEqualNamespaceResolvers();

    public void setDocumentPreservationPolicy(DocumentPreservationPolicy documentPreservationPolicy);

}
