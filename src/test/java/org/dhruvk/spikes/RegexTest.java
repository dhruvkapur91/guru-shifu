package org.dhruvk.spikes;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegexTest {
    @Test
    void findIfFileStartsWithSmallCase() {
        assertTrue(Pattern.compile("[a-z]+.*").matcher("rectangle.java").matches());
        assertFalse(Pattern.compile("[a-z]+.*").matcher("Rectangle.java").matches());
    }
}
