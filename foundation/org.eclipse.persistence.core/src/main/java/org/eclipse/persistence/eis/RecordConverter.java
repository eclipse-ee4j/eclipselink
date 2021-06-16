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
package org.eclipse.persistence.eis;

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
    jakarta.resource.cci.Record converterFromAdapterRecord(jakarta.resource.cci.Record record);

    jakarta.resource.cci.Record converterToAdapterRecord(jakarta.resource.cci.Record record);
}
