/*******************************************************************************
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.helper;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.persistence.config.SystemProperties;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.logging.*;

/**
 * Helper class to help us build diagnostic strings
 */
public class EclipseHelperUtil implements Serializable {
    
    public static final EclipseHelperUtil SINGLETON = new EclipseHelperUtil();
    
   
    /**
     * @return check if the user has specified a system property to control how long we are willing to wait before firing up an exception
     */
    public long getMaxAllowedSleepTimeMs() {
        long defaultMaxAllowedSleepTimeMs = 0;
        
        // (a) Check if a system property was provided by the user to control how long we tolerate before exploding thread waiting to release
        // deferred locks
        String eclipseLinkConcurrencyManagerMaxAllowedSleepTimeMs = System.getProperty(SystemProperties.CONCURRENCY_MANAGER_SLEEP_TIME);
        if(eclipseLinkConcurrencyManagerMaxAllowedSleepTimeMs != null) {
            try {
                return Long.parseLong(eclipseLinkConcurrencyManagerMaxAllowedSleepTimeMs.trim());
            } catch(Exception ignoreE) {
                return defaultMaxAllowedSleepTimeMs ;
            }            
        }
        return defaultMaxAllowedSleepTimeMs;
    }
    
    /**
     * Create a print of the ACTIVE locks associated to the DeferredLockManager
     */
    public String createStringWithSummaryOfActiveLocksOnThread(DeferredLockManager lockManager) {
        // (a) Make sure the lock manager being passed is not null
        if(lockManager == null) {
            return "DeferredLockManager - Listing of all Deferred Locks. Not currently created for current thread and cache key";
        }
        
        // (b) Try to build a string that lists all of the acitve locks on the thread
        StringBuilder sb = new StringBuilder();
        sb.append("DeferredLockManager - Listing of all Deferred Locks.");
        sb.append("\n\n");
        sb.append("Thread Name: ").append(Thread.currentThread().getName());
        sb.append("\n\n");
        
        // Loop over al of the active locks and print them
        long activeLock = 0;
        Vector activeLocks = lockManager.getActiveLocks();
        if (!activeLocks.isEmpty()) {
            for (Enumeration activeLocksEnum = activeLocks.elements();
                     activeLocksEnum.hasMoreElements();) {
                activeLock++;
                ConcurrencyManager manager = (ConcurrencyManager)activeLocksEnum.nextElement();
                String concurrencyManagerActiveLock = SINGLETON.createToStringExplainingOwnedCacheKey(manager);
                sb.append("ACTIVE LOCK NR: ").append("" + activeLock).append("ConcurrencyManager: ").append(concurrencyManagerActiveLock);
                sb.append("\n\n");
            }
        }
        return sb.toString();        
    }
    
    /**
     * 
     * @return A to string of the owned cache key
     */
    public String createToStringExplainingOwnedCacheKey(ConcurrencyManager concurrencyManager) {
        if(concurrencyManager instanceof CacheKey) {
            CacheKey cacheKey = (CacheKey) concurrencyManager;
            Object cacheKeykey = cacheKey.getKey();
            Object cacheKeyObject = cacheKey.getObject();
            String canonicalName = cacheKeyObject != null? cacheKeyObject.getClass().getCanonicalName():null;
            return String.format("ConcurrencyManager-CacheKey: %1$s ownerCacheKey (key,object, canonicalName) = (%2$s, %3$s, %4$s).", this, cacheKeykey, cacheKeyObject, canonicalName);
        } else {
            return String.format("ConcurrencyManager: %1$s. .", this);
        }
        
    }
    
    /**
     * Throw an interrupted exception if appears that eclipse link code is taking too long to release a deferred lock. 
     * @param whileStartDate
     *      the start date of the while tru loop for releasing a deferred lock 
     * @throws InterruptedException
     *  we fire an interupted exception to ensure that the code  blows up and releases all of the locks it had.
     */
    public void determineIfReleaseDeferredLockAppearsToBeDeadLocked(ConcurrencyManager concurrencyManager, final Date whileStartDate, DeferredLockManager lockManager) throws InterruptedException {
        // Determine if we believe to be dealing with a dead lock
        Thread currentThread = Thread.currentThread();
        final long maxAllowedSleepTime40ThousandMs = EclipseHelperUtil.SINGLETON.getMaxAllowedSleepTimeMs();
        Date whileCurrentDate = new Date();
        long elpasedTime = whileCurrentDate.getTime() - whileStartDate.getTime();
        if (elpasedTime > maxAllowedSleepTime40ThousandMs) {
            // We believe this is a dead lock so now we will log some information
            String ownedCacheKey = createToStringExplainingOwnedCacheKey(concurrencyManager);
           
            String errorMessageBase = String.format(
                    "RELEASE DEFERRED LOCK PROBLEM:  The release deffered log process has not managed to finish in: %1$s ms.%n"
                            + " (ownerCacheKey) = (%2$s). Current thread: %3$s. %n",
                    elpasedTime, ownedCacheKey, currentThread.getName());
            String errorMessageExplainingActiveLocksOnThread = EclipseHelperUtil.SINGLETON.createStringWithSummaryOfActiveLocksOnThread(lockManager);
            String errorMessage = errorMessageBase +  errorMessageExplainingActiveLocksOnThread;

            AbstractSessionLog.getLog().log(SessionLog.SEVERE, SessionLog.CACHE, errorMessage,
                    currentThread.getName());
            
            throw new InterruptedException(errorMessage);
        }
    }
    
}
