/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.oxm.jaxb.compiler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.prefs.Preferences;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.DefaultSPIManager;

import org.eclipse.persistence.Version;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.localization.JAXBLocalization;

/**
 * Main class for tljaxb - TopLink JAXB Compiler.
 *
 * @author    Rick Barkhouse <rick.barkhouse@oracle.com>
 * @since    05/31/2004 15:31:43
 */
public class tljaxb {
    private String outputDir = null;
    private String sourceDir = "source";
    private String workbenchDir = "mw";
    private String schema;
    private String targetPkg;
    private String implClassPkg;
    private boolean interfaceGen = false;
    private boolean verbose = false;
    private boolean debug = false;
    private boolean useDomPlatform = false;
    private boolean generateWorkbenchProject = true;
    private static String DEFAULT_CUST = "_tljaxb_cust.xml";
    private ArrayList customizationFile;
    private static final String topLinkDirName = "toplink";
    private static final String fsep = System.getProperty("file.separator");

    // ===========================================================================
    public static void main(String[] args) {
        tljaxb compiler = new tljaxb();

        //by defaul do not generate workbench project from the command line
        compiler.setGenerateWorkbenchProject(false);
        compiler.processArgs(args);

        try {
            compiler.generate();
        } catch (TopLinkJAXBGenerationException ex) {
            ex.printStackTrace();
            ex.getWrappedException().printStackTrace();
            System.exit(1);
        }
    }

    // ===========================================================================
    public void generate() throws TopLinkJAXBGenerationException {
        if (this.outputDir != null) {
            this.sourceDir = this.outputDir;
            this.workbenchDir = this.outputDir + "/" + this.workbenchDir;
        }

        String projectName = stripExtension(getSchema().substring(getSchema().lastIndexOf(fsep) + 1));

        TopLinkJAXBGeneratorLog.log(JAXBLocalization.buildMessage("start_mw_project"));

        MWOXProject mwProject = new MWOXProject(projectName, new DefaultSPIManager(Preferences.userNodeForPackage(this.getClass()), projectName));
        if (this.getGenerateWorkbenchProject()) {
            mwProject.setSaveDirectory(new File(this.workbenchDir));
        }

        MWXmlSchema mwSchema = null;
        try {
            mwSchema = mwProject.getSchemaRepository().createSchemaFromFile(projectName, getSchemaLocation(getSchema()));
        } catch (Exception ex) {
            throw new TopLinkJAXBGenerationException(ex);
        }

        // Invoke orajaxb to generate the interface classes and default customization file.
        invokeOrajaxb();

        // If impl class generation is required and no errors occurred in orajaxb...
        if (!getInterfaceGen()) {
            new TopLinkJAXBGenerator().generate(mwProject, mwSchema, getSourceDir(), getWorkbenchDir(), getImplClassPkg(), projectName, DEFAULT_CUST, this.debug, this.useDomPlatform, this.generateWorkbenchProject);
        }
    }

    // ===========================================================================
    private void processArgs(String[] args) {
        int i = args.length;

        for (int j = 0; j < i; j++) {
            if (args[j].equals("-help")) {
                printUsage("");
                System.exit(0);
            }

            if (args[j].equals("-version")) {
                String s = Version.getProduct() + " " + Version.getVersion() + " " + Version.getBuildNumber();
                System.out.println(JAXBLocalization.buildMessage("version") + s);
                System.exit(0);
            }

            if (args[j].equals("-sourceDir")) {
                if (j == (i - 1)) {
                    printUsage(JAXBLocalization.buildMessage("missing_src_dir"));
                    System.exit(0);
                } else {
                    setSourceDir(args[++j]);
                }
                continue;
            }
            if (args[j].equals("-workbenchDir")) {
                if (j == (i - 1)) {
                    printUsage(JAXBLocalization.buildMessage("missing_project_dir"));
                    System.exit(0);
                } else {
                    this.workbenchDir = args[++j];
                }
                continue;
            }
            if (args[j].equals("-outputDir")) {
                if (j == (i - 1)) {
                    printUsage(JAXBLocalization.buildMessage("missing_output_dir"));
                    System.exit(0);
                } else {
                    this.outputDir = args[++j];
                }
                continue;
            }
            if (args[j].equals("-useDomPlatform")) {
                this.useDomPlatform = true;
            }
            if (args[j].equals("-debug")) {
                this.debug = true;
            }

            if (args[j].equals("-targetPkg")) {
                if (j == (i - 1)) {
                    printUsage(JAXBLocalization.buildMessage("missing_target_package"));
                    System.exit(0);
                } else {
                    this.targetPkg = args[++j];
                }
                continue;
            }
            if (args[j].equals("-implClassPkg")) {
                if (j == (i - 1)) {
                    printUsage(JAXBLocalization.buildMessage("impl_package_missing"));
                    System.exit(0);
                } else {
                    this.implClassPkg = args[++j];
                }
                continue;
            }

            if (args[j].equals("-schema")) {
                if (j == (i - 1)) {
                    printUsage(JAXBLocalization.buildMessage("missing_schema_file"));
                    System.exit(0);
                } else {
                    this.schema = args[++j];
                }
                continue;
            }

            if (args[j].equals("-interface")) {
                this.interfaceGen = true;
                continue;
            }

            if (args[j].equals("-verbose")) {
                this.verbose = true;
                continue;
            }

            if (args[j].equals("-customize")) {
                if (j == (i - 1)) {
                    printUsage(JAXBLocalization.buildMessage("missing_customization"));
                    System.exit(0);
                } else {
                    if (this.customizationFile == null) {
                        this.customizationFile = new ArrayList();
                    }
                    this.customizationFile.add(args[++j]);
                }
                continue;
            }
            if (args[j].equals("-generateWorkbench")) {
                this.generateWorkbenchProject = true;
            }
        }

        String errMsg = "";

        if (this.schema == null) {
            errMsg += JAXBLocalization.buildMessage("missing_schema");
        }

        if (this.implClassPkg == null) {
            this.implClassPkg = this.targetPkg;
        }
        if (errMsg.length() > 0) {
            printUsage(errMsg);
            System.exit(0);
        }
    }

    // ===========================================================================
    private void invokeOrajaxb() throws TopLinkJAXBGenerationException {
        
    }

    private URL fileToURL(String fileName) {
        String path;
        File file = new File(fileName);
        path = file.getAbsolutePath();
        String separator = System.getProperty("file.separator");

        if ((separator != null) && (separator.length() == 1)) {
            path = path.replace(separator.charAt(0), '/');
        }

        if ((path.length() > 0) && (path.charAt(0) != '/')) {
            path = '/' + path;
        }

        try {
            return new URL("file", null, path);
        } catch (MalformedURLException murlex) {
            throw new Error(JAXBLocalization.buildMessage("malformed_url_error"));
        }
    }

    private String getSchemaLocation(String schema) {
        String schemaLoc = "";

        try {
            schemaLoc = new File(schema).getCanonicalPath();
        } catch (IOException ioex) {
            throw new Error(JAXBLocalization.buildMessage("io_exception_error"));
        }

        char oldChar = System.getProperty("file.separator").charAt(0);
        schemaLoc.replace(oldChar, '/');

        return schemaLoc;
    }

    public String getSourceDir() {
        return this.sourceDir;
    }

    public void setSourceDir(String value) {
        this.sourceDir = value;
    }

    public String getWorkbenchDir() {
        return this.workbenchDir;
    }

    public void setWorkbenchDir(String value) {
        this.workbenchDir = value;
    }

    public void setImplClassPkg(String value) {
        this.implClassPkg = value;
    }

    public String getImplClassPkg() {
        return this.implClassPkg;
    }

    public void setSchema(String value) {
        this.schema = value;
    }

    public String getSchema() {
        return this.schema;
    }

    public void setTargetPkg(String value) {
        this.targetPkg = value;
    }

    public String getTargetPkg() {
        return this.targetPkg;
    }

    public void setInterfaceGen(boolean value) {
        this.interfaceGen = value;
    }

    public boolean getInterfaceGen() {
        return this.interfaceGen;
    }

    public void setVerbose(boolean value) {
        this.verbose = value;
    }

    public boolean getVerbose() {
        return this.verbose;
    }

    // Leave this method here for now - the MW calls it, but not for long...
    public void setDefaultCus(String value) {
        // do nothing
    }

    public void setCustomizationFile(String value) {
        if (this.customizationFile == null) {
            this.customizationFile = new ArrayList();
        }
        this.customizationFile.add(value);
    }

    public ArrayList getCustomizationFile() {
        return this.customizationFile;
    }

    public String stripExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }

    // ===========================================================================
    private void printUsage(String message) {
        String usageMessage;

        if (message.equals("")) {
            usageMessage = new String();
        } else {
            usageMessage = JAXBLocalization.buildMessage("error") + message + Helper.cr();
        }

        System.out.println(usageMessage);
        System.out.println(JAXBLocalization.buildMessage("usage"));

    }

    public URL fileToUrl(String file) throws Exception {
        File f = new File(file);
        return f.toURL();
    }

    public boolean getGenerateWorkbenchProject() {
        return generateWorkbenchProject;
    }

    public void setGenerateWorkbenchProject(boolean b) {
        generateWorkbenchProject = b;
    }
}