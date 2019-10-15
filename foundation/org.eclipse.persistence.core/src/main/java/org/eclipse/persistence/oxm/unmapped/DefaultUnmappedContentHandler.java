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
package org.eclipse.persistence.oxm.unmapped;

import org.eclipse.persistence.oxm.record.UnmarshalRecord;

/**
 * <p><b>Purpose:</b>Provide a default implementation of the UnmappedContentHandler
 * <p><b>Responsibilities:</b><ul>
 * <li>This handler swallows all SAX events corresponding to unmapped content so
 * when used unmapped content will not be processed.</ul>
 */
public class DefaultUnmappedContentHandler extends org.eclipse.persistence.internal.oxm.unmapped.DefaultUnmappedContentHandler<UnmarshalRecord> {
}
