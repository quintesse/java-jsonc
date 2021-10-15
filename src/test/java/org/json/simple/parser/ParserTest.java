package org.json.simple.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    @ParameterizedTest
    @MethodSource("jsonOrgPassPathProvider")
    public void jsonOrgPassFiles(Path testFile) throws IOException, ParseException {
        Object result = (new JSONParser()).parse(Files.newBufferedReader(jsonOrgRoot().resolve(testFile)));
        System.out.println("Result (" + result.getClass() + ") = " + result);
    }

    @ParameterizedTest
    @MethodSource("jsonOrgFailPathProvider")
    public void jsonOrgFailFiles(Path testFile) throws IOException, ParseException {
        assertThrows(ParseException.class, () -> {
            Object result = (new JSONParser()).parse(Files.newBufferedReader(jsonOrgRoot().resolve(testFile)));
            System.err.println("Result (" + result.getClass() + ") = " + result);
        });
    }

    static Stream<Path> jsonOrgPassPathProvider() {
        return jsonOrgPathProvider().filter(p -> p.getFileName().toString().startsWith("pass"));
    }

    static Stream<Path> jsonOrgFailPathProvider() {
        return jsonOrgPathProvider().filter(p -> p.getFileName().toString().startsWith("fail"));
    }

    static Stream<Path> jsonOrgPathProvider() {
        try {
            Path root = jsonOrgRoot();
            return Files.list(root).map(p -> root.relativize(p));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Path jsonOrgRoot() {
        URL res = ParserTest.class.getClassLoader().getResource("json_org_test_suite");
        try {
            return Paths.get(res.toURI());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
