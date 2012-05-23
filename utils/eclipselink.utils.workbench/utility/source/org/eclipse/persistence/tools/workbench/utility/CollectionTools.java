/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.utility;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;


public final class CollectionTools {

	/**
	 * Return a new array that contains the elements in the
	 * specified array followed by the specified object to be added.
	 * java.util.Arrays#add(Object[] array, Object o)
	 */
	public static Object[] add(Object[] array, Object o) {
		int len = array.length;
		Object[] result = (Object[]) Array.newInstance(array.getClass().getComponentType(), len + 1);
		System.arraycopy(array, 0, result, 0, len);
		result[len] = o;
		return result;
	}

	/**
	 * Return a new array that contains the elements in the
	 * specified array with the specified object added at the specified index.
	 * java.util.Arrays#add(Object[] array, int index, Object o)
	 */
	public static Object[] add(Object[] array, int index, Object o) {
		int len = array.length;
		Object[] result = (Object[]) Array.newInstance(array.getClass().getComponentType(), len + 1);
		if (index > 0) {
			System.arraycopy(array, 0, result, 0, index);
		}
		result[index] = o;
		if (len > index) {
			System.arraycopy(array, index, result, index + 1, len - index);
		}
		return result;
	}

	/**
	 * Return a new array that contains the elements in the
	 * specified array followed by the specified value to be added.
	 * java.util.Arrays#add(char[] array, char value)
	 */
	public static char[] add(char[] array, char value) {
		int len = array.length;
		char[] result = new char[len + 1];
		System.arraycopy(array, 0, result, 0, len);
		result[len] = value;
		return result;
	}
	
	/**
	 * Return a new array that contains the elements in the
	 * specified array with the specified value added at the specified index.
	 * java.util.Arrays#add(char[] array, int index, char value)
	 */
	public static char[] add(char[] array, int index, char value) {
		int len = array.length;
		char[] result = new char[len + 1];
		System.arraycopy(array, 0, result, 0, index);
		result[index] = value;
		System.arraycopy(array, index, result, index + 1, len - index);
		return result;
	}

	/**
	 * Return a new array that contains the elements in the
	 * specified array followed by the specified value to be added.
	 * java.util.Arrays#add(int[] array, int value)
	 */
	public static int[] add(int[] array, int value) {
		int len = array.length;
		int[] result = new int[len + 1];
		System.arraycopy(array, 0, result, 0, len);
		result[len] = value;
		return result;
	}
	
	/**
	 * Return a new array that contains the elements in the
	 * specified array with the specified value added at the specified index.
	 * java.util.Arrays#add(int[] array, int index, int value)
	 */
	public static int[] add(int[] array, int index, int value) {
		int len = array.length;
		int[] result = new int[len + 1];
		System.arraycopy(array, 0, result, 0, index);
		result[index] = value;
		System.arraycopy(array, index, result, index + 1, len - index);
		return result;
	}

	/**
	 * Add all the elements returned by the specified iterator
	 * to the specified collection.
	 * Return whether the collection changed as a result.
	 * java.util.Collection#addAll(java.util.Iterator iterator)
	 */
	public static boolean addAll(Collection collection, Iterator iterator) {
		boolean modified = false;
		while (iterator.hasNext()) {
			modified |= collection.add(iterator.next());
		}
		return modified;
	}
	
	/**
	 * Add all the elements in the specified array
	 * to the specified collection.
	 * Return whether the collection changed as a result.
	 * java.util.Collection#addAll(Object[] array)
	 */
	public static boolean addAll(Collection collection, Object[] array) {
		boolean modified = false;
		int len = array.length;
		for (int i = 0; i < len; i++) {
			modified |= collection.add(array[i]);
		}
		return modified;
	}

	/**
	 * Return a new array that contains the elements in the
	 * specified array followed by the elements
	 * in the specified collection.
	 * java.util.Arrays#addAll(Object[] array, java.util.Collection c)
	 */
	public static Object[] addAll(Object[] array, Collection c) {
		int len = array.length;
		Object[] result = (Object[]) Array.newInstance(array.getClass().getComponentType(), array.length + c.size());
		System.arraycopy(array, 0, result, 0, len);
		int i = len;
		for (Iterator stream = c.iterator(); stream.hasNext(); i++) {
			result[i] = stream.next();
		}
		return result;
	}

	/**
	 * Return a new array that contains the elements in the
	 * specified array followed by the elements
	 * in the specified iterator.
	 * java.util.Arrays#addAll(Object[] array, java.util.Iterator iterator)
	 */
	public static Object[] addAll(Object[] array, Iterator iterator) {
		return addAll(array, list(iterator));
	}

	/**
	 * Return a new array that contains the elements in the
	 * specified array 1 followed by the elements
	 * in the specified array 2.
	 * java.util.Arrays#addAll(Object[] array1, Object[] array2)
	 */
	public static Object[] addAll(Object[] array1, Object[] array2) {
		int len1 = array1.length;
		int len2 = array2.length;
		Object[] result = (Object[]) Array.newInstance(array1.getClass().getComponentType(), len1 + len2);
		System.arraycopy(array1, 0, result, 0, len1);
		System.arraycopy(array2, 0, result, len1, len2);
		return result;
	}

	/**
	 * Return a new array that contains the elements in the
	 * first specified array with the objects in the second
	 * specified array added at the specified index.
	 * java.util.Arrays#add(Object[] array1, int index, Object[] array2)
	 */
	public static Object[] addAll(Object[] array1, int index, Object[] array2) {
		int len1 = array1.length;
		int len2 = array2.length;
		Object[] result = (Object[]) Array.newInstance(array1.getClass().getComponentType(), len1 + len2);
		System.arraycopy(array1, 0, result, 0, index);
		System.arraycopy(array2, 0, result, index, len2);
		System.arraycopy(array1, index, result, index + len2, len1 - index);
		return result;
	}

	/**
	 * Return a new array that contains the elements in the
	 * specified array 1 followed by the elements
	 * in the specified array 2.
	 * java.util.Arrays#addAll(char[] array1, char[] array2)
	 */
	public static char[] addAll(char[] array1, char[] array2) {
		int len1 = array1.length;
		int len2 = array2.length;
		char[] result = new char[len1 + len2];
		System.arraycopy(array1, 0, result, 0, len1);
		System.arraycopy(array2, 0, result, len1, len2);
		return result;
	}

	/**
	 * Return a new array that contains the elements in the
	 * first specified array with the objects in the second
	 * specified array added at the specified index.
	 * java.util.Arrays#add(char[] array1, int index, char[] array2)
	 */
	public static char[] addAll(char[] array1, int index, char[] array2) {
		int len1 = array1.length;
		int len2 = array2.length;
		char[] result = new char[len1 + len2];
		System.arraycopy(array1, 0, result, 0, index);
		System.arraycopy(array2, 0, result, index, len2);
		System.arraycopy(array1, index, result, index + len2, len1 - index);
		return result;
	}

	/**
	 * Return a new array that contains the elements in the
	 * specified array 1 followed by the elements
	 * in the specified array 2.
	 * java.util.Arrays#addAll(int[] array1, int[] array2)
	 */
	public static int[] addAll(int[] array1, int[] array2) {
		int len1 = array1.length;
		int len2 = array2.length;
		int[] result = new int[len1 + len2];
		System.arraycopy(array1, 0, result, 0, len1);
		System.arraycopy(array2, 0, result, len1, len2);
		return result;
	}

	/**
	 * Return a new array that contains the elements in the
	 * first specified array with the objects in the second
	 * specified array added at the specified index.
	 * java.util.Arrays#add(int[] array1, int index, int[] array2)
	 */
	public static int[] addAll(int[] array1, int index, int[] array2) {
		int len1 = array1.length;
		int len2 = array2.length;
		int[] result = new int[len1 + len2];
		System.arraycopy(array1, 0, result, 0, index);
		System.arraycopy(array2, 0, result, index, len2);
		System.arraycopy(array1, index, result, index + len2, len1 - index);
		return result;
	}

	/**
	 * Return an array corresponding to the specified iterator.
	 * @see java.util.Collection#toArray()
	 * java.util.Iterator#toArray()
	 */
	public static Object[] array(Iterator iterator) {
		return list(iterator).toArray();
	}
	
	/**
	 * Return an array corresponding to the specified iterator;
	 * the runtime type of the returned array is that of the specified array.
	 * If the collection fits in the specified array, it is returned therein.
	 * Otherwise, a new array is allocated with the runtime type of the
	 * specified array and the size of this collection.
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 * java.util.Iterator#toArray(Object[])
	 */
	public static Object[] array(Iterator iterator, Object[] array) {
		return list(iterator).toArray(array);
	}
	
	/**
	 * Return a bag corresponding to the specified enumeration.
	 * HashBag(java.util.Enumeration enumeration)
	 */
	public static Bag bag(Enumeration enumeration) {
		Bag bag = new HashBag();
		while (enumeration.hasMoreElements()) {
			bag.add(enumeration.nextElement());
		}
		return bag;
	}	
	
	/**
	 * Return a bag corresponding to the specified iterator.
	 * HashBag(java.util.Iterator iterator)
	 */
	public static Bag bag(Iterator iterator) {
		Bag bag = new HashBag();
		while (iterator.hasNext()) {
			bag.add(iterator.next());
		}
		return bag;
	}
	
	/**
	 * Return a bag corresponding to the specified array.
	 * HashBag(Object[] array)
	 */
	public static Bag bag(Object[] array) {
		Bag bag = new HashBag(array.length);
		for (int i = array.length; i-- > 0; ) {
			bag.add(array[i]);
		}
		return bag;
	}
	
	/**
	 * Return a collection corresponding to the specified enumeration.
	 */
	public static Collection collection(Enumeration enumeration) {
		return bag(enumeration);
	}
	
	/**
	 * Return a collection corresponding to the specified iterator.
	 */
	public static Collection collection(Iterator iterator) {
		return bag(iterator);
	}
	
	/**
	 * Return a collection corresponding to the specified array.
	 */
	public static Collection collection(Object[] array) {
		return bag(array);
	}

	/**
	 * Return whether the specified enumeration contains the
	 * specified element.
	 * java.util.Enumeration#contains(Object o)
	 */
	public static boolean contains(Enumeration enumeration, Object o) {
		if (o == null) {
			while (enumeration.hasMoreElements()) {
				if (enumeration.nextElement() == null) {
					return true;
				}
			}
		} else {
			while (enumeration.hasMoreElements()) {
				if (o.equals(enumeration.nextElement())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Return whether the specified iterator contains the
	 * specified element.
	 * java.util.Iterator#contains(Object o)
	 */
	public static boolean contains(Iterator iterator, Object o) {
		if (o == null) {
			while (iterator.hasNext()) {
				if (iterator.next() == null) {
					return true;
				}
			}
		} else {
			while (iterator.hasNext()) {
				if (o.equals(iterator.next())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Return whether the specified array contains the
	 * specified element.
	 * java.util.Arrays#contains(Object[] array, Object o)
	 */
	public static boolean contains(Object[] array, Object o) {
		if (o == null) {
			for (int i = array.length; i-- > 0; ) {
				if (array[i] == null) {
					return true;
				}
			}
		} else {
			for (int i = array.length; i-- > 0; ) {
				if (o.equals(array[i])) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Return whether the specified array contains the
	 * specified element.
	 * java.util.Arrays#contains(char[] array, char value)
	 */
	public static boolean contains(char[] array, char value) {
		for (int i = array.length; i-- > 0; ) {
			if (array[i] == value) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return whether the specified array contains the
	 * specified element.
	 * java.util.Arrays#contains(int[] array, int value)
	 */
	public static boolean contains(int[] array, int value) {
		for (int i = array.length; i-- > 0; ) {
			if (array[i] == value) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return whether the specified collection contains all of the
	 * elements in the specified iterator.
	 * java.util.Collection#containsAll(java.util.Iterator iterator)
	 */
	public static boolean containsAll(Collection collection, Iterator iterator) {
		while (iterator.hasNext()) {
			if ( ! collection.contains(iterator.next())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Return whether the specified collection contains all of the
	 * elements in the specified array.
	 * java.util.Collection#containsAll(Object[] array)
	 */
	public static boolean containsAll(Collection collection, Object[] array) {
		for (int i = array.length; i-- > 0; ) {
			if ( ! collection.contains(array[i])) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Return whether the specified iterator contains all of the
	 * elements in the specified collection.
	 * java.util.Iterator#containsAll(java.util.Collection collection)
	 */
	public static boolean containsAll(Iterator iterator, Collection collection) {
		return collection(iterator).containsAll(collection);
	}
	
	/**
	 * Return whether the specified iterator 1 contains all of the
	 * elements in the specified iterator 2.
	 * java.util.Iterator#containsAll(java.util.Iterator iterator)
	 */
	public static boolean containsAll(Iterator iterator1, Iterator iterator2) {
		return containsAll(collection(iterator1), iterator2);
	}
	
	/**
	 * Return whether the specified iterator contains all of the
	 * elements in the specified array.
	 * java.util.Iterator#containsAll(Object[] array)
	 */
	public static boolean containsAll(Iterator iterator, Object[] array) {
		return containsAll(collection(iterator), array);
	}
	
	/**
	 * Return whether the specified array contains all of the
	 * elements in the specified collection.
	 * java.util.Arrays#containsAll(Object[] array, java.util.Collection collection)
	 */
	public static boolean containsAll(Object[] array, Collection collection) {
		return containsAll(array, collection.iterator());
	}
	
	/**
	 * Return whether the specified array contains all of the
	 * elements in the specified iterator.
	 * java.util.Arrays#containsAll(Object[] array, java.util.Iterator iterator)
	 */
	public static boolean containsAll(Object[] array, Iterator iterator) {
		while (iterator.hasNext()) {
			if ( ! contains(array, iterator.next())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Return whether the specified array 1 contains all of the
	 * elements in the specified array 2.
	 * java.util.Arrays#containsAll(Object[] array1, Object[] array2)
	 */
	public static boolean containsAll(Object[] array1, Object[] array2) {
		for (int i = array2.length; i-- > 0; ) {
			if ( ! contains(array1, array2[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return whether the specified array 1 contains all of the
	 * elements in the specified array 2.
	 * java.util.Arrays#containsAll(char[] array1, char[] array2)
	 */
	public static boolean containsAll(char[] array1, char[] array2) {
		for (int i = array2.length; i-- > 0; ) {
			if ( ! contains(array1, array2[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return whether the specified array 1 contains all of the
	 * elements in the specified array 2.
	 * java.util.Arrays#containsAll(int[] array1, int[] array2)
	 */
	public static boolean containsAll(int[] array1, int[] array2) {
		for (int i = array2.length; i-- > 0; ) {
			if ( ! contains(array1, array2[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return the index of the first elements in the specified
	 * arrays that are different, beginning at the end.
	 * If the arrays are identical, return -1.
	 * If the arrays are different sizes, return the index of the
	 * last element in the longer array.
	 * Use the elements' #equals() method to compare the
	 * elements.
	 */
	public static int diffEnd(Object[] array1, Object[] array2) {
		return diffEnd(Arrays.asList(array1), Arrays.asList(array2));
	}

	/**
	 * Return the index of the first elements in the specified
	 * lists that are different, beginning at the end.
	 * If the lists are identical, return -1.
	 * If the lists are different sizes, return the index of the
	 * last element in the longer list.
	 * Use the elements' #equals() method to compare the
	 * elements.
	 */
	public static int diffEnd(List list1, List list2) {
		int size1 = list1.size();
		int size2 = list2.size();
		if (size1 != size2) {
			return Math.max(size1, size2) - 1;
		}
		int end = size1 - 1;
		while (end > -1) {
			Object e = list1.get(end);
			if (e == null) {
				if (list2.get(end) == null) {
					end--;
				} else {
					return end;
				}
			} else {
				if (e.equals(list2.get(end))) {
					end--;
				} else {
					return end;
				}
			}
		}
		return end;
	}

	/**
	 * Return the range of elements in the specified
	 * arrays that are different.
	 * If the arrays are identical, return [size, -1].
	 * Use the elements' #equals() method to compare the
	 * elements.
	 * @see #diffStart(Object[], Object[])
	 * @see #diffEnd(Object[], Object[])
	 */
	public static Range diffRange(Object[] array1, Object[] array2) {
		return diffRange(Arrays.asList(array1), Arrays.asList(array2));
	}

	/**
	 * Return the range of elements in the specified
	 * arrays that are different.
	 * If the arrays are identical, return [size, -1].
	 * Use the elements' #equals() method to compare the
	 * elements.
	 * @see #diffStart(java.util.List, java.util.List)
	 * @see #diffEnd(java.util.List, java.util.List)
	 */
	public static Range diffRange(List list1, List list2) {
		int end = diffEnd(list1, list2);
		if (end == -1) {
			// the lists are identical, the start is the size of the two lists
			return new Range(list1.size(), end);
		}
		// the lists are different, calculate the start of the range
		return new Range(diffStart(list1, list2), end);
	}

	/**
	 * Return the index of the first elements in the specified
	 * arrays that are different. If the arrays are identical, return
	 * the size of the two arrays (i.e. one past the last index).
	 * If the arrays are different sizes and all the elements in
	 * the shorter array match their corresponding elements in
	 * the longer array, return the size of the shorter array
	 * (i.e. one past the last index of the shorter array).
	 * Use the elements' #equals() method to compare the
	 * elements.
	 */
	public static int diffStart(Object[] array1, Object[] array2) {
		return diffStart(Arrays.asList(array1), Arrays.asList(array2));
	}

	/**
	 * Return the index of the first elements in the specified
	 * lists that are different. If the lists are identical, return
	 * the size of the two lists (i.e. one past the last index).
	 * If the lists are different sizes and all the elements in
	 * the shorter list match their corresponding elements in
	 * the longer list, return the size of the shorter list
	 * (i.e. one past the last index of the shorter list).
	 * Use the elements' #equals() method to compare the
	 * elements.
	 */
	public static int diffStart(List list1, List list2) {
		int end = Math.min(list1.size(), list2.size());
		int start = 0;
		while (start < end) {
			Object e = list1.get(start);
			if (e == null) {
				if (list2.get(start) == null) {
					start++;
				} else {
					return start;
				}
			} else {
				if (e.equals(list2.get(start))) {
					start++;
				} else {
					return start;
				}
			}
		}
		return start;
	}

	/**
	 * Return whether the specified iterators return equal elements.
	 * java.util.ListIterator#equals(java.util.ListIterator iterator)
	 */
	public static boolean equals(ListIterator iterator1, ListIterator iterator2) {
		while (iterator1.hasNext() && iterator2.hasNext()) {
			Object o1 = iterator1.next();
			Object o2 = iterator2.next();
			if ( ! (o1 == null ? o2 == null : o1.equals(o2))) {
				return false;
			}
		}
		return ! (iterator1.hasNext() || iterator2.hasNext());
	}

	/**
	 * Return the element corresponding to the specified index
	 * in the specified iterator.
	 * java.util.ListIterator#get(int index)
	 */
	public static Object get(ListIterator iterator, int index) {
		while (iterator.hasNext()) {
			Object next = iterator.next();
			if (iterator.previousIndex() == index) {
				return next;
			}
		}
		throw new IndexOutOfBoundsException(String.valueOf(index) + ':' + String.valueOf(iterator.previousIndex()));
	}
	
	/**
	 * Return whether the specified arrays contain the same elements.
	 * java.util.Arrays#identical(Object[] array1, Object[] array2)
	 */
	public static boolean identical(Object[] array1, Object[] array2) {
		if (array1 == array2) {
			return true;
		}
		if (array1 == null || array2 == null) {
			return false;
		}
		int length = array1.length;
		if (array2.length != length) {
			return false;
		}
		for (int i = length; i-- > 0; ) {
			if (array1[i] != array2[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return whether the specified iterators return the same elements.
	 * java.util.ListIterator#identical(java.util.ListIterator iterator)
	 */
	public static boolean identical(ListIterator iterator1, ListIterator iterator2) {
		while (iterator1.hasNext() && iterator2.hasNext()) {
			if (iterator1.next() != iterator2.next()) {
				return false;
			}
		}
		return ! (iterator1.hasNext() || iterator2.hasNext());
	}

	/**
	 * Return the index of the first elements in the specified
	 * arrays that are different, beginning at the end.
	 * If the arrays are identical, return -1.
	 * If the arrays are different sizes, return the index of the
	 * last element in the longer array.
	 * Use object identity to compare the elements.
	 */
	public static int identityDiffEnd(Object[] array1, Object[] array2) {
		return identityDiffEnd(Arrays.asList(array1), Arrays.asList(array2));
	}

	/**
	 * Return the index of the first elements in the specified
	 * lists that are different, beginning at the end.
	 * If the lists are identical, return -1.
	 * If the lists are different sizes, return the index of the
	 * last element in the longer list.
	 * Use object identity to compare the elements.
	 */
	public static int identityDiffEnd(List list1, List list2) {
		int size1 = list1.size();
		int size2 = list2.size();
		if (size1 != size2) {
			return Math.max(size1, size2) - 1;
		}
		int end = size1 - 1;
		while (end > -1) {
			if (list1.get(end) == list2.get(end)) {
				end--;
			} else {
				return end;
			}
		}
		return end;
	}

	/**
	 * Return the range of elements in the specified
	 * arrays that are different.
	 * If the arrays are identical, return [size, -1].
	 * Use object identity to compare the elements.
	 * @see #identityDiffStart(Object[], Object[])
	 * @see #identityDiffEnd(Object[], Object[])
	 */
	public static Range identityDiffRange(Object[] array1, Object[] array2) {
		return identityDiffRange(Arrays.asList(array1), Arrays.asList(array2));
	}

	/**
	 * Return the range of elements in the specified
	 * arrays that are different.
	 * If the arrays are identical, return [size, -1].
	 * Use object identity to compare the elements.
	 * @see #identityDiffStart(java.util.List, java.util.List)
	 * @see #identityDiffEnd(java.util.List, java.util.List)
	 */
	public static Range identityDiffRange(List list1, List list2) {
		int end = identityDiffEnd(list1, list2);
		if (end == -1) {
			// the lists are identical, the start is the size of the two lists
			return new Range(list1.size(), end);
		}
		// the lists are different, calculate the start of the range
		return new Range(identityDiffStart(list1, list2), end);
	}

	/**
	 * Return the index of the first elements in the specified
	 * arrays that are different. If the arrays are identical, return
	 * the size of the two arrays (i.e. one past the last index).
	 * If the arrays are different sizes and all the elements in
	 * the shorter array match their corresponding elements in
	 * the longer array, return the size of the shorter array
	 * (i.e. one past the last index of the shorter array).
	 * Use object identity to compare the elements.
	 */
	public static int identityDiffStart(Object[] array1, Object[] array2) {
		return identityDiffStart(Arrays.asList(array1), Arrays.asList(array2));
	}

	/**
	 * Return the index of the first elements in the specified
	 * lists that are different. If the lists are identical, return
	 * the size of the two lists (i.e. one past the last index).
	 * If the lists are different sizes and all the elements in
	 * the shorter list match their corresponding elements in
	 * the longer list, return the size of the shorter list
	 * (i.e. one past the last index of the shorter list).
	 * Use object identity to compare the elements.
	 */
	public static int identityDiffStart(List list1, List list2) {
		int end = Math.min(list1.size(), list2.size());
		int start = 0;
		while (start < end) {
			if (list1.get(start) == list2.get(start)) {
				start++;
			} else {
				return start;
			}
		}
		return start;
	}

	/**
	 * Return the index of the first occurrence of the
	 * specified element in the specified iterator,
	 * or return -1 if there is no such index.
	 * java.util.ListIterator#indexOf(Object o)
	 */
	public static int indexOf(ListIterator iterator, Object o) {
		if (o == null) {
			for (int i = 0; iterator.hasNext(); i++) {
				if (iterator.next() == null) {
					return i;
				}
			}
		} else {
			for (int i = 0; iterator.hasNext(); i++) {
				if (o.equals(iterator.next())) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Return the index of the first occurrence of the
	 * specified element in the specified array,
	 * or return -1 if there is no such index.
	 * java.util.Arrays#indexOf(Object[] array, Object o)
	 */
	public static int indexOf(Object[] array, Object o) {
		int len = array.length;
		if (o == null) {
			for (int i = 0; i < len; i++) {
				if (array[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < len; i++) {
				if (o.equals(array[i])) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Return the index of the first occurrence of the
	 * specified element in the specified array,
	 * or return -1 if there is no such index.
	 * java.util.Arrays#indexOf(char[] array, char value)
	 */
	public static int indexOf(char[] array, char value) {
		int len = array.length;
		for (int i = 0; i < len; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Return the index of the first occurrence of the
	 * specified element in the specified array,
	 * or return -1 if there is no such index.
	 * java.util.Arrays#indexOf(int[] array, int value)
	 */
	public static int indexOf(int[] array, int value) {
		int len = array.length;
		for (int i = 0; i < len; i++) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Return the maximum index of where the specified comparable object
	 * should be inserted into the specified sorted list and still keep
	 * the list sorted.
	 */
	public static int insertionIndexOf(List sortedList, Comparable o) {
		int len = sortedList.size();
		for (int i = 0; i < len; i++) {
			if (o.compareTo(sortedList.get(i)) < 0) {
				return i;
			}
		}
		return len;
	}
	
	/**
	 * Return the maximum index of where the specified object
	 * should be inserted into the specified sorted list and still keep
	 * the list sorted.
	 */
	public static int insertionIndexOf(List sortedList, Object o, Comparator c) {
		int len = sortedList.size();
		for (int i = 0; i < len; i++) {
			if (c.compare(o, sortedList.get(i)) < 0) {
				return i;
			}
		}
		return len;
	}
	
	/**
	 * Return the maximum index of where the specified comparable object
	 * should be inserted into the specified sorted array and still keep
	 * the array sorted.
	 */
	public static int insertionIndexOf(Object[] sortedArray, Comparable o) {
		int len = sortedArray.length;
		for (int i = 0; i < len; i++) {
			if (o.compareTo(sortedArray[i]) < 0) {
				return i;
			}
		}
		return len;
	}
	
	/**
	 * Return the maximum index of where the specified comparable object
	 * should be inserted into the specified sorted array and still keep
	 * the array sorted.
	 */
	public static int insertionIndexOf(Object[] sortedArray, Object o, Comparator c) {
		int len = sortedArray.length;
		for (int i = 0; i < len; i++) {
			if (c.compare(o, sortedArray[i]) < 0) {
				return i;
			}
		}
		return len;
	}
	
	/**
	 * Return an iterator on the elements in the specified array.
	 * java.util.Arrays#iterator(Object[] array)
	 */
	public static Iterator iterator(Object[] array) {
		return new ArrayIterator(array);
	}
	
	/**
	 * Return the index of the last occurrence of the
	 * specified element in the specified iterator,
	 * or return -1 if there is no such index.
	 * java.util.ListIterator#lastIndexOf(Object o)
	 */
	public static int lastIndexOf(ListIterator iterator, Object o) {
		return list(iterator).lastIndexOf(o);
	}
	
	/**
	 * Return the index of the last occurrence of the
	 * specified element in the specified array,
	 * or return -1 if there is no such index.
	 * java.util.Arrays#lastIndexOf(Object[] array, Object o)
	 */
	public static int lastIndexOf(Object[] array, Object o) {
		int len = array.length;
		if (o == null) {
			for (int i = len; i-- > 0; ) {
				if (array[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = len; i-- > 0; ) {
				if (o.equals(array[i])) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Return the index of the last occurrence of the
	 * specified element in the specified array,
	 * or return -1 if there is no such index.
	 * java.util.Arrays#lastIndexOf(char[] array, char value)
	 */
	public static int lastIndexOf(char[] array, char value) {
		for (int i = array.length; i-- > 0; ) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Return the index of the last occurrence of the
	 * specified element in the specified array,
	 * or return -1 if there is no such index.
	 * java.util.Arrays#lastIndexOf(int[] array, int value)
	 */
	public static int lastIndexOf(int[] array, int value) {
		for (int i = array.length; i-- > 0; ) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Return a list corresponding to the specified iterator.
	 * java.util.Iterator#toList()
	 */
	public static List list(Iterator iterator) {
		List list = new ArrayList();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}
	
	/**
	 * Return a list iterator for the specified iterator.
	 */
	public static <T> ListIterator<T> listIterator(Iterator<T> iterator) {
		return list(iterator).listIterator();
	}
	
	/**
	 * Return a list corresponding to the specified array.
	 * Unlike java.util.Arrays.asList(Object[]), the list
	 * is modifiable and is not backed by the array.
	 */
	public static List list(Object[] array) {
		int len = array.length;
		List list = new ArrayList(len);
		for (int i = 0; i < len; i++) {
			list.add(array[i]);
		}
		return list;
	}
	
	/**
	 * Return a list iterator for the specified array.
	 * java.util.Arrays#listIterator(Object[] array)
	 */
	public static ListIterator listIterator(Object[] array) {
		return listIterator(array, 0);
	}
	
	/**
	 * Return a list iterator for the specified array
	 * starting at the specified position in the array.
	 * java.util.Arrays#listIterator(Object[] array, int index)
	 */
	public static ListIterator listIterator(Object[] array, int index) {
		return Arrays.asList(array).listIterator(index);
	}

	/**
	 * Return the character from the specified array with the maximum value.
	 * java.util.Arrays#max(char[] array)
	 */
	public static char max(char[] array) {
		int len = array.length;
		if (len == 0) {
			throw new IndexOutOfBoundsException();
		}
		char max = array[0];
		for (int i = 1; i < len; i++) {
			char next = array[i];
			if (next > max) {
				max = next;
			}
		}
		return max;
	}

	/**
	 * Return the integer from the specified array with the maximum value.
	 * java.util.Arrays#max(int[] array)
	 */
	public static int max(int[] array) {
		int len = array.length;
		if (len == 0) {
			throw new IndexOutOfBoundsException();
		}
		int max = array[0];
		for (int i = 1; i < len; i++) {
			int next = array[i];
			if (next > max) {
				max = next;
			}
		}
		return max;
	}

	/**
	 * Return the character from the specified array with the minimum value.
	 * java.util.Arrays#min(char[] array)
	 */
	public static char min(char[] array) {
		int len = array.length;
		if (len == 0) {
			throw new IndexOutOfBoundsException();
		}
		char min = array[0];
		for (int i = 1; i < len; i++) {
			char next = array[i];
			if (next < min) {
				min = next;
			}
		}
		return min;
	}

	/**
	 * Return the integer from the specified array with the minimum value.
	 * java.util.Arrays#min(int[] array)
	 */
	public static int min(int[] array) {
		int len = array.length;
		if (len == 0) {
			throw new IndexOutOfBoundsException();
		}
		int min = array[0];
		for (int i = 1; i < len; i++) {
			int next = array[i];
			if (next < min) {
				min = next;
			}
		}
		return min;
	}

	/**
	 * Replace all occurrences of the specified old value with
	 * the specified new value.
	 * java.util.Arrays#replaceAll(Object[] array, Object oldValue, Object newValue)
	 */
	public static Object[] replaceAll(Object[] array, Object oldValue, Object newValue) {
		if (oldValue == null) {
			for (int i = array.length; i-- > 0; ) {
				if (array[i] == null) {
					array[i] = newValue;
				}
			}
		} else {
			for (int i = array.length; i-- > 0; ) {
				if (oldValue.equals(array[i])) {
					array[i] = newValue;
				}
			}
		}
		return array;
	}

	/**
	 * Replace all occurrences of the specified old value with
	 * the specified new value.
	 * java.util.Arrays#replaceAll(int[] array, int oldValue, int newValue)
	 */
	public static int[] replaceAll(int[] array, int oldValue, int newValue) {
		for (int i = array.length; i-- > 0; ) {
			if (array[i] == oldValue) {
				array[i] = newValue;
			}
		}
		return array;
	}

	/**
	 * Replace all occurrences of the specified old value with
	 * the specified new value.
	 * java.util.Arrays#replaceAll(char[] array, char oldValue, char newValue)
	 */
	public static char[] replaceAll(char[] array, char oldValue, char newValue) {
		for (int i = array.length; i-- > 0; ) {
			if (array[i] == oldValue) {
				array[i] = newValue;
			}
		}
		return array;
	}

	/**
	 * Return a new array that contains the elements in the
	 * specified array with the specified element removed.
	 * java.util.Arrays#remove(Object[] array, Object value)
	 */
	public static Object[] remove(Object[] array, Object value) {
		int index = indexOf(array, value);
		int len = array.length;
		Object[] result = (Object[]) Array.newInstance(array.getClass().getComponentType(), len - 1);
		System.arraycopy(array, 0, result, 0, index);
		System.arraycopy(array, index + 1, result, index, len - index - 1);
		return result;
	}
	
	/**
	 * Return a new array that contains the elements in the
	 * specified array with the specified element removed.
	 * java.util.Arrays#remove(char[] array, char value)
	 */
	public static char[] remove(char[] array, char value) {
		int index = indexOf(array, value);
		int len = array.length;
		char[] result = new char[len - 1];
		System.arraycopy(array, 0, result, 0, index);
		System.arraycopy(array, index + 1, result, index, len - index - 1);
		return result;
	}
	
	/**
	 * Return a new array that contains the elements in the
	 * specified array with the specified element removed.
	 * java.util.Arrays#remove(int[] array, int value)
	 */
	public static int[] remove(int[] array, int value) {
		int index = indexOf(array, value);
		int len = array.length;
		int[] result = new int[len - 1];
		System.arraycopy(array, 0, result, 0, index);
		System.arraycopy(array, index + 1, result, index, len - index - 1);
		return result;
	}
	
	/**
	 * Remove all the elements returned by the specified iterator
	 * from the specified collection.
	 * Return whether the collection changed as a result.
	 * java.util.Collection#removeAll(java.util.Iterator iterator)
	 */
	public static boolean removeAll(Collection collection, Iterator iterator) {
		boolean modified = false;
		while (iterator.hasNext()) {
			modified |= collection.remove(iterator.next());
		}
		return modified;
	}
	
	/**
	 * Remove all the elements in the specified array
	 * from the specified collection.
	 * Return whether the collection changed as a result.
	 * java.util.Collection#removeAll(Object[] array)
	 */
	public static boolean removeAll(Collection collection, Object[] array) {
		boolean modified = false;
		for (int i = array.length; i-- > 0; ) {
			modified |= collection.remove(array[i]);
		}
		return modified;
	}
	
	/**
	 * Remove from the specified array all the elements in
	 * the specified collection and return the result.
	 * java.util.Arrays#removeAll(Object[] array, Collection collection)
	 */
	public static Object[] removeAll(Object[] array, Collection collection) {
		Object[] result = array;
		for (int i = array.length; i-- > 0; ) {
			Object item = array[i];
			if (collection.contains(item)) {
				result = remove(result, item);
			}
		}
		return result;
	}
	
	/**
	 * Remove from the first specified array all the elements in
	 * the second specified array and return the result.
	 * java.util.Arrays#removeAll(Object[] array1, Object[] array2)
	 */
	public static Object[] removeAll(Object[] array1, Object[] array2) {
		// convert to a bag to take advantage of hashed look-up
		return removeAll(array1, bag(array2));
	}
	
	/**
	 * Remove from the first specified array all the elements in
	 * the second specified array and return the result.
	 * java.util.Arrays#removeAll(char[] array1, char[] array2)
	 */
	public static char[] removeAll(char[] array1, char[] array2) {
		char[] result = array1;
		for (int i = array1.length; i-- > 0; ) {
			char item = array1[i];
			if (contains(array2, item)) {
				result = remove(result, item);
			}
		}
		return result;
	}
	
	/**
	 * Remove from the first specified array all the elements in
	 * the second specified array and return the result.
	 * java.util.Arrays#removeAll(int[] array1, int[] array2)
	 */
	public static int[] removeAll(int[] array1, int[] array2) {
		int[] result = array1;
		for (int i = array1.length; i-- > 0; ) {
			int item = array1[i];
			if (contains(array2, item)) {
				result = remove(result, item);
			}
		}
		return result;
	}
	
	/**
	 * Remove all occurrences of the specified element
	 * from the specified collection.
	 * Return whether the collection changed as a result.
	 * java.util.Collection#removeAllOccurrences(Object value)
	 */
	public static boolean removeAllOccurrences(Collection collection, Object value) {
		boolean modified = false;
		Iterator stream = collection.iterator();
		if (value == null) {
			while (stream.hasNext()) {
				if (stream.next() == null) {
					stream.remove();
					modified = true;
				}
			}
		} else {
			while (stream.hasNext()) {
				if (value.equals(stream.next())) {
					stream.remove();
					modified = true;
				}
			}
		}
		return modified;
	}
	
	/**
	 * Remove from the specified array all occurrences of
	 * the specified element and return the result.
	 * java.util.Arrays#removeAllOccurrences(Object[] array, Object value)
	 */
	public static Object[] removeAllOccurrences(Object[] array, Object value) {
		Object[] result = array;
		if (value == null) {
			for (int i = array.length; i-- > 0; ) {
				if (array[i] == null) {
					result = remove(result, null);
				}
			}
		} else {
			for (int i = array.length; i-- > 0; ) {
				if (value.equals(array[i])) {
					result = remove(result, value);
				}
			}
		}
		return result;
	}
	
	/**
	 * Remove from the specified array all occurrences of
	 * the specified element and return the result.
	 * java.util.Arrays#removeAllOccurrences(char[] array, char value)
	 */
	public static char[] removeAllOccurrences(char[] array, char value) {
		char[] result = array;
		for (int i = array.length; i-- > 0; ) {
			if (array[i] == value) {
				result = remove(result, value);
			}
		}
		return result;
	}
	
	/**
	 * Remove from the specified array all occurrences of
	 * the specified element and return the result.
	 * java.util.Arrays#removeAllOccurrences(int[] array, int value)
	 */
	public static int[] removeAllOccurrences(int[] array, int value) {
		int[] result = array;
		for (int i = array.length; i-- > 0; ) {
			if (array[i] == value) {
				result = remove(result, value);
			}
		}
		return result;
	}
	
	/**
	 * Remove any duplicate elements from the specified array,
	 * while maintaing the order.
	 */
	public static Object[] removeDuplicateElements(Object[] array) {
		List list = removeDuplicateElements(Arrays.asList(array));
		return list.toArray((Object[]) Array.newInstance(array.getClass().getComponentType(), list.size()));
	}

	/**
	 * Remove any duplicate elements from the specified list,
	 * while maintaing the order.
	 */
	public static List removeDuplicateElements(List list) {
		List result = new ArrayList(list.size());
		Set set = new HashSet(list.size());		// take advantage of hashed look-up
		for (Iterator stream = list.iterator(); stream.hasNext(); ) {
			Object o = stream.next();
			if (set.add(o)) {
				result.add(o);
			}
		}
		return result;
	}

	/**
	 * Retain only the elements in the specified iterator
	 * in the specified collection.
	 * Return whether the collection changed as a result.
	 * java.util.Collection#retainAll(java.util.Iterator iterator)
	 */
	public static boolean retainAll(Collection collection, Iterator iterator) {
		return collection.retainAll(collection(iterator));
	}
	
	/**
	 * Retain only the elements in the specified array
	 * in the specified collection.
	 * Return whether the collection changed as a result.
	 * java.util.Collection#retainAll(Object[] array)
	 */
	public static boolean retainAll(Collection collection, Object[] array) {
		boolean modified = false;
		for (Iterator stream = collection.iterator(); stream.hasNext(); ) {
			if ( ! contains(array, stream.next())) {
				stream.remove();
				modified = true;
			}
		}
		return modified;
	}
	
	/**
	 * Retain in the specified array all the elements in
	 * the specified collection and return the result.
	 * java.util.Arrays#retainAll(Object[] array, Collection collection)
	 */
	public static Object[] retainAll(Object[] array, Collection collection) {
		Object[] result = array;
		for (int i = array.length; i-- > 0; ) {
			Object item = array[i];
			if ( ! collection.contains(item)) {
				result = remove(result, item);
			}
		}
		return result;
	}
	
	/**
	 * Remove from the first specified array all the elements in
	 * the second specified array and return the result.
	 * java.util.Arrays#retainAll(Object[] array1, Object[] array2)
	 */
	public static Object[] retainAll(Object[] array1, Object[] array2) {
		// convert to a bag to take advantage of hashed look-up
		return retainAll(array1, bag(array2));
	}
	
	/**
	 * Remove from the first specified array all the elements in
	 * the second specified array and return the result.
	 * java.util.Arrays#retainAll(char[] array1, char[] array2)
	 */
	public static char[] retainAll(char[] array1, char[] array2) {
		char[] result = array1;
		for (int i = array1.length; i-- > 0; ) {
			char item = array1[i];
			if ( ! contains(array2, item)) {
				result = remove(result, item);
			}
		}
		return result;
	}
	
	/**
	 * Remove from the first specified array all the elements in
	 * the second specified array and return the result.
	 * java.util.Arrays#removeAll(int[] array1, int[] array2)
	 */
	public static int[] retainAll(int[] array1, int[] array2) {
		int[] result = array1;
		for (int i = array1.length; i-- > 0; ) {
			int item = array1[i];
			if ( ! contains(array2, item)) {
				result = remove(result, item);
			}
		}
		return result;
	}
	
	/**
	 * Return the array reversed.
	 * java.util.Arrays.reverse(Object[] array)
	 */
	public static Object[] reverse(Object[] array) {
		int len = array.length;
		for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
			swap(array, i, j);
		}
		return array;
	}

	/**
	 * Return the array reversed.
	 * java.util.Arrays.reverse(char[] array)
	 */
	public static char[] reverse(char[] array) {
		int len = array.length;
		for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
			swap(array, i, j);
		}
		return array;
	}

	/**
	 * Return the array reversed.
	 * java.util.Arrays.reverse(int[] array)
	 */
	public static int[] reverse(int[] array) {
		int len = array.length;
		for (int i = 0, mid = len >> 1, j = len - 1; i < mid; i++, j--) {
			swap(array, i, j);
		}
		return array;
	}

	/**
	 * Return a list with entries in reverse order from those
	 * returned by the specified iterator.
	 */
	public static List reverseList(Iterator iterator) {
		return reverse(list(iterator));
	}

	/**
	 * Return the rotated array after rotating it one position.
	 * java.util.Arrays.rotate(Object[] array)
	 */
	public static Object[] rotate(Object[] array) {
		return rotate(array, 1);
	}

	/**
	 * Return the rotated array after rotating it the specified distance.
	 * java.util.Arrays.rotate(Object[] array, int distance)
	 */
	public static Object[] rotate(Object[] array, int distance) {
		int len = array.length;
		if (len == 0) {
			return array;
		}
		distance = distance % len;
		if (distance < 0) {
			distance += len;
		}
		if (distance == 0) {
			return array;
		}
		for (int cycleStart = 0, nMoved = 0; nMoved != len; cycleStart++) {
			Object displaced = array[cycleStart];
			int i = cycleStart;
			do {
				i += distance;
				if (i >= len) {
					i -= len;
				}
				Object temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved ++;
			} while (i != cycleStart);
		}
		return array;
	}

	/**
	 * Return the rotated array after rotating it one position.
	 * java.util.Arrays.rotate(char[] array)
	 */
	public static char[] rotate(char[] array) {
		return rotate(array, 1);
	}

	/**
	 * Return the rotated array after rotating it the specified distance.
	 * java.util.Arrays.rotate(char[] array, int distance)
	 */
	public static char[] rotate(char[] array, int distance) {
		int len = array.length;
		if (len == 0) {
			return array;
		}
		distance = distance % len;
		if (distance < 0) {
			distance += len;
		}
		if (distance == 0) {
			return array;
		}
		for (int cycleStart = 0, nMoved = 0; nMoved != len; cycleStart++) {
			char displaced = array[cycleStart];
			int i = cycleStart;
			do {
				i += distance;
				if (i >= len) {
					i -= len;
				}
				char temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved ++;
			} while (i != cycleStart);
		}
		return array;
	}

	/**
	 * Return the rotated array after rotating it one position.
	 * java.util.Arrays.rotate(int[] array)
	 */
	public static int[] rotate(int[] array) {
		return rotate(array, 1);
	}

	/**
	 * Return the rotated array after rotating it the specified distance.
	 * java.util.Arrays.rotate(int[] array, int distance)
	 */
	public static int[] rotate(int[] array, int distance) {
		int len = array.length;
		if (len == 0) {
			return array;
		}
		distance = distance % len;
		if (distance < 0) {
			distance += len;
		}
		if (distance == 0) {
			return array;
		}
		for (int cycleStart = 0, nMoved = 0; nMoved != len; cycleStart++) {
			int displaced = array[cycleStart];
			int i = cycleStart;
			do {
				i += distance;
				if (i >= len) {
					i -= len;
				}
				int temp = array[i];
				array[i] = displaced;
				displaced = temp;
				nMoved ++;
			} while (i != cycleStart);
		}
		return array;
	}

	/**
	 * Return a set corresponding to the specified iterator.
	 * java.util.HashSet(java.util.Iterator iterator)
	 */
	public static Set set(Iterator iterator) {
		Set set = new HashSet();
		while (iterator.hasNext()) {
			set.add(iterator.next());
		}
		return set;
	}

	/**
	 * Return a set corresponding to the specified array.
	 * java.util.HashSet(Object[] array)
	 */
	public static Set set(Object[] array) {
		Set set = new HashSet(array.length);
		for (int i = array.length; i-- > 0; ) {
			set.add(array[i]);
		}
		return set;
	}

	private static final Random RANDOM = new Random();

	/**
	 * Return the array after "shuffling" it.
	 * java.util.Arrays#shuffle(Object[] array)
	 */
	public static Object[] shuffle(Object[] array) {
		return shuffle(array, RANDOM);
	}

	/**
	 * Return the array after "shuffling" it.
	 * java.util.Arrays#shuffle(Object[] array, Random r)
	 */
	public static Object[] shuffle(Object[] array, Random r) {
		for (int i = array.length; i-- > 0; ) {
			swap(array, i, r.nextInt(i));
		}
		return array;
	}

	/**
	 * Return the array after "shuffling" it.
	 * java.util.Arrays#shuffle(char[] array)
	 */
	public static char[] shuffle(char[] array) {
		return shuffle(array, RANDOM);
	}

	/**
	 * Return the array after "shuffling" it.
	 * java.util.Arrays#shuffle(char[] array, Random r)
	 */
	public static char[] shuffle(char[] array, Random r) {
		for (int i = array.length; i-- > 0; ) {
			swap(array, i, r.nextInt(i));
		}
		return array;
	}

	/**
	 * Return the array after "shuffling" it.
	 * java.util.Arrays#shuffle(int[] array)
	 */
	public static int[] shuffle(int[] array) {
		return shuffle(array, RANDOM);
	}

	/**
	 * Return the array after "shuffling" it.
	 * java.util.Arrays#shuffle(int[] array, Random r)
	 */
	public static int[] shuffle(int[] array, Random r) {
		for (int i = array.length; i-- > 0; ) {
			swap(array, i, r.nextInt(i));
		}
		return array;
	}

	/**
	 * Return an iterator that returns only the single,
	 * specified object.
	 * Object#toIterator() ?!
	 */
	public static Iterator singletonIterator(Object o) {
		return new SingleElementIterator(o);
	}
	
	/**
	 * Return the number of elements returned by the specified iterator.
	 * java.util.Iterator#size()
	 */
	public static int size(Iterator iterator) {
		int size = 0;
		while (iterator.hasNext()) {
			iterator.next();
			size++;
		}
		return size;
	}
	
	/**
	 * Return a sorted set corresponding to the specified iterator.
	 * java.util.TreeSet(java.util.Iterator iterator)
	 */
	public static SortedSet sortedSet(Iterator iterator) {
		return sortedSet(iterator, null);
	}
	
	/**
	 * Return a sorted set corresponding to the specified iterator
	 * and comparator.
	 * java.util.TreeSet(java.util.Iterator iterator, java.util.Comparator c)
	 */
	public static SortedSet sortedSet(Iterator iterator, Comparator c) {
		SortedSet sortedSet = new TreeSet(c);
		sortedSet.addAll(list(iterator));
		return sortedSet;
	}
	
	/**
	 * Return a sorted set corresponding to the specified array.
	 * java.util.TreeSet(Object[] array)
	 */
	public static SortedSet sortedSet(Object[] array) {
		return sortedSet(array, null);
	}
	
	/**
	 * Return a sorted set corresponding to the specified array
	 * and comparator.
	 * java.util.TreeSet(Object[] array, java.util.Comparator c)
	 */
	public static SortedSet sortedSet(Object[] array, Comparator c) {
		SortedSet sortedSet = new TreeSet(c);
		sortedSet.addAll(Arrays.asList(array));
		return sortedSet;
	}
	
	/**
	 * Return the array after the specified elements have been "swapped".
	 * java.util.Arrays#swap(Object[] array, int i, int j)
	 */
	public static Object[] swap(Object[] array, int i, int j) {
		Object temp = array[i];
		array[i] = array[j];
		array[j] = temp;
		return array;
	}

	/**
	 * Return the array after the specified elements have been "swapped".
	 * java.util.Arrays#swap(char[] array, int i, int j)
	 */
	public static char[] swap(char[] array, int i, int j) {
		char temp = array[i];
		array[i] = array[j];
		array[j] = temp;
		return array;
	}

	/**
	 * Return the array after the specified elements have been "swapped".
	 * java.util.Arrays#swap(int[] array, int i, int j)
	 */
	public static int[] swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
		return array;
	}

	/**
	 * Return a vector corresponding to the specified iterator.
	 * This is useful for legacy code that requires a java.util.Vector.
	 * java.util.Vector(java.util.Iterator iterator)
	 */
	public static Vector vector(Iterator iterator) {
		Vector v = new Vector();
		while (iterator.hasNext()) {
			v.addElement(iterator.next());
		}
		return v;
	}
	
	/**
	 * Return a vector corresponding to the specified array.
	 * This is useful for legacy code that requires a java.util.Vector.
	 * java.util.Vector(Object[] array)
	 */
	public static Vector vector(Object[] array) {
		int len = array.length;
		Vector v = new Vector(len);
		for (int i = 0; i < len; i++) {
			v.addElement(array[i]);
		}
		return v;
	}


	//********** java.util.Collections enhancements **********

	/**
	 * Return the destination list after the source list has been copied into it.
	 * @see java.util.Collections#copy(java.util.List, java.util.List)
	 */
	public static List copy(List dest, List src) {
		Collections.copy(dest, src);
		return dest;
	}

	/**
	 * Return the list after it has been "filled".
	 * @see java.util.Collections#fill(java.util.List, java.lang.Object)
	 */
	public static List fill(List list, Object o) {
		Collections.fill(list, o);
		return list;
	}

	/**
	 * Return the list after it has been "reversed".
	 * @see java.util.Collections#reverse(java.util.List)
	 */
	public static List reverse(List list) {
		Collections.reverse(list);
		return list;
	}

	/**
	 * Return the list after it has been "rotated" by one position.
	 * @see java.util.Collections#rotate(java.util.List, int)
	 */
	public static List rotate(List list) {
		return rotate(list, 1);
	}

	/**
	 * Return the list after it has been "rotated".
	 * @see java.util.Collections#rotate(java.util.List, int)
	 */
	public static List rotate(List list, int distance) {
		Collections.rotate(list, distance);
		return list;
	}

	/**
	 * Return the list after it has been "shuffled".
	 * @see java.util.Collections#shuffle(java.util.List)
	 */
	public static List shuffle(List list) {
		Collections.shuffle(list);
		return list;
	}

	/**
	 * Return the list after it has been "shuffled".
	 * @see java.util.Collections#shuffle(java.util.List, java.util.Random)
	 */
	public static List shuffle(List list, Random random) {
		Collections.shuffle(list, random);
		return list;
	}

	/**
	 * Return the list after it has been "sorted".
	 * @see java.util.Collections#sort(java.util.List)
	 */
	public static List sort(List list) {
		Collections.sort(list);
		return list;
	}

	/**
	 * Return the list after it has been "sorted".
	 * @see java.util.Collections#sort(java.util.List, java.util.Comparator)
	 */
	public static List sort(List list, Comparator c) {
		Collections.sort(list, c);
		return list;
	}

	/**
	 * Return the list after it has been "sorted".
	 * @see java.util.Collections#sort(java.util.List)
	 */
	public static List sort(Iterator iterator) {
		List list = list(iterator);
		Collections.sort(list);
		return list;
	}

	/**
	 * Return the list after it has been "sorted".
	 * @see java.util.Collections#sort(java.util.List, java.util.Comparator)
	 */
	public static List sort(Iterator iterator, Comparator c) {
		List list = list(iterator);
		Collections.sort(list, c);
		return list;
	}

	/**
	 * Return the list after the specified elements have been "swapped".
	 * @see java.util.Collections#swap(java.util.List, int, int)
	 */
	public static List swap(List list, int i, int j) {
		Collections.swap(list, i, j);
		return list;
	}


	//********** java.util.Arrays enhancements **********

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(boolean[], boolean)
	 */
	public static boolean[] fill(boolean[] a, boolean val) {
		Arrays.fill(a, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(boolean[], int, int, boolean)
	 */
	public static boolean[] fill(boolean[] a, int fromIndex, int toIndex, boolean val) {
		Arrays.fill(a, fromIndex, toIndex, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(byte[], byte)
	 */
	public static byte[] fill(byte[] a, byte val) {
		Arrays.fill(a, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(byte[], int, int, byte)
	 */
	public static byte[] fill(byte[] a, int fromIndex, int toIndex, byte val) {
		Arrays.fill(a, fromIndex, toIndex, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(char[], char)
	 */
	public static char[] fill(char[] a, char val) {
		Arrays.fill(a, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(char[], int, int, char)
	 */
	public static char[] fill(char[] a, int fromIndex, int toIndex, char val) {
		Arrays.fill(a, fromIndex, toIndex, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(double[], double)
	 */
	public static double[] fill(double[] a, double val) {
		Arrays.fill(a, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(double[], int, int, double)
	 */
	public static double[] fill(double[] a, int fromIndex, int toIndex, double val) {
		Arrays.fill(a, fromIndex, toIndex, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(float[], float)
	 */
	public static float[] fill(float[] a, float val) {
		Arrays.fill(a, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(float[], int, int, float)
	 */
	public static float[] fill(float[] a, int fromIndex, int toIndex, float val) {
		Arrays.fill(a, fromIndex, toIndex, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(int[], int)
	 */
	public static int[] fill(int[] a, int val) {
		Arrays.fill(a, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(int[], int, int, int)
	 */
	public static int[] fill(int[] a, int fromIndex, int toIndex, int val) {
		Arrays.fill(a, fromIndex, toIndex, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(Object[], Object)
	 */
	public static Object[] fill(Object[] a, Object val) {
		Arrays.fill(a, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(Object[], int, int, Object)
	 */
	public static Object[] fill(Object[] a, int fromIndex, int toIndex, Object val) {
		Arrays.fill(a, fromIndex, toIndex, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(long[], long)
	 */
	public static long[] fill(long[] a, long val) {
		Arrays.fill(a, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(long[], int, int, long)
	 */
	public static long[] fill(long[] a, int fromIndex, int toIndex, long val) {
		Arrays.fill(a, fromIndex, toIndex, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(short[], short)
	 */
	public static short[] fill(short[] a, short val) {
		Arrays.fill(a, val);
		return a;
	}

	/**
	 * Return the array after it has been "filled".
	 * @see java.util.Arrays#fill(short[], int, int, short)
	 */
	public static short[] fill(short[] a, int fromIndex, int toIndex, short val) {
		Arrays.fill(a, fromIndex, toIndex, val);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(byte[])
	 */
	public static byte[] sort(byte[] a) {
		Arrays.sort(a);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(byte[], int, int)
	 */
	public static byte[] sort(byte[] a, int fromIndex, int toIndex) {
		Arrays.sort(a, fromIndex, toIndex);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(char[])
	 */
	public static char[] sort(char[] a) {
		Arrays.sort(a);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(char[], int, int)
	 */
	public static char[] sort(char[] a, int fromIndex, int toIndex) {
		Arrays.sort(a, fromIndex, toIndex);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(double[])
	 */
	public static double[] sort(double[] a) {
		Arrays.sort(a);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(double[], int, int)
	 */
	public static double[] sort(double[] a, int fromIndex, int toIndex) {
		Arrays.sort(a, fromIndex, toIndex);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(float[])
	 */
	public static float[] sort(float[] a) {
		Arrays.sort(a);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(float[], int, int)
	 */
	public static float[] sort(float[] a, int fromIndex, int toIndex) {
		Arrays.sort(a, fromIndex, toIndex);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(int[])
	 */
	public static int[] sort(int[] a) {
		Arrays.sort(a);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(int[], int, int)
	 */
	public static int[] sort(int[] a, int fromIndex, int toIndex) {
		Arrays.sort(a, fromIndex, toIndex);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(Object[])
	 */
	public static Object[] sort(Object[] a) {
		Arrays.sort(a);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(Object[], java.util.Comparator)
	 */
	public static Object[] sort(Object[] a, Comparator c) {
		Arrays.sort(a, c);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(Object[], int, int)
	 */
	public static Object[] sort(Object[] a, int fromIndex, int toIndex) {
		Arrays.sort(a, fromIndex, toIndex);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(Object[], int, int, java.util.Comparator)
	 */
	public static Object[] sort(Object[] a, int fromIndex, int toIndex, Comparator c) {
		Arrays.sort(a, fromIndex, toIndex, c);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(long[])
	 */
	public static long[] sort(long[] a) {
		Arrays.sort(a);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(long[], int, int)
	 */
	public static long[] sort(long[] a, int fromIndex, int toIndex) {
		Arrays.sort(a, fromIndex, toIndex);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(short[])
	 */
	public static short[] sort(short[] a) {
		Arrays.sort(a);
		return a;
	}

	/**
	 * Return the array after it has been "sorted".
	 * @see java.util.Arrays#sort(short[], int, int)
	 */
	public static short[] sort(short[] a, int fromIndex, int toIndex) {
		Arrays.sort(a, fromIndex, toIndex);
		return a;
	}


	//********** constructor **********

	/**
	 * Suppress default constructor, ensuring non-instantiability.
	 */
	private CollectionTools() {
		super();
		throw new UnsupportedOperationException();
	}
	
}
