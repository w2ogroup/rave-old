/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.rave.util;

import org.apache.commons.lang.Validate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CollectionUtils {
    /**
     * Helper for the reconcileObjectCollections method.
     */
    public interface CollectionReconciliationHelper<T, K> {
        /**
         * Extracts the key that should be used to identify this object for the purposes of reconciliation.
         *
         * @param object The object to extract the key from.
         * @return The key to use.
         */
        K extractKey(T object);

        /**
         * Compares the values of the two objects and reconciles the values from the updatedObject into the
         * existingObject if they are not already the same.
         *
         * @param existingObject The existing object.
         * @param updatedObject  The updated object.
         */
        void reconcileValues(T existingObject, T updatedObject);
    }

    /**
     * Utility to reconciles the values of two collections.  Reconciliation algorithm is implemented as follows:
     * <p/>
     * -- Any object occurring in the existingCollection which does not also occur in the updatedCollection is removed
     * from existingCollection (if removeMissingObjects flag is true).
     * <p/>
     * -- Any object occurring in both collections has the values copied from the instance in the updatedCollection to
     * the instance in the existingCollection.
     * <p/>
     * -- Any object which occurs in the updatedCollection only is copied into the existingCollection.
     *
     * @param existingCollection   The master collection of existing values.
     * @param updatedCollection    The updated collection of values.
     * @param reconciliationHelper The CollectionReconciliationHelper to extract object keys and reconcile values.
     * @param removeMissingObjects True to remove objects from the existingCollection which do not occur in the
     *                             updatedCollection, false otherwise.
     */
    public static <T, K> void reconcileObjectCollections(Collection<T> existingCollection, Collection<T> updatedCollection,
                                                         CollectionReconciliationHelper<T, K> reconciliationHelper,
                                                         boolean removeMissingObjects) {
        //Make sure our collections are not null and do not contain any null elements.
        Validate.noNullElements(existingCollection);
        Validate.noNullElements(updatedCollection);

        //Create maps to map out our existing and updates objects.
        HashMap<K, T> existingObjects = new HashMap<K, T>(existingCollection.size());
        HashMap<K, T> updatedObjects = new HashMap<K, T>(updatedCollection.size());

        //Map out the updated objects.
        for (T updatedObject : updatedCollection) {
            updatedObjects.put(reconciliationHelper.extractKey(updatedObject), updatedObject);
        }

        //Now enumerate and map out the existing objects, removing or updating existing objects along the way as needed.
        for (Iterator<T> iterator = existingCollection.iterator(); iterator.hasNext();) {
            T existingObject = iterator.next();
            T updatedObject = updatedObjects.get(reconciliationHelper.extractKey(existingObject));
            if (updatedObject == null) {
                //This object is no longer present - remove it if required.
                if (removeMissingObjects) {
                    iterator.remove();
                }
            } else {
                //The object is still there -- reconcile the value and add it to our existingObjects map.
                reconciliationHelper.reconcileValues(existingObject, updatedObject);
                existingObjects.put(reconciliationHelper.extractKey(existingObject), existingObject);
            }
        }

        //Now add any objects that are new to the existing objects list.
        for (Map.Entry<K, T> updatedObjectEntry : updatedObjects.entrySet()) {
            if (!existingObjects.containsKey(updatedObjectEntry.getKey())) {
                existingCollection.add(updatedObjectEntry.getValue());
            }
        }
    }
}