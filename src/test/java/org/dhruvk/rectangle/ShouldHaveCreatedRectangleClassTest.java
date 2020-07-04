package org.dhruvk.rectangle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.nio.file.Paths.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ShouldHaveCreatedRectangleClassTest {
    // Todo not sure if these should be directories in source code or generated per test and then cleared up?
    // I'm inclining to create them on fly now, with a feature of persistance/inspection if required... there is a lot of duplication in names and it'll cause difficulty in refactoring if required
    private static final Path directoryWithNoJavaFiles = get("src/test/resources/org/dhruvk/rectangle/directory_with_no_java_files").toAbsolutePath();
    private static final Path directoryWithOnlyRectangleFile = get("src/test/resources/org/dhruvk/rectangle/directory_with_only_rectangle_file").toAbsolutePath();
    private static final Path directoryWithOnlyRectangleFileWithoutFollowingConventions = get("src/test/resources/org/dhruvk/rectangle/directory_with_only_rectangle_file_without_following_conventions").toAbsolutePath();
    private static final Path directoryWithRectanglePlusUnnecessaryFiles = get("src/test/resources/org/dhruvk/rectangle/directory_with_rectangle_plus_unnecessary_files").toAbsolutePath();
    private static final Path directoryWithRectangleFileWithTypos = get("src/test/resources/org/dhruvk/rectangle/directory_with_only_rectangle_file_with_some_typos").toAbsolutePath();

//     TODO - unable to add test for empty directory as git won't add it?

    @Test
    void shouldFailOnDirectoryContainingNoJavaFiles() {
        assertThat(findFeedbackFor(directoryWithNoJavaFiles), is(Optional.of("NO_JAVA_FILE_FOUND"))); // TODO - is it possible to get this as a key from resource bundle to prevent hardcoding...?
    }

    @Test
    void shouldPassOnDirectoryContainingRectangleClassFile() {
        assertThat(findFeedbackFor(directoryWithOnlyRectangleFile), is(Optional.empty()));
    }

    @Test
    void shouldFailOnDirectoryContainingUnnecessaryClassFile() {
        Optional<String> expected = Optional.of("UNNECESSARY_FILES_FOUND");
        assertThat(findFeedbackFor(directoryWithRectanglePlusUnnecessaryFiles), is(expected));
    }

    @Test
    void shouldFailIfNameOfTheClassDoesNotFollowJavaConventions() {
        Optional<String> expected = Optional.of("JAVA_FILE_NAMING_CONVENTIONS_NOT_FOLLOWED");
        assertThat(findFeedbackFor(directoryWithOnlyRectangleFileWithoutFollowingConventions), is(expected));
    }

    @Test
    void shouldRequireThatTheClientSendsAnAbsolutePath() {
        AssertionError assertionError = Assertions.assertThrows(AssertionError.class, () -> new ShouldHaveCreatedRectangleClass(get("some/relative/directory")));
        assertThat(assertionError.getMessage(), is("Expected absolute path, but looks like you passed a relative path -> some/relative/directory"));
    }

    @Test
    void shouldReportUnknownScenarioWhenSeeingATypoForNow() {
        assertThat(findFeedbackFor(directoryWithRectangleFileWithTypos), is(Optional.of("UNKNOWN_SCENARIO")));
    }

    Optional<String> findFeedbackFor(Path path) {
        return new ShouldHaveCreatedRectangleClass(path).suggestionKey();
    }
}