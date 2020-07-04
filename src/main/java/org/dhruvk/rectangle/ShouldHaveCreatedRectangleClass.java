package org.dhruvk.rectangle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
    public List<String> suggestionKey() {
        // TODO, maybe we should return all applicable feedbacks... not just one, and prioritization should be a separate activity...
        try {
            if (moreThanOneFileExists()) return List.of("UNNECESSARY_FILES_FOUND");
            if (lowerCaseClassFilesFound()) return List.of("JAVA_FILE_NAMING_CONVENTIONS_NOT_FOLLOWED");
            if (noJavaFileFound()) return List.of("NO_JAVA_FILE_FOUND");
            if (doesRectangleClassExist()) return List.of();
            return List.of("UNKNOWN_SCENARIO");
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

    private boolean doesRectangleClassExist() throws IOException {
        return Files.walk(absoluteSourcePath)
                .filter(Files::isRegularFile)
                .anyMatch(file -> file.endsWith("Rectangle.java"));
    }

    private Predicate<Path> fileStartsWithALowerCase() {
        return file -> {
            String lowerCaseRegex = "[a-z]+.*";
            return Pattern.compile(lowerCaseRegex).matcher(file.getFileName().toString()).matches();
        };
    }

    private Predicate<Path> isJavaFile() {
        return file -> getExtension(file.toString()).equals("java");
    }
}
