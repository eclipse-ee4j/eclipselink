package org.eclipse.persistence.buildtools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class CleanRevision extends Task {
    private String input = null;
    private String property = null;
    
    public void execute() throws BuildException {
        boolean input_error=false;
        
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
            // If input empty or unexpanded then set value of property to 'NA'
            input_error = true;
        }
        else {            
            // Last changed revision (branch specific) is stored 2nd to last line of
            //    "svn info --revision HEAD <url>" output
            // Get "endString" Index based on search for last instance of "Last Changed" (Last line of output)
            int endLastRev = input.lastIndexOf("Last Changed");
            // Get "beginString" index based upon the last space in the line above the endString index (and don't include the space)
            int beginLastRev = input.lastIndexOf(" ", endLastRev);
            if( (beginLastRev >=0)&& (endLastRev > 0) && (beginLastRev < endLastRev)) {
                // Trim the string to not include the leading space, or the end-of-line
                getProject().setProperty( property, input.substring(beginLastRev, endLastRev).trim());
                log("CleanRev Finished. New value of: '" + property + "' is '" + input.substring(beginLastRev, endLastRev).trim() + "'.", Project.MSG_VERBOSE);  
            }
            else {
                input_error = true;               
            }
        }
        if(input_error) {
            log("CleanRev Finished. Input empty or search failed! Setting value to 'NA'", Project.MSG_VERBOSE);  
            getProject().setProperty(property, "NA");                
        }
    }
    
    public void setInput(String input) {
        this.input = input;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
