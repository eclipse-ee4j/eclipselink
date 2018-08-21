/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 07 October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.oxm;

/**
 * <p>
 * IDResolver can be subclassed to allow customization of the ID/IDREF processing of
 * XMLUnmarshaller.  A custom IDResolver can be specified on the Unmarshaller as follows:
 * </p>
 *
 * <p>
 * <code>
 * IDResolver customResolver = new MyIDResolver();
 * xmlUnmarshaller.setIDResolver(customResolver);
 * </code>
 * </p>
 *
 * @see XMLUnmarshaller
 * @since 2.3.3
 */
public abstract class IDResolver extends org.eclipse.persistence.internal.oxm.IDResolver {

}
