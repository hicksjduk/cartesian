/*
Copyright 2020 Jeremy Hicks

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package uk.org.thehickses.cartesian;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * A combination of objects, which may be of different types.
 */
public class Combination
{
    private final Iterator<Object> iterator;

    public Combination(Object[] objects)
    {
        iterator = Stream.of(objects).iterator();
    }

    /**
     * Queries for the availability of a next element.
     * 
     * @return whether the combination has a next element.
     */
    public boolean hasNext()
    {
        return iterator.hasNext();
    }

    /**
     * Gets the next element of the combination, casting it to the specified type.
     * 
     * @param <T>
     *            the type.
     * @param type
     *            the class object for the type.
     * @return the next element.
     * @throws NoSuchElementException
     *             if there is no next element.
     * @throws ClassCastException
     *             if the next element cannot be cast to the specified type.
     */
    public <T> T next(Class<T> type)
    {
        Object next;
        synchronized (iterator)
        {
            next = iterator.next();
        }
        return type.cast(next);
    }

    /**
     * Gets the next element of the combination, casting it to the {@code int} primitive type.
     * 
     * @return the next element.
     * @throws NoSuchElementException
     *             if there is no next element.
     * @throws ClassCastException
     *             if the next element cannot be cast to {@code int}.
     */
    public int nextInt()
    {
        return next(Integer.class);
    }

    /**
     * Gets the next element of the combination, casting it to the {@code long} primitive type.
     * 
     * @return the next element.
     * @throws NoSuchElementException
     *             if there is no next element.
     * @throws ClassCastException
     *             if the next element cannot be cast to {@code long}.
     */
    public long nextLong()
    {
        return next(Long.class);
    }

    /**
     * Gets the next element of the combination, casting it to the {@code double} primitive type.
     * 
     * @return the next element.
     * @throws NoSuchElementException
     *             if there is no next element.
     * @throws ClassCastException
     *             if the next element cannot be cast to {@code double}.
     */
    public double nextDouble()
    {
        return next(Double.class);
    }

    /**
     * Gets the next element of the combination, casting it to the {@code boolean} primitive type.
     * 
     * @return the next element.
     * @throws NoSuchElementException
     *             if there is no next element.
     * @throws ClassCastException
     *             if the next element cannot be cast to {@code boolean}.
     */
    public boolean nextBoolean()
    {
        return next(Boolean.class);
    }

    /**
     * Gets all the remaining objects in the collection, casting each to the specified type.
     * 
     * @param <T>
     *            the type.
     * @param type
     *            the class object for the type.
     * @return a stream of the remaining objects.
     * @throws ClassCastException
     *             if any element cannot be cast to the specified type.
     */
    public <T> Stream<T> allRemaining(Class<T> type)
    {
        List<T> answer = new LinkedList<>();
        while (hasNext())
            answer.add(next(type));
        return answer.stream();
    }
}