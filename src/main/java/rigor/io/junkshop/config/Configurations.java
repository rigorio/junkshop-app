package rigor.io.junkshop.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Configurations {

  private static Configurations configurations = new Configurations();
  private static final String S = File.separator;
  private static final String TEMPDIR = System.getProperty("java.io.tmpdir");
  private static final String DEFAULT_FILENAME = "config.json";
  private static final String DIR_NAME = "steelman";

  private ObjectMapper mapper = new ObjectMapper();
  private static final String AWS_HOST = "http://junkshop-env.27gxaeyq9n.us-east-2.elasticbeanstalk.com/api";
  private static final String LOCALHOST = "http://localhost:8080/api";

  public static Configurations getInstance() {
    return configurations;
  }

  /**
   * TODO
   *
   * @param host
   */
  public void setHost(String host) {
    Map map = getData();
    map.put(ConfigKeys.DB_HOST, host); // TODO now host is being put to map, need to save to file
    File file = getConfigFile();
    try {
      mapper.writeValue(file, map);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getHost() {
    return AWS_HOST;
//    Map<String, Object> map = getData();
////    System.out.println(map);
//    Object o = map.get(ConfigKeys.DB_HOST.toString());
//    return o != null ? o.toString() : null;
  }

  public void save(ConfigKeys key, Object value) {
    save(key.name(), value);
  }

  public void save(String key, Object value) {
    File configFile = getConfigFile();

  }

  @Nullable
  private Map<String, Object> getData() {
    File configFile = getConfigFile();
    Map<String, Object> map = new HashMap<>();
    try {
      if (!configFile.exists())
        configFile.createNewFile();
      String stringContent = getAsString(configFile.getAbsoluteFile().toString());
      if (stringContent.length() > 0)
        map = mapper.readValue(stringContent, new TypeReference<Map<String, Object>>() {});
    } catch (IOException e) {
      e.printStackTrace();
    }
    return map;
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

  private File getConfigFile() {
    String absoluteFileName = getFilePath() + S + DEFAULT_FILENAME;
//    System.out.println(absoluteFileName);
    return new File(absoluteFileName);
  }

  private String getFilePath() {
    String filePath = TEMPDIR + (S + DIR_NAME);
    boolean mkdirs = new File(filePath).mkdirs();
    return filePath;
  }


}
