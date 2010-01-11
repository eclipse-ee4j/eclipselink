/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports

// EclipseLink imports

/**
 * <p><b>INTERNAL:</b> <code>Attachment<code> is a helper object used by the
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
