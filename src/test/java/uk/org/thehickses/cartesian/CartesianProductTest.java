package uk.org.thehickses.cartesian;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class CartesianProductTest
{
    @Test
    void testNoneEmpty()
    {
        Iterator<Combination> it = CartesianProduct
                .of("a b".split(" "))
                .and(Arrays.asList(1, 2))
                .and(1.1, 2.2)
                .and(Stream.of(true, false))
                .build()
                .iterator();
        Stream.of(
        // @formatter:off
            contentsChecker("a", 1, 1.1, true),
            contentsChecker("a", 1, 1.1, false),
            contentsChecker("a", 1, 2.2, true),
            contentsChecker("a", 1, 2.2, false),
            contentsChecker("a", 2, 1.1, true),
            contentsChecker("a", 2, 1.1, false),
            contentsChecker("a", 2, 2.2, true),
            contentsChecker("a", 2, 2.2, false),
            contentsChecker("b", 1, 1.1, true),
            contentsChecker("b", 1, 1.1, false),
            contentsChecker("b", 1, 2.2, true),
            contentsChecker("b", 1, 2.2, false),
            contentsChecker("b", 2, 1.1, true),
            contentsChecker("b", 2, 1.1, false),
            contentsChecker("b", 2, 2.2, true),
            contentsChecker("b", 2, 2.2, false)
        // @formatter:on
        ).forEach(cc -> cc.accept(it.next()));
        assertThat(it.hasNext()).isFalse();
    }

    private Consumer<Combination> contentsChecker(String first, int second, double third,
            boolean fourth)
    {
        return c ->
            {
                assertThat(c.next(String.class)).isEqualTo(first);
                assertThat(c.nextInt()).isEqualTo(second);
                assertThat(c.nextDouble()).isEqualTo(third);
                assertThat(c.nextBoolean()).isEqualTo(fourth);
                assertThat(c.hasNext()).isEqualTo(false);
            };
    }

    @Test
    void testOneEmpty()
    {
        Stream<Combination> result = CartesianProduct
                .of("a", "b")
                .and(1, 2)
                .and()
                .and(1.1, 2.2)
                .and(true, false)
                .build();
        assertThat(result.iterator().hasNext()).isFalse();
    }
}
