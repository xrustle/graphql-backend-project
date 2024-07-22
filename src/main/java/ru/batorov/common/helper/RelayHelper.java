package ru.batorov.common.helper;

import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelayHelper {

  public static String toGlobalId(String type, long id, long offset) {
    var encoder = java.util.Base64.getUrlEncoder().withoutPadding();

    return encoder.encodeToString(
        (type + ":" + id + ":" + offset).getBytes(StandardCharsets.UTF_8));
  }
}
