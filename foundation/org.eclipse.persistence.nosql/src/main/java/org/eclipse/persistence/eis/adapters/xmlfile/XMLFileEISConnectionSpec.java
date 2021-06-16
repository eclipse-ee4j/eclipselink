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
package org.eclipse.persistence.eis.adapters.xmlfile;

import java.util.Properties;
import jakarta.resource.cci.*;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.*;
import org.eclipse.persistence.exceptions.*;

/**
 * Provides the behavior of instantiating a XML file adapter ConnectionSpec.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLFileEISConnectionSpec extends EISConnectionSpec {

    /** Connection spec properties. */
    public static final String DIRECTORY = "directory";

    /**
     * PUBLIC:
     * Default constructor.
     */
    public XMLFileEISConnectionSpec() {
        super();
    }

    /**
     * Connect with the specified properties and return the Connection.
     */
    @Override
    public Connection connectToDataSource(EISAccessor accessor, Properties properties) throws DatabaseException, ValidationException {
        setConnectionFactory(new XMLFileConnectionFactory());
        String directory = (String)properties.get(DIRECTORY);
        if (directory != null) {
            setConnectionSpec(new XMLFileConnectionSpec(directory));
        }

        return super.connectToDataSource(accessor, properties);
    }
}
