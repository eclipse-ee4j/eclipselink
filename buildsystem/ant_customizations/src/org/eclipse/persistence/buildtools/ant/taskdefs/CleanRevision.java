package org.eclipse.persistence.buildtools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class CleanRevision extends Task {
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
        if ( input.startsWith("${") || input =="" ) {
            getProject().setNewProperty(property, "NA");
        }
        else {            
            // Last changed revision (branch specific) is stored 2nd to last line of
            //    "svn info --revision HEAD <url>" output
            // Get "endString" Index based on search for last instance of "Last Changed"
            int endLastRev = input.lastIndexOf("Last Changed");
            // Get "beginString" index based upon the last space in the line above the endString index
            int beginLastRev = input.lastIndexOf(" ", endLastRev);
            // Trim the string to not include the leading space, or the end-of-line
            getProject().setNewProperty( property, input.substring(beginLastRev+1, endLastRev-1));
        }
    }
    
    public void setInput(String input) {
        this.input = input;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
