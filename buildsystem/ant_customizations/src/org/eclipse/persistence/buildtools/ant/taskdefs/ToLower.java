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
