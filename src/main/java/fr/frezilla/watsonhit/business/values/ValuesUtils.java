package fr.frezilla.watsonhit.business.values;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValuesUtils {
    
    public static String toUppercase(String s) {
        return StringUtils.upperCase(s);
    }
    
    public static String replaceSpecialsCharacters(String s) {
        String result = null;
        if (s != null) {
            result = StringUtils.stripAccents(s);
        }
        return result;
    }
    
}
