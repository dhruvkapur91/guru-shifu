package org.dhruvk.rectangle;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.nio.file.Paths.get;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ShouldHaveCreatedRectangleClassTest {

//     TODO - unable to add test for empty directory as git won't add it?

    @Test
    void shouldFailOnDirectoryContainingNoJavaFiles() throws IOException {

        String directoryWithNoJavaFiles = "org/dhruvk/rectangle/directory_with_no_java_files";
        Rule shouldHaveCreatedRectangleClass = new ShouldHaveCreatedRectangleClass(get(directoryWithNoJavaFiles));

        Optional<String> expected = Optional.of("NO_JAVA_FILE_FOUND");

        assertThat(shouldHaveCreatedRectangleClass.suggestionKey(), is(expected)); // TODO - is it possible to get this as a key from resource bundle to prevent hardcoding...?
    }
}