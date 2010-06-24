/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SelectBundle
 *   property to set
 *   basename     (bnd, org.eclipse.equinox) required
 *   separator    (-,_) default to _
 *   criterion    OSGi selection criteria [1.0,2.0) required
 *
 * Contributors:
 *     egwin - initial conception and implementation
 */

package org.eclipse.persistence.buildtools.ant.taskdefs;

import java.io.File;
import java.io.FilenameFilter;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.eclipse.persistence.buildtools.helper.Version;

public class SelectBundle extends Task {
    private String criterion = "";      // the OSGi-like criteria used to 'select' the most appropriate jar. for example:(1.0,2.0]
    private String basename  = "";      // basename of jar (org.eclipse.persistence.jpa, javax.xml.bind, javax.persistence)
    private String directory  = "";     // directory to search for the jar
    private String property  = "";      // property to set with filename of 'selected' jar
    private String separator = "_";     // the separator used to differentiate basename and jarversion
    private String suffix    = "jar";   // suffix of file to find (default: jar)

    //private boolean debug = true;           // local: whether to print debugging messages
    private boolean minInclusive = false;   // local: whether the 'floor' version is inclusive or not "("=true "["=false
    private boolean maxInclusive = false;   // local: whether the 'ceiling' version is inclusive or not "("=true "["=false
    private Version minVersion   = null;    // local: the value of the "floor" version
    private Version maxVersion   = null;    // local: the value of the "ceiling" version

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
            // validate basic syntax
            if((initInt && !endInt) || (!initInt && endInt) || (!initInt && !commapresent) || (initInt && commapresent) )
                throw new BuildException("The criterion attribute must be a valid OSGi version range string", getLocation());
            // determine "floor" version
            minInclusive = (minSquareBracket || initInt);
            maxInclusive = (maxSquareBracket || endInt);
            //singleton version
            if(initInt) {
                //if(debug) log("evaluateCriteria: Singleton detected");
                minVersion = new Version(criterion);
                // an approximation of infinity (high value for major.monor.micro and qualifier)
                maxVersion = new Version("99999.99999.99999.ÿÿÿÿÿÿ");
            }
            // version range
            if(!initInt) {
                //if(debug) log("evaluateCriteria: Range(min:" + criterion.substring(1,criterion.indexOf(',')) + ")");
                minVersion = new Version( criterion.substring(1,criterion.indexOf(',')) );
            }
            if(!endInt) {
                //if(debug) log("evaluateCriteria: Range(max:" + criterion.substring(criterion.indexOf(',')+1,endIndex) + ")");
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
        //if(debug) {
        //    if(minInclusive)
        //        log("evaluateCriteria: minInclusive(true)");
        //    else
        //        log("evaluateCriteria: minInclusive(false)");
        //    if(maxInclusive)
        //        log("evaluateCriteria: maxInclusive(true)");
        //    else
        //        log("evaluateCriteria: maxInclusive(false)");
        //    if(debug) log("evaluateCriteria: minVersion(" + minVersion.getIdentifier() + ")");
        //    if(debug) log("evaluateCriteria: maxVersion(" + maxVersion.getIdentifier() + ")");
        //}

    }

    private String matchCriteria() {
        String bestMatch = null;       //filename of file selected
        String[] filelist = getListOfFiles(directory);
        int relativeVersionIndex = basename.length() + separator.length();
        //if(debug) log("matchCriteria: relativeVersionIndex(" + Integer.toString(relativeVersionIndex) + ")");
        for ( int i=0; i<filelist.length; i++ )
        {
            int versionEndIndex = filelist[i].indexOf("."+suffix);
            //if(debug) log("matchCriteria: versionEndIndex(" + Integer.toString(versionEndIndex)+")");
            //if(debug) log("matchCriteria: filelist["+ Integer.toString(i) + "](" + filelist[i] + ")");
            // Should add try block to test for version exception
            String version = filelist[i].substring(relativeVersionIndex,versionEndIndex);

            //if(debug) log("matchCriteria: version string of found file(" + version + ")");
            if(version.length()>0){
                if(minInclusive && maxInclusive) {
                    if(minVersion.le(version) && maxVersion.ge(version) ){
                        bestMatch = filelist[i];
                    }
                }
                else if (minInclusive && !maxInclusive){
                    if(minVersion.le(version) && maxVersion.gt(version) ){
                        bestMatch = filelist[i];
                    }
                }
                else if (!minInclusive && maxInclusive){
                    if(minVersion.lt(version) && maxVersion.ge(version) ){
                        bestMatch = filelist[i];
                    }
                }
                else if (!minInclusive && !maxInclusive ){
                    if(minVersion.lt(version) && maxVersion.gt(version) ){
                        bestMatch = filelist[i];
                    }
                }
                //if(debug) log("matchCriteria: Best Match so far(" + bestMatch + ")");
            }
        }
        //if(debug) log("matchCriteria: BestMatch(" + bestMatch + ")");
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
        if (basename == null || basename.length() == 0) {
            throw new BuildException("The basename attribute must be present.", getLocation());
        }
        if (directory == null || directory.length() == 0) {
            throw new BuildException("The directory attribute must be present.", getLocation());
        }
        if (property == null || property.length() == 0) {
            throw new BuildException("The property attribute must be present.", getLocation());
        }
        //if(debug) log("execute: ** Evaluate **");
        evaluateCriteria();
        //if(debug) log("execute: ** Match **");
        String file = matchCriteria();

        if( !(file == null)){
            getProject().setProperty(property, file);
        }
        //if(debug) log("execute: Search Finished.");
    }

    // Setters
    public void setCriterion(String criterion) {
        this.criterion = criterion;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
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
