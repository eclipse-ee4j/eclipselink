/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
//     EclipseLink contributors - initial API and implementation
package org.eclipse.persistence.internal.security;

import example.outside.Outside;
import org.eclipse.persistence.internal.sessions.coordination.MetadataRefreshCommand;
import org.eclipse.persistence.sessions.serializers.JavaSerializer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EclipseLinkSerialFilterTest {

    @Test
    void remoteDeserializeAllowsEclipseLinkCommand() {
        MetadataRefreshCommand command = new MetadataRefreshCommand(new HashMap<>());
        byte[] bytes = (byte[]) JavaSerializer.instance.serialize(command, null);
        Object read = JavaSerializer.deserializeRemote(JavaSerializer.instance, bytes, null);
        assertInstanceOf(MetadataRefreshCommand.class, read);
    }

    @Test
    void remoteDeserializeRejectsApplicationClass() {
        byte[] bytes = (byte[]) JavaSerializer.instance.serialize(new Outside(), null);
        assertThrows(RuntimeException.class,
                () -> JavaSerializer.deserializeRemote(JavaSerializer.instance, bytes, null));
    }

    @Test
    void columnDeserializeStaysUnfiltered() {
        byte[] bytes = (byte[]) JavaSerializer.instance.serialize(new Outside(), null);
        Object read = JavaSerializer.instance.deserialize(bytes, null);
        assertInstanceOf(Outside.class, read);
    }

}
