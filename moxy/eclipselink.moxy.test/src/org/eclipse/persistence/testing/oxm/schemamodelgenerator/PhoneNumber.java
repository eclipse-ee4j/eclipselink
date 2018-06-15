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
// dmccann - Mar 2/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.schemamodelgenerator;

import java.util.Collection;
import java.util.Map;

public class PhoneNumber {
    public String number;         // XMLDirectMapping
    public Map thing;             // XMLAnyAttributeMapping
    public PhoneNumberType type;  // XMLDirectMapping
    public PhoneNumber() {}
}
