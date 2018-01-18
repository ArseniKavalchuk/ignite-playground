package ru.synesis.kipod.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestUtils {

    public static String readFileFromCp(String path, ClassLoader cl) throws IOException, URISyntaxException {
        path = Objects.requireNonNull(path, "Path required");
        URL resource = cl.getResource(path);
        if (resource == null) throw new FileNotFoundException(path);
        return Files
                .readAllLines(new File(resource.toURI()).toPath(), StandardCharsets.UTF_8)
                .stream()
                .collect(Collectors.joining(" "));
    }
    
    public static Collection<String> splitTextByStatements(String text) {
        text = Objects.requireNonNull(text, "Text required");
        String[] res = text.split(";\\n");
        return Arrays.stream(res).map(s -> s + ";").collect(Collectors.toList());
    }
    
}
