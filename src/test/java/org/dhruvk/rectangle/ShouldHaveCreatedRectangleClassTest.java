package org.dhruvk.rectangle;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.Paths.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ShouldHaveCreatedRectangleClassTest {
    private static final Path directoryWithNoJavaFiles = get("src/test/resources/org/dhruvk/rectangle/directory_with_no_java_files").toAbsolutePath();
    private static final Path directoryWithOnlyRectangleFile = get("src/test/resources/org/dhruvk/rectangle/directory_with_only_rectangle_file").toAbsolutePath();

//     TODO - unable to add test for empty directory as git won't add it?

    @Test
    void shouldFailOnDirectoryContainingNoJavaFiles() throws IOException {

        Rule shouldHaveCreatedRectangleClass = new ShouldHaveCreatedRectangleClass(directoryWithNoJavaFiles);

        Optional<String> expected = Optional.of("NO_JAVA_FILE_FOUND");

        assertThat(shouldHaveCreatedRectangleClass.suggestionKey(), is(expected)); // TODO - is it possible to get this as a key from resource bundle to prevent hardcoding...?
    }

    @Test
    void shouldPassOnDirectoryContainingRectangleClassFile() {
        Rule shouldHaveCreatedRectangleClass = new ShouldHaveCreatedRectangleClass(directoryWithOnlyRectangleFile);

        assertThat(shouldHaveCreatedRectangleClass.suggestionKey(), is(Optional.empty()));
    }
}