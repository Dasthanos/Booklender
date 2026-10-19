package kg.attractor.java.server;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {
    public static Map<String, String> parseUrlEncoded(String rawLines, String delimiter) {

        String[] pairs = rawLines.split(delimiter);

        Stream<Map.Entry<String, String>> stream = Arrays.stream(pairs)

                .map(Utils::decode)

                .filter(Optional::isPresent)

                .map(Optional::get);

        return stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }
}
