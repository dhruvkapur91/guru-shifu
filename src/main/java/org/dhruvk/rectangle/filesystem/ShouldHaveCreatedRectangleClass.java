package org.dhruvk.rectangle.filesystem;

import org.dhruvk.rectangle.Rule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
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
    public Set<String> suggestionKey() {
        Set<String> feedbacks = new HashSet<>();
        try {
            if (moreThanOneFileExists()) feedbacks.add("UNNECESSARY_FILES_FOUND");
            if (lowerCaseClassFilesFound()) feedbacks.add("JAVA_FILE_NAMING_CONVENTIONS_NOT_FOLLOWED");
            if (hasUsedSnakeCaseInNaming()) feedbacks.add("JAVA_FILE_NAMING_CONVENTIONS_NOT_FOLLOWED");
            if (noJavaFileFound()) feedbacks.add("NO_JAVA_FILE_FOUND");
            if (doesRectangleClassExist()) feedbacks.add("FOUND_RECTANGLE_CLASS");
            if (possibleOverGeneralization()) feedbacks.add("POSSIBLE_OVER_GENERALIZATION");
            if (usingManagerClasses()) feedbacks.add("AVOID_MANAGER_CLASSES");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return feedbacks.isEmpty() ? Set.of("UNKNOWN_SCENARIO") : feedbacks;
    }

    private boolean lowerCaseClassFilesFound() throws IOException {
        return Files.walk(absoluteSourcePath)
                .filter(Files::isRegularFile)
                .filter(isJavaFile())
                .anyMatch(fileStartsWithALowerCase());
    }

    private boolean hasUsedSnakeCaseInNaming() throws IOException {
        return Files.walk(absoluteSourcePath)
                .filter(Files::isRegularFile)
                .filter(isJavaFile())
                .anyMatch(file -> file.getFileName().toString().contains("_"));
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

    private boolean possibleOverGeneralization() throws IOException {
        return Files.walk(absoluteSourcePath)
                .filter(Files::isRegularFile)
                .anyMatch(file -> file.getFileName().toString().toLowerCase().contains("shape"));
    }

    private boolean usingManagerClasses() throws IOException {
        return Files.walk(absoluteSourcePath)
                .filter(Files::isRegularFile)
                .anyMatch(file -> file.getFileName().toString().toLowerCase().contains("calculator"));
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
