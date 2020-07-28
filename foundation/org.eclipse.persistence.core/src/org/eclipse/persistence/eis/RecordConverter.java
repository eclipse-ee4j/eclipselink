/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
    javax.resource.cci.Record converterFromAdapterRecord(javax.resource.cci.Record record);

    javax.resource.cci.Record converterToAdapterRecord(javax.resource.cci.Record record);
}
