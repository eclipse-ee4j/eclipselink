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

import java.io.File;
import java.io.FilenameFilter;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.eclipse.persistence.buildtools.helper.Version;
import org.eclipse.persistence.buildtools.helper.VersionException;

public class SelectBundle extends Task {
    private boolean includepath = false; // whether to include the path (directory property) in the value of property if selection is successful (default: no)
    private boolean versiononly = false; // whether to return bundle version info only, or full bundle name (default: full bundle name)
    private String criterion = "";       // the OSGi-like criteria used to 'select' the most appropriate jar. for example:(1.0,2.0]
    private String basename  = "";       // basename of jar (org.eclipse.persistence.jpa, javax.xml.bind, javax.persistence)
    private String directory = "";       // directory to search for the jar
    private String property  = "";       // property to set with filename of 'selected' jar
    private String separator = "_";      // the separator used to differentiate basename and jarversion
    private String suffix    = "jar";    // suffix of file to find (default: jar)

    private boolean minInclusive = false;   // local: whether the 'floor' version is inclusive or not "["=true "("=false
    private boolean maxInclusive = false;   // local: whether the 'ceiling' version is inclusive or not "]"=true ")"=false
    private Version minVersion   = null;    // local: the value of the "floor" version
    private Version maxVersion   = null;    // local: the value of the "ceiling" version
    private Version bestVersion  = null;    // the 'version' component of the "bestmatch" string
    private Version version      = null;    // the 'version' currently being evaluated

    private void evaluateCriteria() throws BuildException {
        // ()= includes
        // []= up to, not including
        // should be at most 1 ','
        // can have 1 version, or two containing up to four version parts (major.minor.bugfix.qualifier)
        if ( !( criterion == null || criterion.length() == 0)) {
            int endIndex = criterion.length() - 1;
            boolean minSquareBracket = criterion.startsWith("[");
            boolean maxSquareBracket = criterion.endsWith("]");
            boolean minRoundBracket = criterion.startsWith("(");
            boolean maxRoundBracket = criterion.endsWith(")");
            boolean initInt = criterion.substring(0,1).contains("0123456789");
            boolean endInt = criterion.substring(endIndex-1,endIndex).contains("0123456789");
            boolean commapresent = criterion.contains(",");
            // validate beginning of string
            if ( !( minSquareBracket || minRoundBracket || initInt ) )
                throw new BuildException("The criterion attribute must begin with (,[, or a number.", getLocation());
            // validate end of string
            if ( !( maxSquareBracket || maxRoundBracket || endInt ) )
                throw new BuildException("The criterion attribute must end with ),], or a number.", getLocation());
            // validate basic syntax (should add check for multiple ",")
            if((initInt && !endInt) || (!initInt && endInt) || (!initInt && !commapresent) || (initInt && commapresent) )
                throw new BuildException("The criterion attribute must be a valid OSGi version range string", getLocation());
            // validate range syntax (add check for multiple ",")
            if(commapresent) {
                // if multiple comma present
                if( criterion.substring(criterion.indexOf(',')+1,endIndex).contains(",") )
                    throw new BuildException("The criterion attribute must be a valid OSGi version range string: multiple comma(,) detected.", getLocation());
            }
            // set "inclusion" status
            minInclusive = (minSquareBracket || initInt);
            maxInclusive = (maxSquareBracket || endInt);
            // determine "floor" version
            //singleton version
            if(initInt) {
                log("evaluateCriteria: Singleton detected", Project.MSG_VERBOSE);
                minVersion = new Version(criterion);
                // an approximation of infinity (high value for major.monor.micro and qualifier)
                maxVersion = new Version("99999.99999.99999.ÿÿÿÿÿÿ");
            }
            // version range
            if(!initInt) {
                log("evaluateCriteria: Range(min:" + criterion.substring(1,criterion.indexOf(',')) + ")", Project.MSG_VERBOSE);
                minVersion = new Version( criterion.substring(1,criterion.indexOf(',')) );
            }
            if(!endInt) {
                log("evaluateCriteria: Range(max:" + criterion.substring(criterion.indexOf(',')+1,endIndex) + ")", Project.MSG_VERBOSE);
                maxVersion = new Version( criterion.substring(criterion.indexOf(',')+1,endIndex) );
            }
        }
        else {
            // criteria not specified. Default is "any version" or "[0.0,infinity]"
            minInclusive = true;
            minVersion = new Version();
            // an approximation of infinity (high value for major.monor.micro and qualifier)
            maxVersion = new Version("99999.99999.99999.ÿÿÿÿÿÿ");
        }
        
        // Debug Logging
        if(minInclusive)
            log("evaluateCriteria: minInclusive(true)", Project.MSG_VERBOSE);
        else
            log("evaluateCriteria: minInclusive(false)", Project.MSG_VERBOSE);
        if(maxInclusive)
            log("evaluateCriteria: maxInclusive(true)", Project.MSG_VERBOSE);
        else
            log("evaluateCriteria: maxInclusive(false)", Project.MSG_VERBOSE);
        log("evaluateCriteria: minVersion(" + minVersion.getIdentifier() + ")", Project.MSG_VERBOSE);
        log("evaluateCriteria: maxVersion(" + maxVersion.getIdentifier() + ")", Project.MSG_VERBOSE);

    }

    private String matchCriteria() {
        String bestMatch = null;   //filename of file selected
        String[] filelist = getListOfFiles(directory);
        
        bestVersion = minVersion;  //Start search with bestVersion set to minimum acceptable
        log("matchCriteria: bestVersion.getIdentifier='" + bestVersion.getIdentifier() + "'", Project.MSG_VERBOSE);
        log("matchCriteria: basename.length='" + Integer.toString(basename.length()) + "'", Project.MSG_VERBOSE);
        log("matchCriteria: separator.length='" + Integer.toString(separator.length()) + "'", Project.MSG_VERBOSE);
        int relativeVersionIndex = basename.length() + separator.length();
        log("matchCriteria: relativeVersionIndex='" + Integer.toString(relativeVersionIndex) + "' (Should be '" + Integer.toString(basename.length()+separator.length()) + "')", Project.MSG_VERBOSE);
        if( filelist != null ) {
            log("matchCriteria: filelist.length='" + Integer.toString(filelist.length) + "'", Project.MSG_VERBOSE);
            for ( int i=0; i<filelist.length; i++ )
            {
                int versionEndIndex = filelist[i].indexOf("."+suffix);
                log("matchCriteria: versionEndIndex(" + Integer.toString(versionEndIndex)+")", Project.MSG_VERBOSE);
                log("matchCriteria: filelist["+ Integer.toString(i) + "](" + filelist[i] + ")", Project.MSG_VERBOSE);
                // Should add try block to test for version exception
                try {
                    version = new Version( filelist[i].substring(relativeVersionIndex,versionEndIndex) );
                } catch ( VersionException e){
                    log("matchCriteria: Exception detected -> " + e, Project.MSG_VERBOSE);
                }
    
                log("matchCriteria: version string of found file(" + version.getIdentifier() + ")", Project.MSG_VERBOSE);
                if(!version.empty()) {
                    if(minInclusive && maxInclusive) {
                        log("matchCriteria: Test:  " + minVersion.getIdentifier() + " <= " + version.getIdentifier() + " <= " + maxVersion.getIdentifier(), Project.MSG_VERBOSE);
                        if(minVersion.le(version) && maxVersion.ge(version) ){
                            log("matchCriteria:      Pass", Project.MSG_VERBOSE);
                            if( bestVersion.lt(version) ){
                                log("matchCriteria: " + bestVersion.getIdentifier() + " < " + version.getIdentifier(), Project.MSG_VERBOSE);
                                bestMatch = filelist[i];
                                bestVersion = version;
                            } else
                                log("matchCriteria: ! " + bestVersion.getIdentifier() + " < " + version.getIdentifier(), Project.MSG_VERBOSE);
                        }
                    }
                    else if (minInclusive && !maxInclusive){
                        log("matchCriteria: Test:  " + minVersion.getIdentifier() + " <= " + version.getIdentifier() + " < " + maxVersion.getIdentifier(), Project.MSG_VERBOSE);
                        log("matchCriteria:   Result:  " + Boolean.toString(minVersion.le(version)) + " && " + Boolean.toString(maxVersion.gt(version)), Project.MSG_VERBOSE);
                        if(minVersion.le(version) && maxVersion.gt(version) ){
                             log("matchCriteria:      Pass", Project.MSG_VERBOSE);
                             if( bestVersion.lt(version) ){
                                log("matchCriteria: " + bestVersion.getIdentifier() + " < " + version.getIdentifier(), Project.MSG_VERBOSE);
                                bestMatch = filelist[i];
                                bestVersion = version;
                             } else
                                log("matchCriteria: ! " + bestVersion.getIdentifier() + " < " + version.getIdentifier(), Project.MSG_VERBOSE);
                        }
                    }
                    else if (!minInclusive && maxInclusive){
                        log("matchCriteria: Test:  " + minVersion.getIdentifier() + " < " + version.getIdentifier() + " <= " + maxVersion.getIdentifier(), Project.MSG_VERBOSE);
                        if(minVersion.lt(version) && maxVersion.ge(version) ){
                             log("matchCriteria:      Pass", Project.MSG_VERBOSE);
                             if( bestVersion.lt(version) ){
                                log("matchCriteria: " + bestVersion.getIdentifier() + " < " + version.getIdentifier(), Project.MSG_VERBOSE);
                                bestMatch = filelist[i];
                                bestVersion = version;
                             } else
                                log("matchCriteria: ! " + bestVersion.getIdentifier() + " < " + version.getIdentifier(), Project.MSG_VERBOSE);
                        }
                    }
                    else if (!minInclusive && !maxInclusive ){
                        log("matchCriteria: Test:  " + minVersion.getIdentifier() + " < " + version.getIdentifier() + " < " + maxVersion.getIdentifier(), Project.MSG_VERBOSE);
                        if(minVersion.lt(version) && maxVersion.gt(version) ){
                             log("matchCriteria:      Pass", Project.MSG_VERBOSE);
                             if( bestVersion.lt(version) ){
                                log("matchCriteria: " + bestVersion.getIdentifier() + " < " + version.getIdentifier(), Project.MSG_VERBOSE);
                                bestMatch = filelist[i];
                                bestVersion = version;
                             } else
                                log("matchCriteria: ! " + bestVersion.getIdentifier() + " < " + version.getIdentifier(), Project.MSG_VERBOSE);
                        }
                    }
                    log("matchCriteria: Best Match so far(" + bestMatch + ")", Project.MSG_VERBOSE);
                }
            }
            log("matchCriteria: BestMatch(" + bestMatch + ")", Project.MSG_VERBOSE);
        }
        else log("matchCriteria: filelist is null!", Project.MSG_VERBOSE);
        return bestMatch;
    }

    private String[] getListOfFiles(String directory) {
        class MyFilter implements FilenameFilter
        {
            public boolean accept(File directory, String filepattern)
            {
                if ( filepattern.startsWith(basename + separator) && filepattern.endsWith("." + suffix) ) return true;
                return false;
            }
        }
        return new java.io.File(directory).list( new MyFilter() );
    }

    // The "meat"
    public void execute() throws BuildException {
        if (getProject() == null) {
            throw new IllegalStateException("Project not set!");
        }
        if (basename == null || basename.length() == 0) {
            throw new BuildException("The basename attribute must be present.", getLocation());
        }
        if (directory == null || directory.length() == 0) {
            throw new BuildException("The directory attribute must be present.", getLocation());
        }
        if (property == null || property.length() == 0) {
            throw new BuildException("The property attribute must be present.", getLocation());
        }
        log("execute: ** Evaluate **", Project.MSG_VERBOSE);
        evaluateCriteria();
        log("execute: ** Match **", Project.MSG_VERBOSE);
        String file = matchCriteria();

        if( file != null ){
            if (includepath)
                getProject().setNewProperty(property, directory+"/"+file);
            else {  
                if (versiononly)
                    getProject().setNewProperty(property, bestVersion.getIdentifier() );
                else   
                    getProject().setNewProperty(property, file);
            }
        }
        log("execute: Search Finished.", Project.MSG_VERBOSE);
    }

    // Setters
    public void setIncludepath(boolean includepath) {
        this.includepath = includepath;
    }

    public void setVersionOnly(boolean versiononly) {
        this.versiononly = versiononly;
    }

    public void setCriterion(String criterion) {
        this.criterion = criterion;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
        log("setDirectory: directory='" + directory + "'", Project.MSG_VERBOSE);
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setSeparator(String separator) {
        if ( (separator.length() > 1) || (separator.length() == 0) ) {
            throw new BuildException("The separator attribute can only be a single character.", getLocation());
        }
        this.separator = separator;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

}
