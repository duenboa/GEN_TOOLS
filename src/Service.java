import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Service {

    /**
     * --------------------- SQL ------------------- 注意, sql表名区分大小写
     * SELECT '$name'||atc.COLUMN_NAME||'$type'||atc.DATA_TYPE||'$scale'||atc.DATA_SCALE||'$cmt'||c.COMMENTS||'$end##########'
     * FROM all_tab_columns atc JOIN all_col_comments c ON atc.TABLE_NAME = c.TABLE_NAME AND c.COLUMN_NAME = atc.COLUMN_NAME WHERE
     * c.TABLE_NAME = 'BUS_JIJIN_FIXED_SIM_PROFIT';
     */
    public static String genDTO(String filedStr) {
        StringBuilder sb = new StringBuilder("");
        String[] rows = filedStr.split("##########");
        for (String row : rows) {
            if (!row.contains("$end")) {
                break;
            }
            String fieldName = row.substring(row.indexOf("$name") + 5, row.indexOf("$type"));
            boolean isDecimal = row.substring(row.indexOf("$scale") + 6, row.indexOf("$cmt")).length() > 0;
            String fieldType;
            if (isDecimal) {
                fieldType = "BigDecimal";
            } else {
                fieldType = dbtypeWithJavatypeMap.get(row.substring(row.indexOf("$type") + 5, row.indexOf("$scale")));
            }
            String comment = row.substring(row.indexOf("$cmt") + 4, row.indexOf("$end"));

            sb.append(" /** " + comment + "**/  \n")
            .append("private " + fieldType + " " + lineToHump(fieldName + ";  \n"));

            System.out.println( "/** " + comment + "**/ \n");
            System.out.println("private " + fieldType + " " + lineToHump(fieldName + ";"));
        }

        return sb.toString();
    }

    /**
     *  field property;
     *  eg:
     *  user_name userName;
     */
    public static String genFiledPropertyLine(String filedStr) {
        StringBuilder sb = new StringBuilder("");

        String[] rows = filedStr.split("##########");
        for (String row : rows) {
            if (!row.contains("$end")) {
                break;
            }
            String fieldName = row.substring(row.indexOf("$name") + 5, row.indexOf("$type"));

            sb.append(fieldName + " " + lineToHump(fieldName + "; \r\n"));
            System.out.println(fieldName + " " + lineToHump(fieldName + ";"));
        }
        return sb.toString();
    }




    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /** 下划线转驼峰 */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /** 驼峰转下划线(简单写法，效率低于{@link #humpToLine2(String)}) */
    public static String humpToLine(String str) {
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /** 驼峰转下划线,效率比上面高 */
    public static String humpToLine2(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static Map<String, String> dbtypeWithJavatypeMap = new HashMap<String, String>() {{
        put("NUMBER", "Long");
        put("DATE", "Date");
        put("VARCHAR2", "String");
    }};
}
