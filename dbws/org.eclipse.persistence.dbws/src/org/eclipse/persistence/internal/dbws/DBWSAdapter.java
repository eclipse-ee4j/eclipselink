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

package org.eclipse.persistence.internal.dbws;

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;

/**
 * <p><b>INTERNAL</b>: runtime implementation of EclipseLink Database Web Service (DBWS)
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class DBWSAdapter extends XRServiceAdapter {

    protected Schema extendedSchema;
    protected XMLAttachmentUnmarshaller currentAttachmentUnmarshaller;

    public Schema getExtendedSchema() {
      return extendedSchema;
    }

    public void setExtendedSchema(Schema extendedSchema) {
      this.extendedSchema = extendedSchema;
    }

    public XMLAttachmentUnmarshaller getCurrentAttachmentUnmarshaller() {
        return currentAttachmentUnmarshaller;
    }
    public void setCurrentAttachmentUnmarshaller(XMLAttachmentUnmarshaller currentAttachmentUnmarshaller) {
        this.currentAttachmentUnmarshaller = currentAttachmentUnmarshaller;
    }

}
