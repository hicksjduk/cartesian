package uk.org.thehickses.cartesian;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

import uk.org.thehickses.cartesian.CartesianProductBuilder.Combination;

class CartesianProductBuilderTest
{
    @Test
    void testNoneEmpty()
    {
        Iterator<Combination> it = CartesianProductBuilder
                .of("a", "b")
                .and(1, 2)
                .and(1.1, 2.2)
                .and(true, false)
                .build()
                .iterator();
        contentsChecker("a", 1, 1.1, true).accept(it.next());
        contentsChecker("a", 1, 1.1, false).accept(it.next());
        contentsChecker("a", 1, 2.2, true).accept(it.next());
        contentsChecker("a", 1, 2.2, false).accept(it.next());
        contentsChecker("a", 2, 1.1, true).accept(it.next());
        contentsChecker("a", 2, 1.1, false).accept(it.next());
        contentsChecker("a", 2, 2.2, true).accept(it.next());
        contentsChecker("a", 2, 2.2, false).accept(it.next());
        contentsChecker("b", 1, 1.1, true).accept(it.next());
        contentsChecker("b", 1, 1.1, false).accept(it.next());
        contentsChecker("b", 1, 2.2, true).accept(it.next());
        contentsChecker("b", 1, 2.2, false).accept(it.next());
        contentsChecker("b", 2, 1.1, true).accept(it.next());
        contentsChecker("b", 2, 1.1, false).accept(it.next());
        contentsChecker("b", 2, 2.2, true).accept(it.next());
        contentsChecker("b", 2, 2.2, false).accept(it.next());
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
            };
    }

    @Test
    void testOneEmpty()
    {
        Stream<Combination> result = CartesianProductBuilder
                .of("a", "b")
                .and(1, 2)
                .and()
                .and(1.1, 2.2)
                .and(true, false)
                .build();
        assertThat(result.iterator().hasNext()).isFalse();
    }
}
