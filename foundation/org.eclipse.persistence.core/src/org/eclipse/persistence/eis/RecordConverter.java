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
