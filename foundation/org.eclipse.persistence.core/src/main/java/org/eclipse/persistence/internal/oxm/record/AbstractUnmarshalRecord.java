/*
 * Copyright (c) 2013, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm.record;

import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Unmarshaller;

/**
 * This class represents unmarshal record behaviour that is common to all XML
 * platforms.
 */
public interface AbstractUnmarshalRecord<
    ABSTRACT_SESSION extends CoreAbstractSession,
    FIELD extends CoreField,
    UNMARSHALLER extends Unmarshaller> extends XMLRecord<ABSTRACT_SESSION> {

    Object get(FIELD field);

    UNMARSHALLER getUnmarshaller();

    String resolveNamespacePrefix(String prefix);

}
