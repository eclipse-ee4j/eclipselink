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
//     James Sutherland - initial impl
package org.eclipse.persistence.testing.models.jpa.relationships;

import java.util.HashSet;

/**
 * This custom collection class is used to test custom collection support.
 * @author James Sutherland
 */
public class CustomerCollection<V> extends HashSet<V> {
}
