/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.lob;

import org.eclipse.persistence.testing.tests.writing.ComplexUpdateTest;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * <p>
 * <b>Purpose</b>: Define a test for updating CLOB/BLOB into the database.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Write an object (with CLOB/BLOB value) into the database ready for the update.
 * <li> Query back the inserted object, and make some changes.
 * <li> Verify the object modified matches the object that was written.	
 * </ul>
 * 
 * @author King Wang (Aug. 2002)
 * @since TopLink/Java 5.0
 */
public class LOBUpdateTest extends ComplexUpdateTest {
    private int size;
    Object originalObjectNotInDB;

    public LOBUpdateTest(Object originalObjectNotInDB, int size) {
        this(originalObjectNotInDB, size, false);
    }

    public LOBUpdateTest(Object originalObjectNotInDB, int size, boolean usesUnitOfWork) {
        super();
        this.originalObjectNotInDB = originalObjectNotInDB;
        this.usesUnitOfWork = usesUnitOfWork;
        this.size = size;
        setDescription("This case tests the BLOB/CLOB update with size less or bigger than 4k and checks if it was updated properly");
    }

    protected void changeObject() {
        //Bug#3128838  Test Byte[] support
        Image image = (Image)workingCopy;
        image.setPicture(ImageSimulator.initObjectByteBase(size));
        image.setScript(ImageSimulator.initStringBase(size / 100));
        image.setAudio(ImageSimulator.initByteBase(size * 2));
        image.setCommentary(ImageSimulator.initCharArrayBase(size * 2));
    }

    public String getName() {
        String strUpdated = "Updated " + size + ", " + 2 * size;
        String strOriginal = "Original " + ((Image)originalObjectNotInDB).getPicture().length + ", " + ((Image)originalObjectNotInDB).getScript().length();
        String str = "LOBUpdateTest(" + strUpdated + "; " + strOriginal + ")";
        if (usesUnitOfWork) {
            str = str + " usesUOW";
        }
        return str;
    }

    protected void setup() {
        // Create originalObject and insert it into db - it will be deleted in reset()
        try {
            originalObject = ((Image)originalObjectNotInDB).clone();
        } catch (CloneNotSupportedException ex) {
            throw new TestProblemException("clone failed", ex);
        }

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(originalObject);
        uow.commit();

        super.setup();
    }

    public void reset() {
        if (originalObject != null) {
            super.reset();

            UnitOfWork uow = getSession().acquireUnitOfWork();
            uow.deleteObject(originalObject);
            uow.commit();

            originalObject = null;
        }
    }
}
