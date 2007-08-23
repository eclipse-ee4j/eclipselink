/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml;

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * <code>XMLFileLogin</code> holds the information required to log in to a
 * file-based XML data store, using an <code>XMLFileAccessor</code>.
 *
 * @see XMLFileAccessor
 * @see XMLPlatform
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLFileLogin extends XMLLogin {

    /** The name of the base directory for the tree of XML subdirectories/files. */
    private String baseDirectoryName;

    /** The extension to be used for the XML files. */
    private String fileNameExtension;

    /** This tells the accessor to create directories if they are missing. */
    private boolean createsDirectoriesAsNeeded;

    /**
     * Default constructor.
     */
    public XMLFileLogin() {
        super();
    }

    /**
     * Constructor. Specify the name of the directory holding
     * all the subdirectories with the XML documents.
     */
    public XMLFileLogin(String baseDirectoryName) {
        this();
        this.initialize(baseDirectoryName);
    }

    /**
     * Constructor.
     */
    public XMLFileLogin(DatabasePlatform platform) {
        super(platform);
    }

    /**
     * Build and return an appropriate Accessor.
     */
    public Accessor buildAccessor() {
        XMLFileAccessor accessor = (XMLFileAccessor)super.buildAccessor();
        accessor.setBaseDirectoryName(this.getBaseDirectoryName());
        accessor.setFileNameExtension(this.getFileNameExtension());
        accessor.setCreatesDirectoriesAsNeeded(this.createsDirectoriesAsNeeded());
        return accessor;
    }

    /**
     * Set whether directories will be created
     * as needed, if they do not exist already.
     */
    public void createDirectoriesAsNeeded() {
        this.setCreatesDirectoriesAsNeeded(true);
    }

    /**
     * Return whether directories will be created
     * as needed, if they do not exist already.
     */
    public boolean createsDirectoriesAsNeeded() {
        return createsDirectoriesAsNeeded;
    }

    /**
     * Set whether directories will be created
     * as needed, if they do not exist already.
     */
    public void dontCreateDirectoriesAsNeeded() {
        this.setCreatesDirectoriesAsNeeded(false);
    }

    /**
     * Return the name of the base directory
     * for the tree of XML subdirectories/files.
     */
    public String getBaseDirectoryName() {
        return baseDirectoryName;
    }

    /**
     * Return the name of the temporary directory.
     */
    protected String tempDirectoryName() {
        return Helper.tempDirectory();
    }

    /**
     * Return the default Accessor Class.
     */
    protected Class getDefaultAccessorClass() {
        return ClassConstants.XMLFileAccessor_Class;
    }

    /**
     * Return the default standard file extension that will be
     * appended to the XML file names.
     */
    protected String getDefaultFileNameExtension() {
        return ".xml";
    }

    /**
     * Return the extension to be used for the XML files.
     */
    public String getFileNameExtension() {
        return fileNameExtension;
    }

    /**
     * Initialize the login.
     */
    protected void initialize() {
        super.initialize();
        baseDirectoryName = this.tempDirectoryName();
        fileNameExtension = this.getDefaultFileNameExtension();
        createsDirectoriesAsNeeded = false;
    }

    /**
     * Initialize the login.
     */
    protected void initialize(String baseDirectoryName) {
        if (baseDirectoryName != null) {
            this.setBaseDirectoryName(baseDirectoryName);
        }
    }

    /**
     * Set the name of the base directory
     * for the tree of XML subdirectories/files.
     */
    public void setBaseDirectoryName(String baseDirectoryName) {
        this.baseDirectoryName = baseDirectoryName;
    }

    /**
     * Set whether directories will be created
     * as needed, if they do not exist already.
     */
    public void setCreatesDirectoriesAsNeeded(boolean createsDirectoriesAsNeeded) {
        this.createsDirectoriesAsNeeded = createsDirectoriesAsNeeded;
    }

    /**
     * Set the extension to be used for the XML files.
     */
    public void setFileNameExtension(String fileNameExtension) {
        this.fileNameExtension = fileNameExtension;
    }
}