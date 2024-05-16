/*
 * -----------------------------------------------------------------------------
 * manually
 * Removed text manually
 * Removed text manually
 * -----------------------------------------------------------------------------
 */
package org.eclipse.persistence.testing.tests.junit.helper;

import java.util.List;

import org.eclipse.persistence.internal.helper.ConcurrencyManager;
import org.eclipse.persistence.internal.helper.ReadLockManager;
import org.eclipse.persistence.internal.helper.type.ReadLockAcquisitionMetadata;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for the class {@link org.eclipse.persistence.internal.helper.ReadLockManager}
 */
public class ReadLockAcquisitionMetadataTest {

    // Inner class
    /**
     * Unit test extension class to allows to create some hacky methods to access
     * and manipulate the state of the read lock manager.
     * Such as adding NULL records into the state.
     */
   public static class ReadLockManagerExtension extends ReadLockManager{

       /**
        * This is a dummy method hack our state maps with null entries.
        * Our expectation is that this should never happen in real life
        * but we have seen null pointer exceptions that seem to indicate that this is factuallz posisbly to happen
        * although we have understoor the underlying reason for the null entries.
        *
        * With this method we simulate corrupting our read data structures with null entries.
        */
       public void hackReadLockManagerToBeCorruptedWithNullRecordsItsState() {
           ConcurrencyManager cacheKeyNull = null;
           readLocks.add(cacheKeyNull);
           final Thread currentThread = Thread.currentThread();
           final long currentThreadId = currentThread.getId();
           ReadLockAcquisitionMetadata readLockAcquisitionMetadataNull = null;
           List<ReadLockAcquisitionMetadata> readLocksAcquiredDuringCurrentThreadList = mapThreadToReadLockAcquisitionMetadata.get(currentThreadId);
           readLocksAcquiredDuringCurrentThreadList.add(readLockAcquisitionMetadataNull);

       }


   }
   // helpver variables

   final ConcurrencyManager cacheKeyA = new CacheKey("cacheKeyA");
   final ConcurrencyManager cacheKeyB = new CacheKey("cacheKeyB");


   @Before
   public void before() {

   }


   @Test
   public void normalHappyPathLogicAddingAndRemovingMetadataIntoTheReadLockManager() {
       // SETUP:
       // basic variable initialization
       ReadLockManagerExtension testee = new ReadLockManagerExtension();


       // EXECUTE 1 - Add cache key A
       testee.addReadLock(cacheKeyA);

       // VERIFY 1

       Assert.assertEquals(1, testee.getReadLocks().size());
       Assert.assertTrue(testee.getReadLocks().contains(cacheKeyA));
       Assert.assertFalse(testee.getReadLocks().contains(cacheKeyB));

       Assert.assertEquals(1, testee.getMapThreadToReadLockAcquisitionMetadata().size());
       List<ReadLockAcquisitionMetadata> cacheKeyMetadataForCurrentThread =testee.getMapThreadToReadLockAcquisitionMetadata().get(Thread.currentThread().getId());
       Assert.assertEquals(1, cacheKeyMetadataForCurrentThread.size());

       Assert.assertTrue(cacheKeyA == cacheKeyMetadataForCurrentThread.get(0).getCacheKeyWhoseNumberOfReadersThreadIsIncrementing());
       Assert.assertFalse(cacheKeyB == cacheKeyMetadataForCurrentThread.get(0).getCacheKeyWhoseNumberOfReadersThreadIsIncrementing());


       // EXECUTE 2 - Add cache key B
       testee.addReadLock(cacheKeyB);

       // VERIFY 2

       Assert.assertEquals(2, testee.getReadLocks().size());
       Assert.assertTrue(testee.getReadLocks().contains(cacheKeyA));
       Assert.assertTrue(testee.getReadLocks().contains(cacheKeyB));

       Assert.assertEquals(1, testee.getMapThreadToReadLockAcquisitionMetadata().size());
       cacheKeyMetadataForCurrentThread =testee.getMapThreadToReadLockAcquisitionMetadata().get(Thread.currentThread().getId());
       Assert.assertEquals(2, cacheKeyMetadataForCurrentThread.size());

       // note: when we are adding, we are adding the entries to the HEAD of the list
       Assert.assertTrue(cacheKeyB == cacheKeyMetadataForCurrentThread.get(0).getCacheKeyWhoseNumberOfReadersThreadIsIncrementing());
       Assert.assertTrue(cacheKeyA == cacheKeyMetadataForCurrentThread.get(1).getCacheKeyWhoseNumberOfReadersThreadIsIncrementing());


       // EXECUTE 3 - Remove cache keys
       testee.removeReadLock(cacheKeyA);

       // VERIFY 3
       Assert.assertEquals(1, testee.getReadLocks().size());
       Assert.assertFalse(testee.getReadLocks().contains(cacheKeyA));
       Assert.assertTrue(testee.getReadLocks().contains(cacheKeyB));

       Assert.assertEquals(1, testee.getMapThreadToReadLockAcquisitionMetadata().size());
       cacheKeyMetadataForCurrentThread =testee.getMapThreadToReadLockAcquisitionMetadata().get(Thread.currentThread().getId());
       Assert.assertEquals(1, cacheKeyMetadataForCurrentThread.size());

       Assert.assertTrue(cacheKeyB == cacheKeyMetadataForCurrentThread.get(0).getCacheKeyWhoseNumberOfReadersThreadIsIncrementing());
       Assert.assertFalse(cacheKeyA == cacheKeyMetadataForCurrentThread.get(0).getCacheKeyWhoseNumberOfReadersThreadIsIncrementing());

       // EXECUTE 4 - Remove cache keys
       testee.removeReadLock(cacheKeyB);

       // VERIFY 4
       Assert.assertEquals(0, testee.getReadLocks().size());
       Assert.assertFalse(testee.getReadLocks().contains(cacheKeyA));
       Assert.assertFalse(testee.getReadLocks().contains(cacheKeyB));
       Assert.assertEquals(0, testee.getMapThreadToReadLockAcquisitionMetadata().size());


   }

   @Test
   public void testAddNullReadCacheKeyDoesNothing() {
       // SETUP:
       // basic variable initialization
       ReadLockManagerExtension testee = new ReadLockManagerExtension();
       ConcurrencyManager cacheKeyNull = null;

       // EXECUTE
       // try to add a null cache key to the map
       testee.addReadLock(cacheKeyNull);

       // VERIFY
       Assert.assertEquals(0, testee.getReadLocks().size());
       Assert.assertEquals(0, testee.getMapThreadToReadLockAcquisitionMetadata().size());
   }

   /**
    * The purpose of this unit test is to make sure that if for some unknown reason
    * we ever end up adding NULL as metadata to either the READ Lock manager
    * or to the VECTOR of read locks
    * that we can self heald and remove them from the map automatically.
    *
    */
    @Test
    public void testRemoveWhen_mapThreadToReadLockAcquisitionMetadata_containsNull() {
        // SETUP:
        // let us start by adding some entrires to the read lock manager
        ReadLockManagerExtension testee = new ReadLockManagerExtension();
        testee.addReadLock(cacheKeyA);
        testee.addReadLock(cacheKeyB);
        Assert.assertEquals(2, testee.getReadLocks().size());
        Assert.assertEquals(1, testee.getMapThreadToReadLockAcquisitionMetadata().size());
        List<ReadLockAcquisitionMetadata> cacheKeyMetadataForCurrentThread =testee.getMapThreadToReadLockAcquisitionMetadata().get(Thread.currentThread().getId());
        Assert.assertEquals(2, cacheKeyMetadataForCurrentThread.size());

        // SETUP:
        // now we are going to hack our state to put in here null entires both in the read locks map and in the list of metadata
        // Validate that that the maps are now properly hacked
        testee.hackReadLockManagerToBeCorruptedWithNullRecordsItsState();
        Assert.assertEquals(3, testee.getReadLocks().size());
        Assert.assertTrue( testee.getReadLocks().contains(null));
        cacheKeyMetadataForCurrentThread =testee.getMapThreadToReadLockAcquisitionMetadata().get(Thread.currentThread().getId());
        Assert.assertEquals(3, cacheKeyMetadataForCurrentThread.size());
        Assert.assertTrue( cacheKeyMetadataForCurrentThread.contains(null));

        // EXECUTE
        // Now we will try to REMOVE from the read lock manager a cache key that does not actually exist in the map
        // this MUST have the side effect of causing the code to loop over ALL OF THE read lock manager metadata and spot that we
        // have NULL records in the metadata array
        // in so doing the code should self-heal and get rid of all that garbage
        ConcurrencyManager cacheKeyNotExistingInTheReadLockManagerToCallFullLoopOverData = new CacheKey("cacheKeyNotExistingInTheReadLockManagerToCallFullLoopOverData");
        testee.removeReadLock(cacheKeyNotExistingInTheReadLockManagerToCallFullLoopOverData);

        // VERIFY that our code self healeded
        Assert.assertEquals(2, testee.getReadLocks().size());
        Assert.assertFalse( testee.getReadLocks().contains(null));
        cacheKeyMetadataForCurrentThread =testee.getMapThreadToReadLockAcquisitionMetadata().get(Thread.currentThread().getId());
        Assert.assertEquals(2, cacheKeyMetadataForCurrentThread.size());
        Assert.assertFalse( cacheKeyMetadataForCurrentThread.contains(null));

    }
}
