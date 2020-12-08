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

import java.util.Collection;
import java.util.stream.Stream;

/**
 * A builder that creates the Cartesian product of two or more collections of objects. The objects can be specified
 * individually, or in a stream, an array or a collection.
 * 
 * <p>
 * The Cartesian product of a set of collections consists of all possible combinations of an element from the first
 * collection, followed by an element from the second collection, followed by an element from each subsequent
 * collection. For example, the Cartesian product of the collections (A, B), (C, D) and (E, F) consists of (A, C, E),
 * (A, C, F), (A, D, E), (A, D, F), (B, C, E), (B, C, F), (B, D, E) and (B, D, F).
 * 
 * <p>
 * An example of the use of this class to build the above Cartesian product:
 * 
 * <pre>
 * CartesianProductBuilder.of(A, B).and(C, D).and(E, F).build();
 * </pre>
 * 
 * @author Jeremy Hicks
 *
 */
public class CartesianProductBuilder implements BuilderImpl
{
    /**
     * Gets a base builder which contains the objects in the specified stream.
     * 
     * @param objects
     *            the objects.
     * @return the builder.
     */
    public static CartesianProductBuilderBase of(Stream<?> objects)
    {
        return new CartesianProductBuilderBase(objects);
    }

    /**
     * Gets a base builder which contains the objects in the specified collection.
     * 
     * @param objects
     *            the objects.
     * @return the builder.
     */
    public static CartesianProductBuilderBase of(Collection<?> objects)
    {
        return new CartesianProductBuilderBase(objects.stream());
    }

    /**
     * Gets a base builder which contains the specified objects.
     * 
     * @param objects
     *            the objects.
     * @return the builder.
     */
    @SafeVarargs
    public static <T> CartesianProductBuilderBase of(T... objects)
    {
        return new CartesianProductBuilderBase(Stream.of(objects));
    }

    /**
     * The base builder that constructs the Cartesian product with which the objects in this builder are to be permuted.
     */
    private final BuilderImpl base;

    /**
     * A set of objects which are to be permuted with the Cartesian product constructed by the base.
     */
    private final Object[] objects;

    /**
     * Creates a builder from the specified base builder, and some objects that are to be permuted with the Cartesian
     * product that the builder constructs.
     * 
     * @param base
     *            the base builder.
     * @param objects
     *            the objects to be permuted.
     */
    private CartesianProductBuilder(BuilderImpl base, Stream<?> objects)
    {
        this.base = base;
        this.objects = objects.toArray();
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
    @SafeVarargs
    public final <T> CartesianProductBuilder and(T... objects)
    {
        return new CartesianProductBuilder(this, Stream.of(objects));
    }

    @Override
    public Stream<Combination> build()
    {
        return base.build().flatMap(c -> Stream.of(objects).map(o -> c.with(o)));
    }

    /**
     * A base object that is used as an intermediate stage in building a {@link CartesianProductBuilder}. This builder
     * cannot itself be used to build a Cartesian product as at least two collections must be specified; a second
     * collection can be added to it by calling one of the variants of the {@code and()} method.
     */
    public static class CartesianProductBuilderBase implements BuilderImpl
    {
        /**
         * A set of objects which are to be permuted with other objects in the final Cartesian product.
         */
        private final Object[] objects;

        private CartesianProductBuilderBase(Stream<?> objects)
        {
            this.objects = objects.toArray();
        }

        /**
         * Gets a builder which will permute the objects in the specified stream with the Cartesian product constructed
         * by this builder.
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
         * Gets a builder which will permute the objects in the specified collection with the Cartesian product
         * constructed by this builder.
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
         * Gets a builder which will permute the specified objects with the Cartesian product constructed by this
         * builder.
         * 
         * @param objects
         *            the objects.
         * @return the new builder.
         */
        @SafeVarargs
        public final <T> CartesianProductBuilder and(T... objects)
        {
            return new CartesianProductBuilder(this, Stream.of(objects));
        }

        @Override
        public Stream<Combination> build()
        {
            return Stream.of(objects).map(Combination::new);
        }
    }
}
