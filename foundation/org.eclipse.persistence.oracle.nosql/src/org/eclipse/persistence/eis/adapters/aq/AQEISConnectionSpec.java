/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.eis.adapters.aq;

import java.util.Properties;
import javax.resource.cci.*;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.internal.eis.adapters.aq.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Provides connection information to an Oracle AQ data source.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class AQEISConnectionSpec extends EISConnectionSpec {

    /** Connection spec properties. */
    public static String URL = "url";
    public static String DATASOURCE = "datasource";

    /**
     * PUBLIC:
     * Default constructor.
     */
    public AQEISConnectionSpec() {
        super();
    }

    /**
     * Connect with the specified properties and return the Connection.
     */
    public Connection connectToDataSource(EISAccessor accessor, Properties properties) throws DatabaseException, ValidationException {
        setConnectionFactory(new AQConnectionFactory());
        String user = (String)properties.get(USER);
        String password = getPasswordFromProperties(properties);
        String url = (String)properties.get(URL);
        String datasource = (String)properties.get(DATASOURCE);
        if (getConnectionSpec() == null) {
            AQConnectionSpec spec = new AQConnectionSpec(user, password, url);
            if (datasource != null) {
                spec.setDatasource(datasource);
            }
            setConnectionSpec(spec);
        }

        return super.connectToDataSource(accessor, properties);
    }
}
