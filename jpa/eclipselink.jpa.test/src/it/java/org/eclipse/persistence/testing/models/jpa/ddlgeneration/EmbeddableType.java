/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     12/30/2010-2.3 Guy Pelletier submitted for Paul Fullbright
//       - 312253: Descriptor exception with Embeddable on DDL gen
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import javax.persistence.Embeddable;

@Embeddable
public class EmbeddableType {
    // Note: The owning entity has property access. Since embeddable collection
    // mappings extract the source table from the source fields, this embeddable
    // would not return any mappings eventually causing a validation exception
    // at descriptor initialization time since the aggregate to source fields
    // map is empty (built from the mappings).
    private String basic;
}
