/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.dbws;

// Javase imports

// Java extension imports

// TopLink imports
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.xr.XRServiceAdapter;

/**
 * <p><b>INTERNAL</b>: runtime implementation of TopLink Database Web Service (DBWS)
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public class DBWSAdapter extends XRServiceAdapter {
  
  protected Schema extendedSchema;

  public Schema getExtendedSchema() {
    return extendedSchema;
  }

  public void setExtendedSchema(Schema extendedSchema) {
    this.extendedSchema = extendedSchema;
  }

}
