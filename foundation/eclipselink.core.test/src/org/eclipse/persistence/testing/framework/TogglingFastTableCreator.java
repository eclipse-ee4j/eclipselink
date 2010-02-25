/*******************************************************************************
 * Copyright (c) 2010 Dies Koper (Fujitsu) All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Created Feb 19, 2010 - Dies Koper (Fujitsu)
 *        bug 288715: Drop Table Restrictions: "table locked" errors when dropping
 *                    tables in several Core and many JPA LRG tests on Symfoware.
 ******************************************************************************/
package org.eclipse.persistence.testing.framework;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableCreator;

/**
 * Many JPA and a few Core tests use the same tables names, so at the start of
 * the tests tables are dropped and recreated. If the tables were created in a
 * previous test and the connection used to create it is still open, Symfoware
 * complains that the tables are locked when it tries to drop them.
 * <p/>
 * 
 * This class sets a flag before the subsequent recreation of the tables to
 * delete the rows instead. The first time it is called it does not set the
 * flag, to allow the tables to be initially created.
 * <p/>
 * 
 * To enable this functionality, the system property
 * "eclipselink.test.toggle-fast-table-creator" needs to be set to "true".
 * <p/>
 * 
 * This class should be positioned between the test's table creator class and
 * TableCreator by making the's test table creator extend this class.<br/>
 * 
 * @author Dies Koper
 * 
 */
public class TogglingFastTableCreator extends TableCreator {
    protected static Set fastTableCreators = new HashSet();
    protected static boolean useFastTableCreatorAfterInitialCreate = Boolean
            .getBoolean("eclipselink.test.toggle-fast-table-creator");

    /**
     * Delegates to super's constructor.
     */
    public TogglingFastTableCreator() {
        super();
    }

    /**
     * Delegates to super's constructor.
     */
    public TogglingFastTableCreator(Vector tableDefinitions) {
        super(tableDefinitions);
    }

    @Override
    public void replaceTables(DatabaseSession session) {
        // on Symfoware, to avoid table locking issues only the first invocation
        // of an instance of this class (drops & re-)creates the tables.
        boolean isFirstCreate = !isFastTableCreator();
        boolean orig_FAST_TABLE_CREATOR = SchemaManager.FAST_TABLE_CREATOR;
        session.getSessionLog().log(SessionLog.FINEST, "DEBUG: " + this.getClass().getName()
                + " - isFirstCreate: " + isFirstCreate);
        session.getSessionLog().log(SessionLog.FINEST, "DEBUG: Current fastTableCreators: "
                + fastTableCreators);

        session.getSessionLog().log(SessionLog.FINEST, "DEBUG: useFastTableCreatorAfterInitialCreate: "
                + useFastTableCreatorAfterInitialCreate);
        if (useFastTableCreatorAfterInitialCreate && !isFirstCreate) {
            SchemaManager.FAST_TABLE_CREATOR = true;
            session.getSessionLog().log(SessionLog.FINEST, "DEBUG: " + this.getClass().getName()
                    + " - toggling true");
        }
        try {
            super.replaceTables(session);
        } finally {
            // reset to original value
            if (useFastTableCreatorAfterInitialCreate && !isFirstCreate) {
                SchemaManager.FAST_TABLE_CREATOR = orig_FAST_TABLE_CREATOR;
            }
        }

        // next time just delete the rows instead.
        if (useFastTableCreatorAfterInitialCreate) {
            setFastTableCreator();
            session.getSessionLog().log(SessionLog.FINEST, "DEBUG: " + this.getClass().getName()
                    + " added to fastTableCreators");
        }
    }

    public boolean resetFastTableCreator() {
        AbstractSessionLog.getLog().log(SessionLog.FINEST, "DEBUG: removing table creator: "
                + this.getClass().getName());
        return fastTableCreators.remove(this.getClass().getName());
    }

    public boolean setFastTableCreator() {
        AbstractSessionLog.getLog().log(SessionLog.FINEST, "DEBUG: adding table creator: "
                + this.getClass().getName());
        return fastTableCreators.add(this.getClass().getName());
    }

    public boolean isFastTableCreator() {
        return fastTableCreators.contains(this.getClass().getName());
    }
}
