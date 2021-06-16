/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
