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
//     17/04/2011 - 2.3 Vikram Bhatia
//     342922: Unwanted insert statement generated when using ElementCollection
//     with lazy loading.
package org.eclipse.persistence.testing.models.jpa.advanced.entities;

import java.util.HashMap;
import java.util.Map;

public class SimpleNature {
    public static final String[] PERSONALITY =
        {"Active", "Reactive", "Aggressive", "Optimistic", "Pessimistic", "Lazy"};
}
