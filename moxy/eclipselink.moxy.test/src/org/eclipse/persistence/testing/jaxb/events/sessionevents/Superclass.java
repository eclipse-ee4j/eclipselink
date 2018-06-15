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
// dmccann - April 7/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.events.sessionevents;

/**
 * Since Subclass extends this class, the static creation of Subclass below
 * will result in infinite recursive constructor calls.
 */
public class Superclass {
    Subclass sc = new Subclass();
}
