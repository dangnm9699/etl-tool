package app.model;

import java.util.ArrayList;
import java.util.List;

public enum StringConvertType {
    Capitalize,
    Uppercase,
    Lowercase,
    Ignore;

    public static List<String> getList() {
        List<String> list = new ArrayList<>();
        for (StringConvertType stringConvertType : StringConvertType.values()) {
            list.add(stringConvertType.toString());
        }
        return list;
    }
}
