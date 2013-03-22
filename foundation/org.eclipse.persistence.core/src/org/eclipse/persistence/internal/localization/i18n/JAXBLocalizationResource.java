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
package org.eclipse.persistence.internal.localization.i18n;

import java.util.ListResourceBundle;

public class JAXBLocalizationResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "start_orajaxb", "Invoking orajaxb" },
                                           { "start_toplink", "Invoking TopLink Generation" },
                                           { "read_customization", "Reading Customization File" },
                                           { "generate_source", "Generating Implementation Classes" },
                                           { "start_mw_project", "Creating Mapping Workbench Project" },
                                           { "create_descriptors", "Creating Descriptors" },
                                           { "create_mappings", "Adding mappings to descriptors" },
                                           { "setup_inheritance", "Setting up inheritance" },
                                           { "add_namespace_resolvers", "Adding namespace resolvers" },
                                           { "generate_files", "Writing Deployment XML and Session Configuration" },
                                           { "missing_src_dir", "Source Ouput Directory name is missing" },
                                           { "missing_project_dir", "TopLink Workbench Project directory name is missing" },
                                           { "missing_output_dir", "TopLink Output Directory name is missing" },
                                           { "missing_target_package", "Target Package Name is missing" },
                                           { "impl_package_missing", "Implementation Class Package Name is missing" },
                                           { "missing_schema_file", "Input Schema file is missing" },
                                           { "missing_customization", "Input customization file is missing" },
                                           { "error", "Error: " },
                                           { "malformed_url_error", "Error: Unexpected MalformedURLException" },
                                           { "io_exception_error", "Error: Unexpected IOException" },
                                           { "version", "Version: " },
    };

    /**
    * Return the lookup table.
    */
    protected Object[][] getContents() {
        return contents;
    }
}
