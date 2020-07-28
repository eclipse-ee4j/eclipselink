/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation and/or its affiliates. All rights reserved.
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
//     09/24/2014-2.6 Rick Curtis
//       - 443762 : Misc message cleanup.
package org.eclipse.persistence.internal.localization.i18n;

import java.util.ListResourceBundle;

public class JAXBLocalizationResource extends ListResourceBundle {
    static final Object[][] contents = {
            {"start_orajaxb", "Invoking orajaxb"},
            {"start_toplink", "Invoking TopLink Generation"},
            {"read_customization", "Reading Customization File"},
            {"generate_source", "Generating Implementation Classes"},
            {"start_mw_project", "Creating Mapping Workbench Project"},
            {"create_descriptors", "Creating Descriptors"},
            {"create_mappings", "Adding mappings to descriptor {0}"},
            {"setup_inheritance", "Setting up inheritance"},
            {"add_namespace_resolvers", "Adding namespace resolvers"},
            {"generate_files", "Writing Deployment XML and Session Configuration"},
            {"missing_src_dir", "Source Output Directory name is missing"},
            {"missing_project_dir", "TopLink Workbench Project directory name is missing"},
            {"missing_output_dir", "TopLink Output Directory name is missing"},
            {"missing_target_package", "Target Package Name is missing"},
            {"impl_package_missing", "Implementation Class Package Name is missing"},
            {"missing_schema_file", "Input Schema file is missing"},
            {"missing_customization", "Input customization file is missing"},
            {"error", "Error: "},
            {"malformed_url_error", "Error: Unexpected MalformedURLException"},
            {"io_exception_error", "Error: Unexpected IOException"},
            {"version", "Version: "},
    };

    /**
     * Return the lookup table.
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
