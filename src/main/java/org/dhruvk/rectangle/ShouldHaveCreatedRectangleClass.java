package org.dhruvk.rectangle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.apache.commons.io.FilenameUtils.getExtension;

public class ShouldHaveCreatedRectangleClass implements Rule {

    private final Path absoluteSourcePath;

    public ShouldHaveCreatedRectangleClass(Path sourceAbsolutePath) {
        assert sourceAbsolutePath.startsWith("/") : "Expected absolute path, but looks like you passed a relative path -> " + sourceAbsolutePath;
        this.absoluteSourcePath = sourceAbsolutePath;
    }

    @Override
    public Optional<String> suggestionKey() {
        // TODO, maybe we should return all applicable feedbacks... not just one, and prioritization should be a separate activity...
        try {
            if (moreThanOneFileExists()) return Optional.of("UNNECESSARY_FILES_FOUND");
            if (lowerCaseClassFilesFound()) return Optional.of("JAVA_FILE_NAMING_CONVENTIONS_NOT_FOLLOWED");
            if (noJavaFileFound()) return Optional.of("NO_JAVA_FILE_FOUND");
            if (doesRectangleClassExist()) return Optional.empty();
            return Optional.of("UNKNOWN_SCENARIO");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean lowerCaseClassFilesFound() throws IOException {
        return Files.walk(absoluteSourcePath)
                .filter(Files::isRegularFile)
                .filter(isJavaFile())
                .anyMatch(fileStartsWithALowerCase());
    }

    private Predicate<Path> fileStartsWithALowerCase() {
        return file -> {
            String lowerCaseRegex = "[a-z]+.*";
            return Pattern.compile(lowerCaseRegex).matcher(file.getFileName().toString()).matches();
        };
    }

    private boolean noJavaFileFound() throws IOException { // TODO - wait for removing the duplication of the Files API... lets see enough of it to understand what will be a good abstraction
        return Files.walk(absoluteSourcePath)
                .filter(Files::isRegularFile)
                .noneMatch(isJavaFile());
    }

    private boolean moreThanOneFileExists() throws IOException {
        return Files.walk(absoluteSourcePath)
                .filter(Files::isRegularFile)
                .count() > 1;
    }

    private Predicate<Path> isJavaFile() {
        return file -> getExtension(file.toString()).equals("java");
    }

    private boolean doesRectangleClassExist() {
        try {
            long count = Files.walk(absoluteSourcePath) // Todo - better name?
                    .filter(Files::isRegularFile)
                    .filter(file -> file.endsWith("Rectangle.java"))
                    .count();
            if (count == 1) {
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // This logic of traversing a path and figuring out if the file is present should likely be extracted out...
        }
        return false;
    }
}
