/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.platformsplugin.ui;

import org.eclipse.persistence.tools.workbench.framework.resources.AbstractIconResourceFileNameMap;

/**
 * map icon keys to resource files containing the icons
 */
final class PlatformsPluginIconResourceFileNameMap extends AbstractIconResourceFileNameMap {

    private static final String[][] entries = {

        {"NEW_DATABASE_PLATFORM_REPOSITORY",        "basic/database/DataSources.gif"},

        {"DATABASE_PLATFORM_REPOSITORY",                "basic/database/DataSources.gif"},
        {"RENAME_DATABASE_PLATFORM_REPOSITORY",    "basic/misc/Rename.gif"},
        {"ADD_DATABASE_PLATFORM",                            "basic/database/Database.new.gif"},

        {"DATABASE_PLATFORM",                    "basic/database/Database.gif"},
        {"RENAME_DATABASE_PLATFORM",        "basic/misc/Rename.gif"},
        {"DELETE_DATABASE_PLATFORM",        "basic/database/Database.remove.gif"},
        {"CLONE_DATABASE_PLATFORM",        "basic/edit/Copy.gif"},
        {"ADD_DATABASE_TYPE",                    "basic/database/Table.add.gif"},

        {"DATABASE_TYPE",                    "basic/database/Table.gif"},
        {"RENAME_DATABASE_TYPE",    "basic/misc/Rename.gif"},
        {"DELETE_DATABASE_TYPE",        "basic/database/Table.remove.gif"},

    };

    protected String[][] getEntries() {
        return entries;
    }

}
