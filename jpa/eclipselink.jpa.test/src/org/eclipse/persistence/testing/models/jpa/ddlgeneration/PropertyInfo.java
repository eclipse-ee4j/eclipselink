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
//     01/22/2010-2.0.1 Karen Moore
//       - 294361: incorrect generated table for element collection attribute overrides
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.math.BigDecimal;

import javax.persistence.Embeddable;

@Embeddable
public class PropertyInfo {
    public Integer parcelNumber;
    public Integer size;
    public BigDecimal tax;
}

