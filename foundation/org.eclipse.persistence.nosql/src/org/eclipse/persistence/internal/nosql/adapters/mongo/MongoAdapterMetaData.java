/*******************************************************************************
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.nosql.adapters.mongo;

import javax.resource.cci.*;
import org.eclipse.persistence.Version;

/**
 * Defines the meta-data for the Mongo adapter
 *
 * @author James
 * @since EclipseLink 2.4
 */
public class MongoAdapterMetaData implements ResourceAdapterMetaData {

    /**
     * Default constructor.
     */
    public MongoAdapterMetaData() {
    }

    @Override
    public String getAdapterName() {
        return "Mongo Adapter";
    }

    @Override
    public String getAdapterShortDescription() {
        return "Mongo JCA adapter.";
    }

    @Override
    public String getAdapterVendorName() {
        return "Eclipse";
    }

    @Override
    public String getAdapterVersion() {
        return Version.getVersion();
    }

    @Override
    public String[] getInteractionSpecsSupported() {
        String[] specs = new String[2];
        specs[0] = "org.eclipse.persistence.internal.eis.adapters.mongo.MongoInteractionSpec";
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
