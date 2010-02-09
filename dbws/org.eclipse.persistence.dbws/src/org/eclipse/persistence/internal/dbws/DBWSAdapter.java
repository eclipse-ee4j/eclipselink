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
