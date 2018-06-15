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
//     Denise Smith - October 18, 2012
package org.eclipse.persistence.testing.jaxb.annotations.xmltransient;

import java.util.List;

public class ObjectWithTransient {
    public String testString;
    public TransientClass transientThing;
    public List<TransientClass> transientThings;
}
