package org.codejive.jsonc.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class JsonParserTest {

    @ParameterizedTest
    @MethodSource("jsonOrgPassPathProvider")
    public void jsonOrgPassFiles(Path testFile) throws IOException, JsonParseException {
        Object result =
                (new JsonParser(JsonParserConfig.strictJson()))
                        .parse(Files.newBufferedReader(jsonOrgRoot().resolve(testFile)));
        System.out.println("Result (" + result.getClass() + ") = " + result);
    }

    @ParameterizedTest
    @MethodSource("jsonOrgFailPathProvider")
    public void jsonOrgFailFiles(Path testFile) {
        assertThrows(
                JsonParseException.class,
                () -> {
                    Object result =
                            (new JsonParser(JsonParserConfig.strictJson()))
                                    .parse(
                                            Files.newBufferedReader(
                                                    jsonOrgRoot().resolve(testFile)));
                    System.err.println("Result (" + result.getClass() + ") = " + result);
                });
    }

    @ParameterizedTest
    @MethodSource("lenientJsonPathProvider")
    public void lenientPassFiles(Path testFile) throws IOException, JsonParseException {
        Object result =
                (new JsonParser(JsonParserConfig.lenientJson()))
                        .parse(Files.newBufferedReader(lenientJsonRoot().resolve(testFile)));
        System.out.println("Result (" + result.getClass() + ") = " + result);
    }

    static Stream<Path> jsonOrgPassPathProvider() {
        return pathProvider(jsonOrgRoot())
                .filter(p -> p.getFileName().toString().startsWith("pass"));
    }

    static Stream<Path> jsonOrgFailPathProvider() {
        return pathProvider(jsonOrgRoot())
                .filter(p -> p.getFileName().toString().startsWith("fail"));
    }

    static Stream<Path> lenientJsonPathProvider() {
        return pathProvider(lenientJsonRoot())
                .filter(p -> p.getFileName().toString().startsWith("pass"));
    }

    static Path jsonOrgRoot() {
        return resourceRoot("json_org_test_suite");
    }

    static Path lenientJsonRoot() {
        return resourceRoot("lenient_json");
    }

    static Path resourceRoot(String resource) {
        URL res = JsonParserTest.class.getClassLoader().getResource(resource);
        try {
            return Paths.get(res.toURI());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Stream<Path> pathProvider(Path root) {
        try {
            return Files.list(root).map(p -> root.relativize(p));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
