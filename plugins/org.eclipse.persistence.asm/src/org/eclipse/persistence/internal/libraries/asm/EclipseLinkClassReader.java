/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Oracle - initial API and implementation
package org.eclipse.persistence.internal.libraries.asm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EclipseLinkClassReader extends ClassReader {

    public EclipseLinkClassReader(InputStream stream) throws IOException {
        this(getStream(stream));
    }

    public EclipseLinkClassReader(final byte[] classFile) {
        this(classFile, 0, classFile.length);
    }

    public EclipseLinkClassReader(final byte[] classFileBuffer, final int classFileOffset, final int classFileLength) {
        super(classFileBuffer, classFileOffset, /* checkClassVersion = */ false);
    }

    // not closing stream is intentional here
    private static byte[] getStream(final InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            outputStream.write(data, 0, bytesRead);
        }
        outputStream.flush();
        return outputStream.toByteArray();
    }

}
