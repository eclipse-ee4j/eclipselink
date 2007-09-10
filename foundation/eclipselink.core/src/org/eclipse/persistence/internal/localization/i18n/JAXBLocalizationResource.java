/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.localization.i18n;

import java.util.ListResourceBundle;
import org.eclipse.persistence.internal.helper.Helper;

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
                                           { "missing_schema", Helper.cr() + "  --Schema file needs to be given as input with the option -schema <FileName>." },
                                           { "error", "Error: " },
                                           { "malformed_url_error", "Error: Unexpected MalformedURLException" },
                                           { "io_exception_error", "Error: Unexpected IOException" },
                                           { "usage", "Usage: org.eclipse.persistence.jaxb.compiler.tljaxb [-options]" + Helper.cr() + Helper.cr() + "Options:" + Helper.cr() + "    -help                     " + "Prints the help message text" + Helper.cr() + "    -version                  " + "Prints the release version" + Helper.cr() + "    -sourceDir <DirName>      " + "The directory to generate Java source" + Helper.cr() + "    -generateWorkbench        " + "Option to generate a TopLink Mapping Workbench Project" + Helper.cr() + "    -workbenchDir <DirName>   " + "The directory to generate TopLink Mapping Workbench Project" + Helper.cr() + "    -schema    <FileName>     " + "The input schema file (required)" + Helper.cr() + "    -targetPkg <PkgName>      " + "The package name for generated Java files" + Helper.cr() + "    -implClassPkg <PkgName>   " + "The package name for generated impl classes, if different from Interfaces (optional)" + Helper.cr() + "    -interface                " + "Option to generate the Java interfaces only" + Helper.cr() + "    -verbose                  " + "Give a list of interfaces and classes that are generated" + Helper.cr() + "    -customize                " + "Specify a standard JAXB customization file to override default JAXB compiler behavior (optional)" + Helper.cr() },
                                           { "version", "Version: " },
    };

    /**
    * Return the lookup table.
    */
    protected Object[][] getContents() {
        return contents;
    }
}