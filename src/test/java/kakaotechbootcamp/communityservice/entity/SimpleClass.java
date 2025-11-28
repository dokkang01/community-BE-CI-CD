package kakaotechbootcamp.communityservice.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleClass {
    @Test
    void simpleCheck(){
        String result = "Hello".toUpperCase();
        assertThat(result).isEqualTo("HELLO");

    }
}
