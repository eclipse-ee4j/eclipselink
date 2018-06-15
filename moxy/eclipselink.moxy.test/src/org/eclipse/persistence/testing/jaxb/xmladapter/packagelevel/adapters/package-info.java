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
// Denise Smith - September 10 /2009
@XmlJavaTypeAdapters(@XmlJavaTypeAdapter(value=org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel.adapters.ClassAtoClassBAdapter.class, type=ClassB.class))
package org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel.adapters;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
