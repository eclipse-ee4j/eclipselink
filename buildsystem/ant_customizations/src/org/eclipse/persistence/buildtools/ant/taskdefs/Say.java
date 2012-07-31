/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     egwin - initial implementation, Dec 2009
 *     egwin - expanded to add file output and property expansion support, Jan 2010
 *     egwin - clean-up and comment
 ******************************************************************************/
package org.eclipse.persistence.buildtools.ant.taskdefs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.LogLevel;
import org.apache.tools.ant.util.FileUtils;

/**
 * This class expanded to do everything Ant's "Echo" class/task does, while also supporting "if=" and "unless="
 *      used primarily in debugging builds and for dependency warnings.
 *
 * @author Eric Gwin
 * @since EclipseLink 2.2.0,
 */
public class Say extends Task {

    private String encoding = "";             // encoding to use when writing to file, defaults to platform default
    private String ifProperty;                // the name of the property to test; if exist=true, then exec=true
    private String unlessProperty;            // the name of the property to test; if exist=true, then exec=false

    protected boolean append = false;         // whether to append message to file if it exists.
    protected int level = Project.MSG_WARN;   // logging level (when to output). Should be: error, warning, info, verbose, or debug.
    protected File outfile = null;            // file to write to (standard out if not specified).
    protected String message = "";            // message to print if conditions met.

    // The "meat"
    public void execute() throws BuildException {
        if (testIf() && testUnless()) {
            if (outfile == null) {
                log(message, level);
            } else {
                Writer outbuffer = null;
                try {
                    String f = outfile.getAbsolutePath();
                    if (encoding.length() == 0 || encoding == null) {
                        outbuffer = new FileWriter(f, append);
                    } else {
                        outbuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, append), encoding));
                    }
                    outbuffer.write(message, 0, message.length());
                } catch (IOException e) {
                    throw new BuildException(e, getLocation());
                } finally {
                    FileUtils.close(outbuffer);
                }
            }
        }
    }

    // Conditional printing: if "if=" doesn't exist return "true" for print status
    // otherwise return result of test: "ifProperty" isn't null
    private boolean testIf() {
        if (ifProperty == null || ifProperty.equals("")) {
            return true;
        }
        return getProject().getProperty(ifProperty) != null;
    }

    // Conditional printing: if "unless=" doesn't exist return "true" for print status
    // otherwise return result of test: "unlessProperty" is null
    private boolean testUnless() {
        if (unlessProperty == null || unlessProperty.equals("")) {
            return true;
        }
        return getProject().getProperty(unlessProperty) == null;
    }

    // allows for property substitution within 'message' string
    public void addText(String singlelinemsg) {
        message = message + getProject().replaceProperties(singlelinemsg);
    }

    // Setters
    public void setAppend(boolean append) {
        this.append = append;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setFile(File outfile) {
        this.outfile = outfile;
    }

    public void setIf(String c) {
        ifProperty = c;
    }

    public void setLevel(SayLevel sayLevel) {
        level = sayLevel.getLevel();
    }

    public void setMessage(String singlelinemsg) {
        this.message = singlelinemsg;
    }

    public void setUnless(String c) {
        unlessProperty = c;
    }

    // innerclass
    public static class SayLevel extends LogLevel {
    }
  }
