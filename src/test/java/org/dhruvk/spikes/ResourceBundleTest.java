package org.dhruvk.spikes;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.MissingResourceException;

import static java.util.ResourceBundle.getBundle;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ResourceBundleTest {
    @Test
    void shouldThrowExceptionIfKeyIsNotFound() {
        Locale english = new Locale("en");
        java.util.ResourceBundle resourceBundle = getBundle("messages", english);

        assertThrows(MissingResourceException.class, () -> {
            resourceBundle.getString("NON_EXISTING_KEY");
        });

    }

    @Test
    void shouldBeAbleToAccessTheEnglishResourceBundleFromTests() {
        Locale english = new Locale("en");
        java.util.ResourceBundle resourceBundle = getBundle("messages", english);

        assertThat(resourceBundle.getString("NO_JAVA_FILE_FOUND"), isNonEmptyString());
        ;
    }

    @Test
    void shouldBeAbleToAccessDefaultEnglishIndiaLocale() {
        java.util.ResourceBundle resourceBundle = getBundle("messages");

        assertThat(resourceBundle.getString("NO_JAVA_FILE_FOUND"), isNonEmptyString());
        ;
    }


    private Matcher<String> isNonEmptyString() {
        return is(not(isEmptyString()));
    }
}
