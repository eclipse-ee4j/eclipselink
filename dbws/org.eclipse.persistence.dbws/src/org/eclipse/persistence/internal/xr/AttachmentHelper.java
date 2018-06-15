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
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports
import javax.activation.DataHandler;

// EclipseLink imports
import org.eclipse.persistence.internal.oxm.ByteArrayDataSource;

/**
* <p><b>INTERNAL:</b> <code>AttachmentHelper</code> is a helper object used by
* the {@link QueryOperation} to handle binary attachments.
*
* @author Mike Norman - michael.norman@oracle.com
* @since EclipseLink 1.x
*/
public class AttachmentHelper  {

    /**
    * <p><b>INTERNAL:</b> build a {@link DataHandler} backed by a
    * {@link ByteArrayDataSource}
    */
    public static Object buildAttachmentHandler(byte[] bytes, String mimeType) {
        return new DataHandler(new ByteArrayDataSource(bytes, mimeType));
    }
}
