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
package org.eclipse.persistence.eis.adapters.aq;

import java.util.Properties;
import jakarta.resource.cci.*;
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
    @Override
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
