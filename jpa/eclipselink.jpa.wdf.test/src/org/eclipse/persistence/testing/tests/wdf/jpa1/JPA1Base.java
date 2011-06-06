/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.wdf.jpa1;

import javax.persistence.PersistenceException;

import org.eclipse.persistence.testing.framework.wdf.AbstractBaseTest;

public abstract class JPA1Base extends AbstractBaseTest {

    private static final String[] CLEARABLE_TABLE_NAMES = { "TMP_EMP_CREDIT", "TMP_CREDIT_ACC", "TMP_BROKER_ACC",
            "TMP_SAVE_ACC", "TMP_MARGIN_ACC", "TMP_CHECK_ACC", "TMP_EMP_PROJECT", "TMP_REVIEW_DETAILS",
            "TMP_EMP_REVIEW", "TMP_BASIC_TYPES_FA", "TMP_TEMPORAL_FA", "TMP_BASIC_TYPES_PA", "TMP_NODE", "TMP_EMBEDD_FA",
            "TMP_EMBEDD_PA", "TMP_ISLAND", "TMP_EMP_PATENT", "TMP_PROJECT_DETAILS", "TMP_VEHICLE_PROFILE", "TMP_CASC_NODE",
            "TMP_COURSE_EMP", "TMP_COURSE", "TMP_REVIEW", "TMP_PATENT", "TMP_CASC_NODE_DESC", "TMP_EMP_HOBBY", "TMP_HOBBY",
            "TMP_EMP_BICYCLE", "TMP_VEHICLE", "TMP_EMP", "TMP_PROFILE", "TMP_DEP", "TMP_OFFICE_CUBICLE", "TMP_OFFICE", "TMP_ALLRELATIONS", "TMP_CUBICLE",
            "TMP_ALLRELATIONS_LIST", "TMP_ALLRELATIONS_SET", "TMP_ALLRELATIONS_COLLECTION", "TMP_ALLRELATIONS_MAPID",
            "TMP_ALLRELATIONS_MAPFIELD", "TMP_TASK", "TMP_PROJECT", "TMP_CITY", "TMP_CITY_TMP_COP", "TMP_CITY_TMP_CRIMINAL",
            "TMP_COP", "TMP_COP_TMP_CRIMINAL", "TMP_COP_TMP_INFORMER", "TMP_CRIMINAL", "TMP_CRIMINAL_TMP_CRIMINAL",
            "TMP_INFORMER", "TMP_INFORMER_TMP_COP", "TMP_PERSON", "TMP_TIMESTAMP", "TMP_TRAILER", "TMP_ANIMAL", "TMP_PLANT",
            "TMP_ELEMENT", "TMP_CREATURE_DETAILS", "TMP_MYTHICALCREATURE", "TMP_CAVE_CREATURE", "TMP_CREATURE", "TMP_CAVE",
            "TMP_ABSTR_EMP", "TMP_FT_EMP", "TMP_PT_EMP", "TMP_CONTRACT_EMP", "TMP_READONLY",
            "BYTE_ITEM", "BYTE_ITEM_ATT", "TMP_WEAPON", "DIS_METRIC", "TMP_MATERIAL", "TMP_BPEM_TM_PRINC", "TMP_BPEM_TM_TASK",
            "TMP_COSTCENTER", "DIS_COMPONENT", "TMP_ACCOUNT", };

    public JPA1Base() {
        super("jpa1testmodel");
    }

    final protected String[] getClearableTableNames() {
        return CLEARABLE_TABLE_NAMES;
    }

    /**
     * Checks whether the given throwable is of type javax.persistence.PersistenceException, or otherwise if the throwable
     * contains a javax.persistence.PersistenceException somewhere in the cause stack.
     * 
     * @param e
     *            The throwable to check
     * @return <code>true</code> if the throwable is instance of or caused by javax.persistence.PersistenceException
     */
    protected final boolean checkForPersistenceException(Throwable e) {
        boolean contained = false;
        while (e != null) {
            if (e instanceof PersistenceException) {
                contained = true;
                break;
            }
            e = e.getCause();
        }
        return contained;
    }

}
