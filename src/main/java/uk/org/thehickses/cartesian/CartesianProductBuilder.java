package uk.org.thehickses.cartesian;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.LongBinaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CartesianProductBuilder
{
    public static CartesianProductBuilder of(Stream<?> objects)
    {
        return new CartesianProductBuilder(null, objects);
    }

    public static CartesianProductBuilder of(Collection<?> objects)
    {
        return new CartesianProductBuilder(null, objects.stream());
    }

    public static CartesianProductBuilder of(Object... objects)
    {
        return new CartesianProductBuilder(null, Stream.of(objects));
    }

    private final Object[][] objects;

    private CartesianProductBuilder(CartesianProductBuilder base, Stream<?> objects)
    {
        int length = (base == null ? 0 : base.objects.length) + 1;
        this.objects = new Object[length][];
        if (base != null)
            System.arraycopy(base.objects, 0, this.objects, 0, base.objects.length);
        this.objects[length - 1] = objects.toArray();
    }

    public CartesianProductBuilder and(Stream<?> objects)
    {
        return new CartesianProductBuilder(this, objects);
    }

    public CartesianProductBuilder and(Collection<?> objects)
    {
        return new CartesianProductBuilder(this, objects.stream());
    }

    public CartesianProductBuilder and(Object... objects)
    {
        return new CartesianProductBuilder(this, Stream.of(objects));
    }

    public Stream<Combination> build()
    {
        return StreamSupport.stream(new CartesianSpliterator(objects), false);
    }

    public static class Combination
    {
        private final Iterator<Object> iterator;

        private Combination(Object[] objects)
        {
            iterator = Stream.of(objects).iterator();
        }

        public <T> T next(Class<T> type)
        {
            Object next;
            synchronized (iterator)
            {
                next = iterator.next();
            }
            return type.cast(next);
        }

        public int nextInt()
        {
            return next(Integer.class);
        }

        public long nextLong()
        {
            return next(Long.class);
        }

        public double nextDouble()
        {
            return next(Double.class);
        }

        public boolean nextBoolean()
        {
            return next(Boolean.class);
        }
    }

    private static class CartesianSpliterator extends AbstractSpliterator<Combination>
    {
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

    private static class DataReader
    {
        private final Object[] objects;
        private int currentIndex;

        private DataReader(Object[] objects)
        {
            this.objects = Stream.of(objects).toArray();
        }

        public boolean hasValue()
        {
            return currentIndex < objects.length;
        }

        public Object value()
        {
            return objects[currentIndex];
        }

        public DataReader advance()
        {
            currentIndex++;
            return this;
        }

        public DataReader reset()
        {
            currentIndex = 0;
            return this;
        }
    }
}
