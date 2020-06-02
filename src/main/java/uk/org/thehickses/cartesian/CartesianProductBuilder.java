package uk.org.thehickses.cartesian;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A builder that creates the Cartesian product of any number of collections of objects. The objects can be specified
 * individually, or in streams or collections.
 * 
 * <p>
 * The Cartesian product of a set of collections consists of all possible combinations of an element from the first
 * collection, followed by an element from the second collection, followed by an element from each subsequent
 * collection. For example, the Cartesian product of the collections (A, B), (C, D) and (E, F) consists of (A, C, E),
 * (A, C, F), (A, D, E), (A, D, F), (B, C, E), (B, C, F), (B, D, E) and (B, D, F).
 * 
 * @author Jeremy Hicks
 *
 */
public class CartesianProductBuilder
{
    /**
     * Gets a builder which (unless more collections are added to it) builds a Cartesian product containing one
     * combination for each element in the specified stream.
     * 
     * @param objects
     *            the objects that are to appear in the output.
     * @return the builder.
     */
    public static CartesianProductBuilder of(Stream<?> objects)
    {
        return new CartesianProductBuilder(null, objects);
    }

    /**
     * Gets a builder which (unless more collections are added to it) builds a Cartesian product containing one
     * combination for each element in the specified collection.
     * 
     * @param objects
     *            the objects that are to appear in the output.
     * @return the builder.
     */
    public static CartesianProductBuilder of(Collection<?> objects)
    {
        return new CartesianProductBuilder(null, objects.stream());
    }

    /**
     * Gets a builder which (unless more collections are added to it) builds a Cartesian product containing one
     * combination for each of the specified elements.
     * 
     * @param objects
     *            the objects that are to appear in the output.
     * @return the builder.
     */
    public static CartesianProductBuilder of(Object... objects)
    {
        return new CartesianProductBuilder(null, Stream.of(objects));
    }

    /**
     * The sets of objects from which a Cartesian product is to be constructed. Each element of the outer array is an
     * array whose members are to be permuted with the others.
     */
    private final Object[][] objects;

    /**
     * Creates a builder from the specified base builder, and some objects that are to be permuted with the Cartesian
     * product of the base builder.
     * 
     * @param base
     *            the base builder. May be null, in which case the specified objects form the first dimension of the
     *            data array.
     * @param objects
     *            the objects to be permuted.
     */
    private CartesianProductBuilder(CartesianProductBuilder base, Stream<?> objects)
    {
        int length = (base == null ? 0 : base.objects.length) + 1;
        this.objects = new Object[length][];
        if (base != null)
            System.arraycopy(base.objects, 0, this.objects, 0, base.objects.length);
        this.objects[length - 1] = objects.toArray();
    }

    /**
     * Gets a builder which will permute the objects in the specified stream with the Cartesian product constructed by
     * this builder.
     * 
     * @param objects
     *            the objects.
     * @return the new builder.
     */
    public CartesianProductBuilder and(Stream<?> objects)
    {
        return new CartesianProductBuilder(this, objects);
    }

    /**
     * Gets a builder which will permute the objects in the specified collection with the Cartesian product constructed
     * by this builder.
     * 
     * @param objects
     *            the objects.
     * @return the new builder.
     */
    public CartesianProductBuilder and(Collection<?> objects)
    {
        return new CartesianProductBuilder(this, objects.stream());
    }

    /**
     * Gets a builder which will permute the specified objects with the Cartesian product constructed by this builder.
     * 
     * @param objects
     *            the objects.
     * @return the new builder.
     */
    public CartesianProductBuilder and(Object... objects)
    {
        return new CartesianProductBuilder(this, Stream.of(objects));
    }

    /**
     * Builds the Cartesian product. The result is a stream, each element of which is a combination of objects.
     * 
     * @return the Cartesian product.
     */
    public Stream<Combination> build()
    {
        return StreamSupport.stream(new CartesianSpliterator(objects), false);
    }

    /**
     * A combination of objects, which may be of different types.
     */
    public static class Combination
    {
        private final Iterator<Object> iterator;

        private Combination(Object[] objects)
        {
            iterator = Stream.of(objects).iterator();
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

    /**
     * A Spliterator that is used to construct the combinations that make up the Cartesian product.
     */
    private static class CartesianSpliterator extends AbstractSpliterator<Combination>
    {
        /**
         * Gets an estimate of the number of elements in the Cartesian product of the specified data lists. This is
         * exact (the product of the lists' sizes) unless the actual size is too big to be represented as a
         * {@code long}, in which case it is {@link Long#MAX_VALUE}.
         * 
         * @param data
         *            the lists.
         * @return the estimate.
         */
        private static long estimatedSize(Object[][] data)
        {
            return Stream
                    .of(data)
                    .mapToLong(d -> d.length)
                    .reduce(1, multiplySubjectToMaximum(Long.MAX_VALUE));
        }

        /**
         * Gets an operator that returns the product of its operands, unless that product is greater than the specified
         * maximum value in which case the maximum is returned. Note that this is safe from overflow errors, as the test
         * to see whether the product exceeds the maximum is done by dividing the maximum by the first operand and
         * checking whether the quotient is less than the second operand.
         * 
         * @param maxValue
         *            the maximum value.
         * @return the operator.
         */
        private static LongBinaryOperator multiplySubjectToMaximum(long maxValue)
        {
            return (a, b) -> a == 0 ? 0 : maxValue / a < b ? maxValue : a * b;
        }

        private DataReader[] readers;

        private CartesianSpliterator(Object[][] data)
        {
            super(estimatedSize(data), IMMUTABLE | NONNULL | ORDERED);
            if (Stream.of(data).anyMatch(d -> d.length == 0))
                readers = null;
            else
                readers = Stream.of(data).map(DataReader::new).toArray(DataReader[]::new);
        }

        @Override
        public boolean tryAdvance(Consumer<? super Combination> action)
        {
            if (readers == null)
                return false;
            action.accept(currentCombination());
            return advance();
        }

        /**
         * Gets the current combination indicated by the readers.
         * 
         * @return the combination.
         */
        private Combination currentCombination()
        {
            return new Combination(Stream.of(readers).map(DataReader::value).toArray());
        }

        /**
         * Advances the readers to the next available combination, if there is one.
         * 
         * @return whether another combination is available.
         */
        private boolean advance()
        {
            for (int i = readers.length - 1; i >= 0; i--)
            {
                if (readers[i].advance().hasValue())
                    return true;
                readers[i].reset();
            }
            readers = null;
            return false;
        }
    }

    /**
     * A utility class that encapsulates the actions associated with reading an array sequentially, and perhaps
     * repeatedly.
     */
    private static class DataReader
    {
        private final Object[] objects;
        private int currentIndex;

        private DataReader(Object[] objects)
        {
            this.objects = Stream.of(objects).toArray();
        }

        /**
         * Gets whether the reader has another value to return
         * 
         * @return whether the current index is a valid index into the array.
         */
        public boolean hasValue()
        {
            return currentIndex < objects.length;
        }

        /**
         * Gets the value at the current index in the list. Note that if {@link #hasValue()} returns {@code false},
         * calling this method will throw an {@link IndexOutOfBoundsException}; the reader should not be used again
         * without first calling {@link #reset()}.
         * 
         * @return the value.
         * @throws IndexOutOfBoundsException
         *             if the index is not a valid index into the list.
         */
        public Object value()
        {
            return objects[currentIndex];
        }

        /**
         * Advances the reader to the next possible index.
         * 
         * @return the reader, to enable calls to be chained.
         */
        public DataReader advance()
        {
            currentIndex++;
            return this;
        }

        /**
         * Resets the reader to start again at the beginning of the list.
         * 
         * @return the reader, to enable calls to be chained.
         */
        public DataReader reset()
        {
            currentIndex = 0;
            return this;
        }
    }
}
