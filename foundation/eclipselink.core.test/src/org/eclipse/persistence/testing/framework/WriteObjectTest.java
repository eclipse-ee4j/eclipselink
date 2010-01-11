/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.foundation.*;

/**
 * <p>
 * <b>Purpose</b>: Define a generic test for writing an object to the database.
 * Should originalObject contain no changes to the original object from the
 * database (a TRIVIAL UPDATE), find and mutate a direct to field mapping before
 * writing the object to the database.  If originalObject is different from but
 * has the same primary key as an object on the database, do not mutate the 
 * object as it has already been changed (a NON-TRIVIAL UPDATE).
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Be independent of the class being tested.
 * <li> Attempt to mutate a Direct to Field mapping if instructed to do so.
 * <li> Execute the insert object query and verify no errors occurred.
 * <li> Verify the object written matches the object that was written.
 * </ul>
 */
public class WriteObjectTest extends TransactionalTestCase {

    /** Query to read the original object from the database */
    protected ReadObjectQuery query;

    /** The original object that is set thru example methods from the model */
    protected Object originalObject;

    /** The originalObject is read from the database and stored here */
    protected Object objectToBeWritten;

    /** The object from the database is read in verify to compare against the 
    * objectToBeWritten.
    */
    protected Object objectFromDatabase;

    /** The following allows individual tests to be run with or
     * without bind all parameters on. */
    protected Boolean bindAllParameters = null;
    protected Boolean bindAllParametersOriginal = null;

    /** An option to test non-trivial updates. */
    protected boolean makesTrivialUpdate = true;

    /** This determines whether the test should attempt to mutate the object.
     * This defaults to true.  It can be set to false where changing the object
     * will cause failures.
     */
    protected boolean testShouldMutate = true;

    public WriteObjectTest() {
        setDescription("The test writing of the intended object from the "+
            "database and checks if it was inserted properly");
    }

    public WriteObjectTest(Object originalObject) {
        this.originalObject = originalObject;
        setName(getName() + "(" + originalObject + ")");
        setDescription(
            "The test writing of the intended object, '"+originalObject
            +"', from the database and checks if it was inserted properly");
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    /**
     * A method that takes an object and attempts to find a direct to field
     * mapping that can be mutated.  The value of the mapping is changed by
     * appending a mutation string to the existing value in the mapping.
     * Should no suitable mapping be found, the method simply returns the
     * object passed in.
     * This covers test case bug 2773036
     */
    public Object findAndMutateDirectToFieldMappingInObject(Object objectToBeMutated, boolean isInUOW) {
        DatabaseMapping dbMapping = null;
        DatabaseMapping mutatableMapping = null;

        String mutationString = "M"; // The string to append

        /**
         * Here the class of the object passed is used to get the corresponding
         * descriptor, which is then used to find the mappings and determine if
         * one exists that can be mutated
         */
        Class objectClass = objectToBeMutated.getClass();
        ClassDescriptor descriptor = getSession().getProject().getClassDescriptor(objectClass);
        java.util.Vector mappings = descriptor.getMappings();

        if (isInUOW) {
            mutationString += "U";
        }

        java.util.Enumeration en = mappings.elements();

        /**
         * Parse the mappings for the object's descriptor to find a suitable
         * mapping that can be mutated.  The mapping must meet the conditions:
         * Not a primary key mapping
         * Must be direct to field
         * Must be a string field
         * Must not have a converter (i.e. Male-->M, Female-->F, Unknown-->U)
         * The loop exits once an appropriate mapping is found or the list of
         * mappings has been fully parsed (whichever occurs first)
         */
        while (en.hasMoreElements ()) {
            dbMapping = (DatabaseMapping) en.nextElement();

            if (!dbMapping.isPrimaryKeyMapping()
                    && dbMapping.isDirectToFieldMapping()
                    &&!((AbstractDirectMapping) dbMapping).hasConverter()
                    && (dbMapping.getAttributeAccessor().getAttributeClass())
                        .getName().indexOf("String") != -1) {
                mutatableMapping = dbMapping;
                break;
            }
        }

        /**
         * If a mapping was found that can be mutated, use TopLink methods to
         * modify the value stored in the object for that mapping.  Otherwise
         * do nothing.
         */
        if (mutatableMapping != null) {
            mutatableMapping.setAttributeValueInObject(
                objectToBeMutated, 
                mutatableMapping.getAttributeValueFromObject(
                    objectToBeMutated) + mutationString);
        }
        else {
            // Can't necessarily throw error/warning as some projects 
            // (i.e. LOB project) have descriptors that are not simple to mutate
        }

        return objectToBeMutated;
    }

    /**
     * @see #setMakesTrivialUpdate(boolean)
     */
    public boolean makesTrivialUpdate() {
        return makesTrivialUpdate;
    }

    /**
     * @see #setTestShouldMutate(boolean)
     */
    public boolean testShouldMutate() {
        return testShouldMutate;
    }

    public void reset() {
        if (bindAllParametersOriginal != null) {
            getSession().getLogin().setShouldBindAllParameters(bindAllParametersOriginal.booleanValue());
        }
        super.reset();
    }

    /**
     * A trivial update is writing the same version of an object to the
     * database as exists on the database.  Normally in this test the object
     * passed to the constructor is used only to read the version of itself on
     * the database.  This version from the database is then written to the
     * database: a trivial update.  If this flag is set and originalObject is
     * different from but has the same primary key as an object on the database,
     * then a non-trivial update will occur.
     * Warning: If you modify the objects passed to this
     * test, do not obtain them from the population manager, as they may become
     * corrupted for other users.
     */
    public void setMakesTrivialUpdate(boolean value) {
        this.makesTrivialUpdate = value;
    }

    /**
     * Some subclasses of WriteObjectTest will not return the correct results if 
     * the object is mutated, for example tests that pass null values and expect
     * nulls to be returned.  If this flag is set then the object will not be
     * mutated before attempting to write to the database.
     */
    public void setTestShouldMutate(boolean value) {
        this.testShouldMutate = value;
    }

    /**
     * Allows one to set bindAllParameters to true on a test by test basis.
     * This works only for simple sessions, and could cause this simple test to
     * run much slower than before.
     */
    public void setShouldBindAllParameters(boolean value) {
        bindAllParameters = Boolean.valueOf(value);
    }

    protected void setup() {
        if (shouldBindAllParameters() != null) {
            bindAllParametersOriginal = Boolean.valueOf(getSession().getLogin().shouldBindAllParameters());
            getSession().getLogin().setShouldBindAllParameters(shouldBindAllParameters().booleanValue());
        }
        super.setup();

        this.query = new ReadObjectQuery();
        this.query.setSelectionObject(this.originalObject);

        /* Must ensure that the object is from the database for updates. */
        this.objectToBeWritten = getSession().executeQuery(this.query);
        if (this.objectToBeWritten == null) {
            this.objectToBeWritten = this.originalObject;
            this.query = new ReadObjectQuery();
            this.query.setSelectionObject(this.originalObject);
        }

        if (!makesTrivialUpdate()) {
            this.objectToBeWritten = this.originalObject;
        }
    }

    public Boolean shouldBindAllParameters() {
        return bindAllParameters;
    }


    /**
     * The test() method will, if required, pass the object to the 
     * findAndMutateDirectToFieldMappingInObject method, and will then attempt
     * to write the object to the database.
     */
    protected void test() {

        if (makesTrivialUpdate() && testShouldMutate()) {
            // Only want to do this if the update is trivial
            // Otherwise there are already changes in the object that
            // will generate SQL
            this.objectToBeWritten = 
                this.findAndMutateDirectToFieldMappingInObject(
                    this.objectToBeWritten, false);
        }

        getDatabaseSession().writeObject(this.objectToBeWritten);
    }

    /**
     * Verify if the objects match completely through allowing the session 
     * to use the descriptors.  This will compare the objects and all of 
     * their privately owned parts.
     */
    protected void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        this.objectFromDatabase = getSession().executeQuery(this.query);

        if (!(compareObjects(this.objectToBeWritten, this.objectFromDatabase))) {
            throw new TestErrorException("The object inserted into the database, '"
                + this.objectFromDatabase + "' does not match the original, '" 
                + this.objectToBeWritten + "'.");
        }
    }
}
