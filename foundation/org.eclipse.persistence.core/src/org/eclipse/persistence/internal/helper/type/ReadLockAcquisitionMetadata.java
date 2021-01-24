/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.internal.helper.type;

import org.eclipse.persistence.internal.helper.ConcurrencyManager;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Metadata about ReadLock acquisition
 */
public class ReadLockAcquisitionMetadata implements Serializable {

    private static final AtomicLong READ_LOCK_GLOBAL_ACQUISITION_NUMBER = new AtomicLong(0);

    /**
     * Each time a read lock is acquired we generate a new number for this read lock acquision.
     */
    private final long readLockGlobalAcquisitionNumber = READ_LOCK_GLOBAL_ACQUISITION_NUMBER.incrementAndGet();

    /**
     * Date when this event of trying to acquire a read lock has take place.
     */
    private final Date dateOfReadLockAcquisition = new Date();

    /**
     * This is the cache key whose number of readers is about to be incremented
     */
    private final ConcurrencyManager cacheKeyWhoseNumberOfReadersThreadIsIncrementing;

    /**
     * This is the current number of readers on the cache key just before we execute the increment.
     */
    private final int numberOfReadersOnCacheKeyBeforeIncrementingByOne;

    /**
     * If enabled we will trace the exact code location that did the increment of number of readers.
     */
    private final String currentThreadStackTraceInformation;

    /**
     * how expensive was it to acquire the stack trace. This might be too expensive for being used live.
     */
    private final long currentThreadStackTraceInformationCpuTimeCostMs;

    /**
     * Create a new ReadLockAcquisitionMetadata.
     *
     * @param cacheKeyWhoseNumberOfReadersThreadIsIncrementing
     * @param numberOfReadersOnCacheKeyBeforeIncrementingByOne
     * @param currentThreadStackTraceInformation
     */
    public ReadLockAcquisitionMetadata(
            ConcurrencyManager cacheKeyWhoseNumberOfReadersThreadIsIncrementing,
            int numberOfReadersOnCacheKeyBeforeIncrementingByOne,
            String currentThreadStackTraceInformation, long currentThreadStackTraceInformationCpuTimeCostMs) {
        super();
        this.cacheKeyWhoseNumberOfReadersThreadIsIncrementing = cacheKeyWhoseNumberOfReadersThreadIsIncrementing;
        this.numberOfReadersOnCacheKeyBeforeIncrementingByOne = numberOfReadersOnCacheKeyBeforeIncrementingByOne;
        this.currentThreadStackTraceInformation = currentThreadStackTraceInformation;
        this.currentThreadStackTraceInformationCpuTimeCostMs = currentThreadStackTraceInformationCpuTimeCostMs;
    }

    /** Getter for {@link #readLockGlobalAcquisitionNumber} */
    public long getReadLockGlobalAcquisitionNumber() {
        return readLockGlobalAcquisitionNumber;
    }

    /** Getter for {@link #dateOfReadLockAcquisition} */
    public Date getDateOfReadLockAcquisition() {
        return dateOfReadLockAcquisition;
    }

    /** Getter for {@link #cacheKeyWhoseNumberOfReadersThreadIsIncrementing} */
    public ConcurrencyManager getCacheKeyWhoseNumberOfReadersThreadIsIncrementing() {
        return cacheKeyWhoseNumberOfReadersThreadIsIncrementing;
    }

    /** Getter for {@link #numberOfReadersOnCacheKeyBeforeIncrementingByOne} */
    public int getNumberOfReadersOnCacheKeyBeforeIncrementingByOne() {
        return numberOfReadersOnCacheKeyBeforeIncrementingByOne;
    }

    /** Getter for {@link #currentThreadStackTraceInformation} */
    public String getCurrentThreadStackTraceInformation() {
        return currentThreadStackTraceInformation;
    }


    /** Getter for {@link #currentThreadStackTraceInformationCpuTimeCostMs} */
    public long getCurrentThreadStackTraceInformationCpuTimeCostMs() {
        return currentThreadStackTraceInformationCpuTimeCostMs;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (readLockGlobalAcquisitionNumber ^ (readLockGlobalAcquisitionNumber >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ReadLockAcquisitionMetadata other = (ReadLockAcquisitionMetadata) obj;
        if (readLockGlobalAcquisitionNumber != other.readLockGlobalAcquisitionNumber) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ReadLockAcquisitionMetadataDto [\nreadLockGlobalAcquisitionNumber=" + readLockGlobalAcquisitionNumber
                + "\n, dateOfReadLockAcquisition=" + dateOfReadLockAcquisition
                + "\n, cacheKeyWhoseNumberOfReadersThreadIsIncrementing=" + cacheKeyWhoseNumberOfReadersThreadIsIncrementing
                + "\n, numberOfReadersOnCacheKeyBeforeIncrementingByOne=" + numberOfReadersOnCacheKeyBeforeIncrementingByOne
                + "\n, currentThreadStackTraceInformation=" + currentThreadStackTraceInformation
                + "\n, currentThreadStackTraceInformationCpuTimeCostMs=" + currentThreadStackTraceInformationCpuTimeCostMs + "]\n";
    }
}
