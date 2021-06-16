/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.oxm;

import org.eclipse.persistence.internal.oxm.Root;

/**
 * <p>XMLRoot is used to hold an Object along with the corresponding QName and some other related information.
 * Typically this is used when the object is marshalled/unmarshalled to a QName other than
 * the defaultRootElement set on the XMLDescriptor.</p>
 *
 * <p>XMLRoot objects can be returned from XMLUnmarshaller unmarshal operations and
 * can be given to XMLMarshaller.marshal operations.  They may also be in values
 * return by XMLAnyCollectionMappings and XMLAnyObjectMappings.</p>
 */
public class XMLRoot extends Root {

}
