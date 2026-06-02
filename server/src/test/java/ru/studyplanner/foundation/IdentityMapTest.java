package ru.studyplanner.foundation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class IdentityMapTest {

    @Test
    void getOrPutReturnsSameInstanceForSameKey() {
        IdentityMap<Long, StringBuilder> identityMap = new IdentityMap<>();
        AtomicInteger factoryCalls = new AtomicInteger();

        StringBuilder first = identityMap.getOrPut(1L, key -> {
            factoryCalls.incrementAndGet();
            return new StringBuilder("assignment-").append(key);
        });
        StringBuilder second = identityMap.getOrPut(1L, key -> {
            factoryCalls.incrementAndGet();
            return new StringBuilder("other-").append(key);
        });

        assertThat(second).isSameAs(first);
        assertThat(factoryCalls).hasValue(1);
        assertThat(identityMap.size()).isEqualTo(1);
    }
}
