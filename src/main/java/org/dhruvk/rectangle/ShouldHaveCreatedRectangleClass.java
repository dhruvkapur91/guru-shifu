package org.dhruvk.rectangle;

import java.nio.file.Path;
import java.util.Optional;

public class ShouldHaveCreatedRectangleClass implements Rule  {

    public ShouldHaveCreatedRectangleClass(Path sourcePath) {
    }

    @Override
    public Optional<String> suggestionKey() {
        return Optional.of("NO_JAVA_FILE_FOUND");
    }
}
