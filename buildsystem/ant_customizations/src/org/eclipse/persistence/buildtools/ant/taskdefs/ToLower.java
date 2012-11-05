/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SelectBundle
 *   basename     (bnd, org.eclipse.equinox) : required
 *   criterion    OSGi version selection criteria [1.0,2.0) : required
 *   separator    separator used between basename and version in filename (such as - or _) : defaults to _
 *   property     propety to set : required
 *   includepath  boolean flag, if set will include path and filename in "property" : defaults to 'false'
 *   versiononly  boolean flag, if set will only set full version of bundle in "property" : defaults to 'false'
 *
 * Contributors:
 *     egwin - initial conception and implementation
 */

package org.eclipse.persistence.buildtools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class ToLower extends Task {
    private String input = null;
    private String property = null;
    
    public void execute() throws BuildException {
        if (input == null) {
            throw new BuildException("'input' attribute must be set.", getLocation());
        }
        if (property == null) {
            throw new BuildException("'property' attribute must be set.", getLocation());
        }
        if (property == "") {
            throw new BuildException("'property' cannot be an empty string.", getLocation());
        }
        if ( input.startsWith("${") || input.startsWith("@{") || input == "" ) {
            // If input empty or unexpanded then set value of property to 'NA'
            log("ToLower Finished.  Input empty or search failed! original value was '" + input + "'.", Project.MSG_VERBOSE);  
            throw new BuildException("'input' is empty, or a property value cannot be expanded.", getLocation());
        }
        else {            
            // put result into property - NB overwrites previous value! Not safe for <parallel> tasks
            getProject().setProperty( property, input.toLowerCase());
            log("ToLower Finished. Old string of '" + input + "' set to '" + input.toLowerCase() + "' in property '" + property + "'.", Project.MSG_VERBOSE);  
        }
    }
    
    public void setInput(String input) {
        this.input = input;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
