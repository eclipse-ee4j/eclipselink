/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.eis;

import javax.resource.cci.*;

/**
 * <p>The <code>RecordConverter</code> interface allows conversion of an adapter 
 * specific record.  This can be used with the <code>EISPlatform</code> to allow 
 * user code to convert between the JCA-CCI Record used by the adapter and 
 * EclipeLink.  This can also be used to convert a proprietary adapter record 
 * format or contents into XML, Mapped or Indexed data.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public interface RecordConverter {
    Record converterFromAdapterRecord(Record record);

    Record converterToAdapterRecord(Record record);
}
