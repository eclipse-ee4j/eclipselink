/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.eis;

import java.util.*;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.mappings.*;

/**
 * INTERNAL:
 * Capture the changes for an ordered collection where
 * the entire collection is simply replaced if it has changed.
 */
public class EISOrderedCollectionChangeRecord extends CollectionChangeRecord implements org.eclipse.persistence.sessions.changesets.EISOrderedCollectionChangeRecord {

    /** The added stuff. */
    private Vector adds;

    /** The indexes into the new collection of the elements that were added. */
    private int[] addIndexes;

    /** The moved stuff. */
    private Vector moves;

    /** The index pairs of the elements that were moved (before and after indexes). */
    private int[][] moveIndexPairs;

    /** The removed stuff. */
    private Vector removes;

    /** The indexes into the old collection of the elements that were removed. */
    private int[] removeIndexes;

    /**
     * Construct a ChangeRecord that can be used to represent the changes to
     * an ordered collection.
     */
    public EISOrderedCollectionChangeRecord(ObjectChangeSet owner, String attributeName, DatabaseMapping mapping) {
        super();
        this.owner = owner;
        this.attribute = attributeName;
        this.mapping = mapping;
    }

    /**
     * Add an added change set.
     */
    public void addAddedChangeSet(Object changeSet, int index) {
        this.getAdds().addElement(changeSet);
        this.setAddIndexes(this.addTo(index, this.getAddIndexes()));
    }

    /**
     * Add an moved change set.
     */
    public void addMovedChangeSet(Object changeSet, int oldIndex, int newIndex) {
        this.getMoves().addElement(changeSet);
        int[] pair = new int[2];
        pair[0] = oldIndex;
        pair[1] = newIndex;
        this.setMoveIndexPairs(this.addTo(pair, this.getMoveIndexPairs()));
    }

    /**
     * Add an removed change set.
     */
    public void addRemovedChangeSet(Object changeSet, int index) {
        this.getRemoves().addElement(changeSet);
        this.setRemoveIndexes(this.addTo(index, this.getRemoveIndexes()));
    }

    /**
     * Add the int to the end of the array.
     * Return the new array.
     */
    private int[] addTo(int newInt, int[] oldArray) {
        int oldCount = oldArray.length;
        int[] newArray = new int[oldCount + 1];
        System.arraycopy(oldArray, 0, newArray, 0, oldCount);
        newArray[oldCount] = newInt;// zero-based index
        return newArray;
    }

    /**
     * Add the int[] to the end of the array.
     * Return the new array.
     */
    private int[][] addTo(int[] newInts, int[][] oldArray) {
        int oldCount = oldArray.length;
        int[][] newArray = new int[oldCount + 1][];
        System.arraycopy(oldArray, 0, newArray, 0, oldCount);
        newArray[oldCount] = newInts;// zero-based index
        return newArray;
    }

    /**
     * Return the specified add.
     */
    private Object getAdd(int index) {
        return this.getAdds().get(index);
    }

    /**
     * ADVANCED:
     * Return the indexes into the new collection of
     * the elements that were added.
     */
    public int[] getAddIndexes() {
        if (addIndexes == null) {
            addIndexes = new int[0];
        }
        return addIndexes;
    }

    /**
     * ADVANCED:
     * Return the entries for all the elements added to the new collection.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public Vector getAdds() {
        if (adds == null) {
            adds = new Vector(1);// keep it as small as possible
        }
        return adds;
    }

    /**
     * Return the index in the adds of the specified change set,
     * without triggering the instantiation of the collection.
     */
    private int getAddsIndexOf(Object changeSet) {
        if (adds == null) {
            return -1;
        }
        return adds.indexOf(changeSet);
    }

    /**
     * Return the number of adds, without triggering
     * the instantiation of the collection.
     */
    private int getAddsSize() {
        if (adds == null) {
            return 0;
        }
        return adds.size();
    }

    /**
     * Return the specified move.
     */
    private Object getMove(int index) {
        return this.getMoves().get(index);
    }

    /**
     * Return the specified "before" move index.
     */
    private int getBeforeMoveIndex(int index) {
        int[][] pairs = getMoveIndexPairs();
        return pairs[index][0];
    }

    /**
     * ADVANCED:
     * Return the indexes of the elements that were simply moved
     * within the collection.
     * Each element in the outer array is another two-element
     * array where the first entry [0] is the index of the object in
     * the old collection and the second entry [1] is the index
     * of the object in the new collection. These two indexes
     * can be equal.
     */
    public int[][] getMoveIndexPairs() {
        if (moveIndexPairs == null) {
            moveIndexPairs = new int[0][0];
        }
        return moveIndexPairs;
    }

    /**
     * ADVANCED:
     * Return the entries for all the elements that were simply shuffled
     * within the collection.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public Vector getMoves() {
        if (moves == null) {
            moves = new Vector(1);// keep it as small as possible
        }
        return moves;
    }

    /**
     * Return the index in the moves of the specified change set,
     * without triggering the instantiation of the collection.
     */
    private int getMovesIndexOf(Object changeSet) {
        if (moves == null) {
            return -1;
        }
        return moves.indexOf(changeSet);
    }

    /**
     * Return the number of moves, without triggering
     * the instantiation of the collection.
     */
    private int getMovesSize() {
        if (moves == null) {
            return 0;
        }
        return moves.size();
    }

    /**
     * ADVANCED:
     * Return the entries for all the elements in the new collection.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public Vector getNewCollection() {
        int newSize = this.getNewCollectionSize();
        Vector newCollection = new Vector(newSize);

        int[] localAddIndexes = addIndexes;
        if (localAddIndexes == null) {
            localAddIndexes = new int[0];
        }

        int[][] localMoveIndexPairs = moveIndexPairs;
        if (localMoveIndexPairs == null) {
            localMoveIndexPairs = new int[0][0];
        }

        int addIndex = 0;
        int moveIndex = 0;
        for (int i = 0; i < newSize; i++) {
            if ((addIndex < localAddIndexes.length) && (localAddIndexes[addIndex] == i)) {
                newCollection.add(this.getAdd(addIndex));
                addIndex++;
                continue;
            }
            if ((moveIndex < localMoveIndexPairs.length) && (localMoveIndexPairs[moveIndex][1] == i)) {
                newCollection.add(this.getMove(moveIndex));
                moveIndex++;
                continue;
            }
            throw new IllegalStateException(String.valueOf(i));
        }
        return newCollection;
    }

    /**
     * Return the number of elements in the new collection,
     * without triggering the instantiation of the collections.
     */
    private int getNewCollectionSize() {
        return this.getAddsSize() + this.getMovesSize();
    }

    /**
     * Return the specified remove index.
     */
    private int getRemoveIndex(int index) {
        return this.getRemoveIndexes()[index];
    }

    /**
     * ADVANCED:
     * Return the indexes into the old collection of
     * the elements that were removed.
     */
    public int[] getRemoveIndexes() {
        if (removeIndexes == null) {
            removeIndexes = new int[0];
        }
        return removeIndexes;
    }

    /**
     * ADVANCED:
     * Return the entries for all the elements removed from the old collection.
     * The contents of this collection is determined by the mapping that
     * populated it
     */
    public Vector getRemoves() {
        if (removes == null) {
            removes = new Vector(1);// keep it as small as possible
        }
        return removes;
    }

    /**
     * Return the index in the removes of the specified change set,
     * without triggering the instantiation of the collection.
     */
    private int getRemovesIndexOf(Object changeSet) {
        if (removes == null) {
            return -1;
        }
        return removes.indexOf(changeSet);
    }

    /**
     * Return whether any adds have been recorded with the change record.
     * Directly reference the instance variable, so as to not trigger the lazy instantiation.
     */
    private boolean hasAdds() {
        return (addIndexes != null) && (addIndexes.length != 0);
    }

    /**
     * Return whether any changes have been recorded with the change record.
     */
    public boolean hasChanges() {
        if (this.hasAdds() || this.hasRemoves() || this.getOwner().isNew()) {
            return true;
        }

        // BUG#.... the moves always contain everything, must check if any indexes are different.
        if (hasMoves()) {
            for (int index = 0; index < moveIndexPairs.length; index++) {
                if (moveIndexPairs[index][0] != moveIndexPairs[index][1]) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return whether any moves have been recorded with the change record.
     * Directly reference the instance variable, so as to not trigger the lazy instantiation.
     */
    private boolean hasMoves() {
        return (moveIndexPairs != null) && (moveIndexPairs.length != 0);
    }

    /**
     * Return whether any removes have been recorded with the change record.
     * Directly reference the instance variable, so as to not trigger the lazy instantiation.
     */
    private boolean hasRemoves() {
        return (removeIndexes != null) && (removeIndexes.length != 0);
    }

    /**
     * Remove the specified slot from the array.
     * Return the new array.
     */
    private int[] removeFrom(int removeIndex, int[] oldArray) {
        int oldCount = oldArray.length;
        int[] newArray = new int[oldCount - 1];
        System.arraycopy(oldArray, 0, newArray, 0, removeIndex);
        System.arraycopy(oldArray, removeIndex + 1, newArray, removeIndex, oldCount - removeIndex - 1);
        return newArray;
    }

    /**
     * Remove the specified slot from the array.
     * Return the new array.
     */
    private int[][] removeFrom(int removeIndex, int[][] oldArray) {
        int oldCount = oldArray.length;
        int[][] newArray = new int[oldCount - 1][];
        System.arraycopy(oldArray, 0, newArray, 0, removeIndex);
        System.arraycopy(oldArray, removeIndex + 1, newArray, removeIndex, oldCount - removeIndex - 1);
        return newArray;
    }

    /**
     * The specified change set was added earlier;
     * cancel it out.
     */
    private void cancelAddedChangeSet(Object changeSet) {
        int changeSetIndex = this.getAddsIndexOf(changeSet);
        if (changeSetIndex == -1) {
            throw new IllegalStateException(changeSet.toString());
        }
        this.getAdds().remove(changeSetIndex);
        this.setAddIndexes(this.removeFrom(changeSetIndex, this.getAddIndexes()));
    }

    /**
     * Attempt to remove the specified change set
     * from the collection of moved change sets.
     * Return true if the change set was moved earlier
     * and was successfully removed.
     */
    private boolean removeMovedChangeSet(Object changeSet) {
        int changeSetIndex = this.getMovesIndexOf(changeSet);
        if (changeSetIndex == -1) {
            return false;
        }
        this.getMoves().remove(changeSetIndex);
        int beforeMoveIndex = this.getBeforeMoveIndex(changeSetIndex);
        this.setMoveIndexPairs(this.removeFrom(changeSetIndex, this.getMoveIndexPairs()));
        // now move the change set over to the collection of removes
        this.addRemovedChangeSet(changeSet, beforeMoveIndex);
        return true;
    }

    /**
     * Attempt to restore the specified change set.
     * Return true if the change set was removed earlier
     * and was successfully restored to the end of
     * the collection.
     */
    private boolean restoreRemovedChangeSet(Object changeSet) {
        int changeSetIndex = this.getRemovesIndexOf(changeSet);
        if (changeSetIndex == -1) {
            return false;
        }
        this.getRemoves().remove(changeSetIndex);
        int removeIndex = this.getRemoveIndex(changeSetIndex);
        this.setRemoveIndexes(this.removeFrom(changeSetIndex, this.getRemoveIndexes()));
        // now move the change set over to the collection of moves
        this.addMovedChangeSet(changeSet, removeIndex, this.getNewCollectionSize());
        return true;
    }

    /**
     * Set the indexes into the new collection of
     * the elements that were added.
     */
    private void setAddIndexes(int[] addIndexes) {
        this.addIndexes = addIndexes;
    }

    /**
     * Set the indexes of the elements that were moved.
     */
    private void setMoveIndexPairs(int[][] moveIndexPairs) {
        this.moveIndexPairs = moveIndexPairs;
    }

    /**
     * Set the indexes into the old collection of
     * the elements that were removed.
     */
    private void setRemoveIndexes(int[] removeIndexes) {
        this.removeIndexes = removeIndexes;
    }

    /**
     * Add a change set after it has been applied.
     */
    public void simpleAddChangeSet(Object changeSet) {
        // check whether the change set was removed earlier
        if (!this.restoreRemovedChangeSet(changeSet)) {
            // the change set is tacked on the end of the new collection
            this.addAddedChangeSet(changeSet, this.getNewCollectionSize());
        }
    }

    /**
     * Remove a change set after it has been applied.
     */
    public void simpleRemoveChangeSet(Object changeSet) {
        // the change set must have been either moved or added earlier
        if (!this.removeMovedChangeSet(changeSet)) {
            this.cancelAddedChangeSet(changeSet);
        }
    }
}
