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

// EclipseLink imports

/**
 * <p><b>INTERNAL:</b> <code>Attachment</code> is a helper object used by the
 * {@link QueryOperation} to handle binary attachments.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class Attachment {

    protected String mimeType;

    /**
     * <p><b>INTERNAL:</b> get the mime-type setting for the binary attachment
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * <p><b>INTERNAL:</b> set the mime-type setting for the binary attachement
     * @param mimeType mime-type setting for the binary attachement
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
