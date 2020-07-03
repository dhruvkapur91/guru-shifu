package org.dhruvk.rectangle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ShouldHaveCreatedRectangleClass implements Rule {

    private final Path sourcePath;

    public ShouldHaveCreatedRectangleClass(Path sourceAbsolutePath) {
        assert sourceAbsolutePath.startsWith("/") : "Expected absolute path, but looks like you passed a relative path -> " + sourceAbsolutePath;
        this.sourcePath = sourceAbsolutePath;
    }

    @Override
    public Optional<String> suggestionKey() {
        try {
            long count = Files.walk(sourcePath) // Todo - better name?
                    .filter(Files::isRegularFile)
                    .filter(file -> file.endsWith("Rectangle.java"))
                    .count();
            if (count == 1) {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new RuntimeException(e); // This logic of traversing a path and figuring out if the file is present should likely be extracted out...
        }


        return Optional.of("NO_JAVA_FILE_FOUND");
    }
}
