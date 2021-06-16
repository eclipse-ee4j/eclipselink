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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.math.BigDecimal;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;


public class Weather implements java.io.Serializable {
    public long id;
    public String stormPattern;

    /**
     * Weather constructor comment.
     */
    public Weather() {
        super();
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(Weather.class);
        descriptor.setTableName("U_WEATH");
        descriptor.setPrimaryKeyFieldName("ID");
        descriptor.setSequenceNumberName("SEQ");
        descriptor.setSequenceNumberFieldName("ID");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("id", "ID");
        descriptor.addDirectMapping("stormPattern", "STORM_PAT");
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.NoIdentityMap.class);
        descriptor.getQueryManager().checkDatabaseForDoesExist();

        return descriptor;
    }

    public static Weather example1() {
        Weather weather = new Weather();
        weather.setStormPattern("sunny");
        return weather;
    }

    public static Weather example2() {
        Weather weather = new Weather();
        weather.setStormPattern("raining");
        return weather;
    }

    public String getStormPattern() {
        return stormPattern;
    }

    /**
     * Weather constructor comment.
     */
    public void setStormPattern(String stormPattern) {
        this.stormPattern = stormPattern;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("U_WEATH");

        definition.addIdentityField("ID", BigDecimal.class, 15);
        definition.addField("STORM_PAT", String.class, 50);

        return definition;
    }
}
