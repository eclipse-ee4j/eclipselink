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
