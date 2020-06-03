package uk.org.thehickses.cartesian;

import java.util.Iterator;
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
}