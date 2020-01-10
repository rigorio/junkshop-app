package rigor.io.junkshop.cache;

import rigor.io.junkshop.models.customProperties.CustomProperty;
import rigor.io.junkshop.models.customProperties.CustomPropertyHandler;
import rigor.io.junkshop.models.customProperties.CustomPropertyKeys;

import java.util.HashMap;
import java.util.Map;

public class PublicCache {
  public static Map<String, String> values = new HashMap<>();

  public static String getContact() {
    if (values.containsKey(CustomPropertyKeys.RECEIPT_CONTACT.name())) {
      return values.get(CustomPropertyKeys.RECEIPT_CONTACT.name());
    }
    CustomPropertyHandler handler = new CustomPropertyHandler();
    CustomProperty property = handler.getProperty(CustomPropertyKeys.RECEIPT_CONTACT.name());
    return property.getValue();
  }
}
