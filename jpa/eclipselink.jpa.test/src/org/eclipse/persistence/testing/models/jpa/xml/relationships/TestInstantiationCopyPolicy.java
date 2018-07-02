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
//     tware - April 1 2008 - 1.0M6 - initial implementation

package org.eclipse.persistence.testing.models.jpa.xml.relationships;

import org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy;

/**
 * A copy policy designed to test copy policy annotations
 * By design, this is an empty subclass of InstantiationCopyPolicy since we will just
 * use this to test it is properly set.
 * @author tware
 *
 */
public class TestInstantiationCopyPolicy extends InstantiationCopyPolicy {
}
