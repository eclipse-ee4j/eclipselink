/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.nosql.adapters.sdk;

import jakarta.resource.cci.ResourceAdapterMetaData;
import org.eclipse.persistence.Version;

/**
 * Defines the meta-data for the Oracle NoSQL SDK adapter
 *
 * @author Radek Felcman
 * @since EclipseLink 4.0
 */
public class OracleNoSQLAdapterMetaData implements ResourceAdapterMetaData {

    /**
     * Default constructor.
     */
    public OracleNoSQLAdapterMetaData() {
    }

    @Override
    public String getAdapterName() {
        return "Oracle NoSQL SDK Adapter";
    }

    @Override
    public String getAdapterShortDescription() {
        return "Oracle NoSQL SDK JCA adapter.";
    }

    @Override
    public String getAdapterVendorName() {
        return "Oracle";
    }

    @Override
    public String getAdapterVersion() {
        return Version.getVersion();
    }

    @Override
    public String[] getInteractionSpecsSupported() {
        String[] specs = new String[1];
        specs[0] = "org.eclipse.persistence.internal.nosql.adapters.sdk.OracleNoSQLInteractionSpec";
        return specs;
    }

    @Override
    public String getSpecVersion() {
        return "1.5";
    }

    @Override
    public boolean supportsExecuteWithInputAndOutputRecord() {
        return true;
    }

    @Override
    public boolean supportsExecuteWithInputRecordOnly() {
        return true;
    }

    @Override
    public boolean supportsLocalTransactionDemarcation() {
        return true;
    }
}
