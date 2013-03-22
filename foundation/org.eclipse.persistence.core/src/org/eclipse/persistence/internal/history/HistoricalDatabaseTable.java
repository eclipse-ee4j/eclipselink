/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.history;

import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.*;

/**
 * INTERNAL:
 * An impersonating database table is one that pretends to be another database
 * table, and whose true identity is revealed only when printed as SQL.
 * <p>
 * a.k.a HistoricalDatabaseTable, DisguisedDatabaseTable
 * <p>
 * More precisely, if a is impersonating b, (a.equals(b) == true) but
 * (a.getQualifiedName().equals(b.getQualifiedName()) == false).
 * <p>
 * This class is used in temporal versioning, where every update to one table
 * triggers an update to a nearly identical historical table.  This second update
 * is almost identical to the first, save that the table names are
 * different (i.e. EMPLOYEE -> EMPLOYEE_HIST).  It is much easier just to switch
 * the table names at the last minute, as database fields in the descriptors
 * and expressions have hardcoded table names.
 * @since OracleAS TopLink 10<i>g</i> (10.1.3)
 * @author Stephen McRitchie
 */
public class HistoricalDatabaseTable extends DatabaseTable {
    protected String historicalName;
    protected String historicalNameDelimited;

    public HistoricalDatabaseTable() {
        super();
    }

    public HistoricalDatabaseTable(String name, String qualifier) {
        super(name, qualifier);
    }

    public HistoricalDatabaseTable(String possiblyQualifiedName) {
        super(possiblyQualifiedName);
    }

    /**
     * Constructs a new database table which appears as <code>guise</code> but
     * in fact really is <code>identity</code>.
     */
    public HistoricalDatabaseTable(DatabaseTable source, DatabaseTable mirroring, DatasourcePlatform platform) {
        super(source.getName(), source.getTableQualifier());
        this.historicalName = mirroring.getQualifiedName();
        if(mirroring.shouldUseDelimiters()) {
            this.historicalNameDelimited = mirroring.getQualifiedNameDelimited(platform);
        }
    }

    public void setHistoricalName(String name) {
        if (name.startsWith(Helper.getDefaultStartDatabaseDelimiter()) && name.endsWith(Helper.getDefaultEndDatabaseDelimiter())) {
            this.historicalNameDelimited = name;
            this.historicalName = this.historicalNameDelimited.replaceAll(Helper.getDefaultStartDatabaseDelimiter(), "");
            this.historicalName = this.historicalName.replaceAll(Helper.getDefaultEndDatabaseDelimiter(), "");
        } else {
            this.historicalName = name ;
        }
    }

    public String getQualifiedName() {
        if (historicalName != null) {
            return historicalName;
        } else {
            return super.getQualifiedName();
        }
    }

    public String getQualifiedNameDelimited(DatasourcePlatform platform) {
        if (historicalNameDelimited != null) {
            return historicalNameDelimited;
        } else if(historicalName != null) {
            return historicalName;
        } else {
            return super.getQualifiedNameDelimited(platform);
        }
    }
}
