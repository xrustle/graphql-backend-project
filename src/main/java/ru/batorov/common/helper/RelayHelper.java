package ru.batorov.common.helper;

import java.nio.charset.StandardCharsets;

public class RelayHelper {

  public static String toGlobalId(String type, long id, long offset) {
    var encoder = java.util.Base64.getUrlEncoder().withoutPadding();

    return encoder.encodeToString(
        (type + ":" + id + ":" + offset).getBytes(StandardCharsets.UTF_8));
  }

  public static ResolvedGlobalId fromGlobalId(String globalId) {
    var decoder = java.util.Base64.getUrlDecoder();
    var parts = new String(decoder.decode(globalId), StandardCharsets.UTF_8).split(":", 3);

    if (parts.length != 3) {
      throw new IllegalArgumentException(
          String.format("Expected a valid global id, got %s.", globalId));
    }

    return new ResolvedGlobalId(parts[0], Long.parseLong(parts[1]), Long.parseLong(parts[2], 10));
  }

  public record ResolvedGlobalId(String type, long id, long offset) {}
}
