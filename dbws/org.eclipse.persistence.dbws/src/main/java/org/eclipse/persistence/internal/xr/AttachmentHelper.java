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

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports
import jakarta.activation.DataHandler;

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
