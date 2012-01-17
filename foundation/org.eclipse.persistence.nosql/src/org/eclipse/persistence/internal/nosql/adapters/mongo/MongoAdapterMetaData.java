/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

    public String getAdapterName() {
        return "Mongo Adapter";
    }

    public String getAdapterShortDescription() {
        return "Mongo JCA adapter.";
    }

    public String getAdapterVendorName() {
        return "Eclipse";
    }

    public String getAdapterVersion() {
        return Version.getVersion();
    }

    public String[] getInteractionSpecsSupported() {
        String[] specs = new String[2];
        specs[0] = "org.eclipse.persistence.internal.eis.adapters.mongo.MongoInteractionSpec";
        return specs;
    }

    public String getSpecVersion() {
        return "1.5";
    }

    public boolean supportsExecuteWithInputAndOutputRecord() {
        return true;
    }

    public boolean supportsExecuteWithInputRecordOnly() {
        return true;
    }

    public boolean supportsLocalTransactionDemarcation() {
        return true;
    }
}
