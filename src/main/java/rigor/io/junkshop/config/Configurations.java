package rigor.io.junkshop.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

public class Configurations {

  private static Configurations configurations = new Configurations();
  private static final String S = File.separator;
  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");
  private static final String DEFAULT_FILENAME = "config.json";
  private static final String DIR_NAME = "steelman";
  public static String HOST = "http://localhost:8080/api";

  private ObjectMapper mapper = new ObjectMapper();

  public static Configurations getInstance() {
    return configurations;
  }

  /**
   * TODO
   *
   * @param host
   */
  public void setHost(String host) {
    String stringContent = getAsString(getConfigFile().getAbsoluteFile().toString());
    Map map = null;
    try {
      map = mapper.readValue(stringContent, new TypeReference<Map>(){});
    } catch (IOException e) {
      e.printStackTrace();
    }
    map.put(ConfigKeys.DB_HOST, host); // TODO now host is being put to map, need to save to file
  }

  private String getAsString(String filePath) {
    StringBuilder contentBuilder = new StringBuilder();
    try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
      stream.forEach(contentBuilder::append);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return contentBuilder.toString();
  }

  public void save(ConfigKeys key, Object value) {
    save(key.name(), value);
  }

  public void save(String key, Object value) {
    File configFile = getConfigFile();

  }

  private File getConfigFile() {
    String absoluteFileName = getFilePath() + S + DEFAULT_FILENAME;
    return new File(absoluteFileName);
  }

  private String getFilePath() {
    String filePath = TEMPDIR + (S + DIR_NAME);
    boolean mkdirs = new File(filePath).mkdirs();
    return filePath;
  }


}
