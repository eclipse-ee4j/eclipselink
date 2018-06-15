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
// dmccann - July 21/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.someotherpackage;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is used to verify that the package level adapter (DateAdapter) is
 * not applied to this class, as it is in a different package.
 *
 */

@XmlRootElement(name="someLameClass")
public class SomeLameClass {
    public Calendar cal;
}
