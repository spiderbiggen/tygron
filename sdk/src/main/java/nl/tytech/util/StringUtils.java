/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Pattern;
import nl.tytech.locale.CurrencyOrder;
import nl.tytech.locale.TCurrency;
import nl.tytech.locale.unit.UnitSystem;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * String manipulation utilities not found elsewhere or placed here to avoid linking to jars unnecessarily
 *
 * @author alex
 */
public abstract class StringUtils {

    public static class CaseInsensitiveToStringComparator<T> implements Comparator<T> {

        @Override
        public int compare(T o1, T o2) {
            return String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString());
        }
    }

    public static class ThreadLocalFormat extends ThreadLocal<NumberFormat> {

        /**
         * Default US settings to use for parsing and formatting numbers
         */
        @Override
        public NumberFormat initialValue() {
            // Correct format US format
            NumberFormat format = NumberFormat.getInstance(Locale.US);
            // no grouping, e.g. 1,000
            format.setGroupingUsed(false);
            // max behind 0 is 6 otherwise it can be casted to double causing strange roundoff errors.
            format.setMaximumFractionDigits(6);
            // new formatter
            return format;
        }
    }

    /**
     * Separator is a pattern used to separate the values in for example arrays. These are separated by one or more Whitespaces.
     */
    public final static Pattern SEPARATOR = Pattern.compile("\\s+");

    /**
     * Placeholder string
     */
    public static final String PLACEHOLDER = StringUtils.LANG_SPLIT + "PLACEHOLDER" + StringUtils.LANG_SPLIT;

    /**
     * Empty String.
     */
    public final static String EMPTY = "";

    public final static String LANG_SPLIT = ":;:";

    /**
     * Whitespace is a String with one whitespace.
     */
    public final static String WHITESPACE = " ";

    /**
     * New line characters: "\n";
     */
    public final static String NEW_LINE = "\n";

    /**
     * Under score "_"
     */
    public final static String UNDER_SCORE = "_";

    /**
     * Colon ":"
     */
    public static final String COLON = ":";

    private static final String HOUR_FORMAT = "%02d:%02d";

    public static final String AND = "and";

    /**
     * An array with chars.
     */
    private static final char[] CHARACTERS = new char[62];

    static {
        for (int idx = 0; idx < 10; ++idx) {
            CHARACTERS[idx] = (char) ('0' + idx);
        }
        for (int idx = 10; idx < 36; ++idx) {
            CHARACTERS[idx] = (char) ('a' + idx - 10);
        }
        for (int idx = 36; idx < 62; ++idx) {
            CHARACTERS[idx] = (char) ('A' + idx - 36);
        }
    }

    /**
     * Formatter per THREAD, thus safe!
     */
    private final static ThreadLocalFormat THREAD_FORMATTERS = new ThreadLocalFormat();

    private final static String HUMAN_STRING_SEPERATOR = ", ";

    public final static String PHONE_DONT_CALL = "PLEASE DONT CALL ME";

    public static String arrayToHumanString(String[] array) {
        return arrayToHumanString(array, AND);
    }

    public static String arrayToHumanString(String[] array, String andWord) {
        if (array.length == 0) {
            return StringUtils.EMPTY;
        } else if (array.length == 1) {
            return array[0];
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            result.append(array[i]);
            if (i < array.length - 2) {
                result.append(HUMAN_STRING_SEPERATOR);
            } else if (i < array.length - 1) {
                result.append(WHITESPACE).append(andWord).append(WHITESPACE);
            }
        }
        return result.toString();
    }

    public static String arrayToString(String[] array) {

        String result = "{ ";
        for (String data : array) {
            result += data + ", ";
        }
        result += " }";
        return result;
    }

    /**
     * Attach a piece of HTML text to an existing HTML text.
     *
     * @param orginalHTML
     * @param attachHTML
     * @return
     */
    public static String attachTextToHTMLString(String orginalHTML, String attachHTML) {
        String htmlText = "</body></html>";
        if (orginalHTML.contains(htmlText)) {
            return orginalHTML.replace(htmlText, attachHTML + htmlText);
        } else {
            return orginalHTML + attachHTML;
        }
    }

    public static String blockToHTMLString(TColor color, String text) {

        return "<table><tr><td width=\"10\" height=\"10\" border=\"2\" bordercolor=\"#000000\" bgcolor=\"" + color.toHTML()
                + "\"></td><td> " + text + "</td></tr></table>";
    }

    /**
     * Make a String’s first letter upper case and the rest lower case.
     *
     * @param s The String to manipulate
     * @return The modified String
     */
    public static String capitalize(String s) {
        if (s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     * Make a String’s first letter upper case.
     *
     * @param s The String to manipulate
     * @return The modified String
     */
    public static String capitalizeFirstLetter(String s) {
        if (s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String capitalizeWithSpacedUnderScores(String string) {

        if (string == null) {
            return null;
        }
        String[] data = string.split(UNDER_SCORE);
        StringBuffer result = new StringBuffer();
        for (String part : data) {
            result.append(StringUtils.capitalize(part));
            result.append(StringUtils.WHITESPACE);
        }
        return result.toString().trim();
    }

    /**
     * Make a String’s first and all letters after the underscore(_) upper case and the rest lower case.
     *
     * @param s The String to manipulate
     * @return The modified String
     */
    public static String capitalizeWithUnderScores(String s) {

        String[] data = s.split(UNDER_SCORE);
        StringBuffer result = new StringBuffer();
        for (String part : data) {
            result.append(StringUtils.capitalize(part));
        }
        return result.toString();
    }

    public static String clamp(String string, int maxChars) {

        if (containsData(string) && string.length() > maxChars) {
            string = string.substring(0, maxChars) + "...";
        }
        return string;
    }

    /**
     * Remove all fonts and span from HTML text
     * @param inputHTML
     * @return
     */
    public static String cleanHTML(String inputHTML) {

        if (!containsData(inputHTML)) {
            return StringUtils.EMPTY;
        }

        inputHTML = inputHTML.replaceAll("style=\"[^\"]*\"", StringUtils.EMPTY);
        inputHTML = inputHTML.replaceAll("<font[^>]*>", StringUtils.EMPTY);
        inputHTML = inputHTML.replaceAll("</font>", StringUtils.EMPTY);
        inputHTML = inputHTML.replaceAll("<span[^>]*>", StringUtils.EMPTY);
        inputHTML = inputHTML.replaceAll("</span>", StringUtils.EMPTY);

        return inputHTML;
    }

    /**
     * Test is the given String is not NULL and if it contains more then one character.
     *
     * @param string
     * @return
     */
    public static boolean containsData(String string) {
        if (string == null) {
            return false;
        }
        return string.length() > 0;
    }

    public static boolean containsNumbers(String string) {
        if (!containsData(string)) {
            return false;
        }
        return string.trim().matches(".*[0-9].*");
    }

    public static int countUpperCase(String s) {
        int cnt = 0;
        for (int i = 0; i < s.length(); i++) {
            int charPoint = s.charAt(i);
            if (charPoint >= 65 && charPoint <= 90) {
                // it’s ucase
                cnt++;
            }
        }
        return cnt;
    }

    public static String dateToHumanString(long timeMillis, boolean date) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeMillis);
        if (date) {
            String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
            return cal.get(Calendar.DAY_OF_MONTH) + StringUtils.WHITESPACE + monthName + StringUtils.WHITESPACE + cal.get(Calendar.YEAR);
        } else {
            return String.format(HOUR_FORMAT, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        }
    }

    public static String dateToShortString(long millis) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(cal.getTime());
    }

    /**
     * Make a String’s first letter lower case.
     *
     * @param s The String to manipulate
     * @return The modified String
     */
    public static String deCapitalizeFirstLetter(String s) {
        if (s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static Map<String, String> decodeMultiLanguage(String string) {
        String[] splitResult = string.split(LANG_SPLIT);
        Map<String, String> decoded = new HashMap<>();
        for (int i = 0; i < splitResult.length - 1; i += 2) {
            decoded.put(splitResult[i + 1], splitResult[i]);
        }
        return decoded;
    }

    public static String encodeFilenameForWeb(String filename) {

        int index = filename.lastIndexOf('/') + 1;
        String pathStub = StringUtils.EMPTY;
        if (index >= 0) {
            pathStub = filename.substring(0, index);
            filename = filename.substring(index);
        }
        filename = filename.replace(WHITESPACE, "%20");

        return pathStub + filename;
    }

    /**
     * Encode a multi language string based on a map<Language, Content>
     *
     * @param map
     * @return
     */
    public static String encodeMultiLanguage(Map<String, String> map) {

        StringBuffer result = new StringBuffer(StringUtils.EMPTY);
        boolean first = true;
        for (Entry<String, String> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append(StringUtils.LANG_SPLIT);
            }
            result.append(entry.getValue().trim());
            // also add language to String
            result.append(StringUtils.LANG_SPLIT);
            result.append(entry.getKey());
        }
        return result.toString();
    }

    /**
     * @param mappings
     * @return
     */
    public static String[] enumArrayToStringArray(Enum<?>[] enums) {
        String[] strings = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            strings[i] = enums[i].toString();
        }
        return strings;
    }

    /**
     * Thread safe method that can be used to format (Number) Objects to (Number) String's.
     *
     * @param object
     * @return
     */
    public static String format(Object object) {
        return THREAD_FORMATTERS.get().format(object);
    }

    public static String formatEnumString(String data, Enum<?> term, Object... args) {

        if (term == null) {
            TLogger.severe("Cannot get empty term.");
            return null;
        }

        String result;
        if (StringUtils.EMPTY.equals(data)) {
            result = "{" + term + "}";
            TLogger.warning(term + " is not in dictionary. Replaced by " + result + ".");
        } else {
            result = data;
        }

        // replace with args if available
        try {
            if (args != null && args.length > 0) {
                result = String.format(result, args);
            }
        } catch (Exception e) {
            TLogger.exception(e, "Bad formatting for: " + term);
        }
        return result;
    }

    private static String formatNumberToLocalCurrency(double amount, UnitSystem unitSystem, TCurrency currency, CurrencyOrder order) {

        StringBuffer money = new StringBuffer();
        money.append(currency.getCurrencyCharacter());
        money.append(StringUtils.WHITESPACE);
        money.append(order.getNumberWithPostFix(amount, unitSystem));

        return money.toString();
    }

    public static String getHTMLColorFontOpeningTag(TColor color) {

        return "<font color=\"" + color.toHTML() + "\">";
    }

    public static String humanReadableByteCount(long bytes, boolean siUnits) {
        int unit = siUnits ? 1000 : 1024;

        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (siUnits ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + StringUtils.EMPTY;

        Locale locale = siUnits ? Locale.GERMAN : Locale.US;

        return String.format(locale, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String implode(Object[] data) {
        return implode(data, ", ");
    }

    public static String implode(Object[] data, String delimiter) {
        String implodedString;
        if (data.length == 0) {
            implodedString = EMPTY;
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(data[0]);
            for (int i = 1; i < data.length; i++) {
                sb.append(delimiter);
                sb.append(data[i]);
            }
            implodedString = sb.toString();
        }
        return implodedString;
    }

    public static String increaseLenghtWithSpaces(String value, int lenght) {
        while (value.length() < lenght) {
            value += WHITESPACE;
        }
        return value;
    }

    /**
     * Trims all spaces in a given string.
     *
     * @param orginal
     * @return trimmed version
     */
    public static String internalTrim(String orginal) {

        // null check
        if (orginal == null) {
            return null;
        }
        // regex that splits on 1 or more spaces
        String[] splits = orginal.split("\\s+");
        StringBuffer result = new StringBuffer();
        for (String split : splits) {
            result.append(split);
            result.append(WHITESPACE);
        }
        return result.toString().trim();
    }

    public static boolean isAllUpperCase(String s) {
        return s.length() == countUpperCase(s);
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean lineIsBlockElement(String line) {
        return line.startsWith("<table>") || line.startsWith("<p>") || line.startsWith("<img");
    }

    /**
     * Make a lowercase String with underscores ast spaces.
     *
     * @param s The String to manipulate
     * @return The modified String
     */
    public static String lowerCaseWithUnderScores(String s) {

        s = s.toLowerCase();
        String[] data = s.split(WHITESPACE);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            result.append(data[i].trim());
            // add under scorres, except the last one.
            if (i + 1 != data.length) {
                result.append(UNDER_SCORE);
            }
        }
        return result.toString();
    }

    /**
     * Thread safe method that can be used to parse String to Numbers.
     *
     * @param string
     * @return
     * @throws ParseException
     */
    public static Number parse(String string) throws ParseException {
        return THREAD_FORMATTERS.get().parse(string.trim());
    }

    /**
     * Generates a random string of given length
     *
     * @param name
     * @return
     */
    public static String randomString(int length) {

        SecureRandom random = new SecureRandom();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append(CHARACTERS[random.nextInt(CHARACTERS.length)]);
        }
        return buffer.toString();
    }

    /**
     * Generate random UUID Token as String
     * @return
     */
    public static String randomToken() {
        return UUID.randomUUID().toString();
    }

    public static String removeHTMLTags(String html) {
        return removeHTMLTags(html, false);
    }

    public static String removeHTMLTags(String html, boolean addLineBreaks) {
        if (!containsData(html)) {
            return EMPTY;
        }
        if (addLineBreaks) {
            html = html.replaceAll("<\\/p>", "\n");
            html = html.replaceAll("<\\/br>", "\n");
            html = html.replaceAll("&nbsp;", EMPTY);
        }
        return html.replaceAll("\\<[^>]*>", EMPTY).trim();
    }

    public static String removeNonDigits(String string) {
        String prefix = EMPTY;
        if (string.length() > 0 && '-' == string.charAt(0)) {
            prefix = "-";
        }
        return prefix + string.replaceAll("[^\\d.]", StringUtils.EMPTY);
    }

    public static String timeToString(Calendar cal) {
        if (cal == null) {
            return "NULL";
        }
        String format = String.format("%%0%dd", 2);
        String format2 = String.format("%%0%dd", 1);
        String seconds = String.format(format, cal.get(Calendar.SECOND));
        String minutes = String.format(format, cal.get(Calendar.MINUTE));
        String hours = String.format(format2, cal.get(Calendar.HOUR_OF_DAY));
        String timeString = hours + ":" + minutes + ":" + seconds;

        return timeString;
    }

    public static String toHTML(String text) {
        // Strip everything to be sure we start clean.
        text = text.replace("</p>", "</p>\n");
        text = text.replaceAll("<(/){0,1}(html|body|p)[^>]*>", WHITESPACE).trim();
        text = text.replace("\\n", "\n");
        text = text.replace("[b]", "<strong>");
        text = text.replace("[/b]", "</strong>");
        StringTokenizer lineSplitter = new StringTokenizer(text, "\n");
        String html = "<html><body>";
        while (lineSplitter.hasMoreTokens()) {
            String line = lineSplitter.nextToken();
            if (lineIsBlockElement(line)) {
                html += line;
            } else {
                html += toParagraph(line);
            }
        }
        html += "</body></html>";
        return html;
    }

    public static String toMoney(double currencyValue, UnitSystem unitSystem, TCurrency currency) {
        return toMoney(currencyValue, unitSystem, currency, CurrencyOrder.WHOLE_NUMBERS);
    }

    public static String toMoney(double currencyValue, UnitSystem unitSystem, TCurrency currency, CurrencyOrder significantOrder) {
        currencyValue = significantOrder.getNumberWithAdjustedNotation(currencyValue);
        return formatNumberToLocalCurrency(currencyValue, unitSystem, currency, significantOrder);
    }

    public static String toMoneyWithAdjustedOrder(double currencyValue, UnitSystem unitSystem, TCurrency currency) {
        CurrencyOrder significantOrder = CurrencyOrder.getSignificantOrder(currencyValue);
        return toMoney(currencyValue, unitSystem, currency, significantOrder);
    }

    public static String toMoneyWithAdjustedOrder(double currencyValue, UnitSystem unitSystem, TCurrency currency, double factor) {
        return toMoneyWithAdjustedOrder(currencyValue * factor, unitSystem, currency);
    }

    public static String toParagraph(String word) {
        return "<p>" + word + "</p>";
    }

    public static String toPercentage(double amount) {
        return Math.round((100f * amount)) + "%";
    }

    public static String toUpperCaseUnderscore(String text) {
        text = text.toUpperCase().trim();
        text = text.replace(WHITESPACE, "_");
        return text;
    }

    /**
     * Make a uppercase String with underscores ast spaces.
     *
     * @param s The String to manipulate
     * @return The modified String
     */
    public static String upperCaseWithUnderScores(String s) {

        s = s.toUpperCase();
        String[] data = s.split(WHITESPACE);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            result.append(data[i].trim());
            // add under scorres, except the last one.
            if (i + 1 != data.length) {
                result.append(UNDER_SCORE);
            }
        }
        return result.toString();
    }

    public static boolean validEmail(String name) {

        if (name == null) {
            return false;
        }
        /**
         * Valid chars and lenght
         */
        if (!name.trim().matches("[a-zA-Z0-9@._-]{6,50}")) {
            return false;
        }
        /**
         * only one @
         */
        if (name.trim().split("\\@").length != 2) {
            return false;
        }
        /**
         * At least one dot
         */
        return name.trim().split("\\.").length > 1;
    }

    public static boolean validFilename(String filename, String extension) {

        if (filename == null) {
            return false;
        }
        /**
         * Trim
         */
        filename = filename.trim();

        /**
         * check chars
         */
        if (!filename.matches("[a-z0-9._-]{5,100}")) {
            return false;
        }

        /**
         * Check extension
         */
        String[] parts = filename.split("\\.");
        if (parts.length <= 1) {
            return false;
        }

        /**
         * Finally check extension
         */
        if (extension != null) {
            return parts[parts.length - 1].equalsIgnoreCase(extension);
        }

        /**
         * Extension should be at least 3 char
         */
        return parts[parts.length - 1].length() >= 3;
    }

    public static boolean validPhone(String number) {

        if (number == null) {
            return false;
        }

        if (number.trim().equals(PHONE_DONT_CALL)) {
            return true;
        } else if (number.trim().startsWith("+")) {
            return number.trim().matches("[0-9+() ]{11,50}");
        } else {
            return number.trim().matches("[0-9]{10,10}");
        }
    }

    public static boolean validValue(String name, int minLenght, int maxLenght, boolean requireNumbers) {
        return validValue(name, minLenght, maxLenght, requireNumbers, false);
    }

    /**
     * Value is valid when it does not contain strange signs and has minimal and maximal amount of characters
     *
     * @param name
     * @return
     */

    public static boolean validValue(String name, int minLenght, int maxLenght, boolean requireNumbers, boolean onlyCharAndNumbers) {
        if (name == null || maxLenght <= minLenght) {
            return false;
        }

        if (onlyCharAndNumbers) {
            return name.trim().matches("[a-zA-Z0-9 -]{" + minLenght + "," + maxLenght + "}");
        }

        boolean check = name.trim().matches("[a-zA-Z0-9@._-]{" + minLenght + "," + maxLenght + "}");
        if (requireNumbers) {
            return check && name.trim().matches(".*[0-9].*");
        }
        return check;
    }

    /**
     * Wrap the given html text with a font that colors the text in between.
     *
     * @param color
     * @param orginalHTML
     * @return
     */
    public static String wrapWithHTMLColorFont(TColor color, String orginalHTML) {

        String openTag = getHTMLColorFontOpeningTag(color);
        String closeTag = "</font>";
        return openTag + orginalHTML + closeTag;
    }

    public static void writeToFile(String string, String filename) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
            bw.write(string);
            bw.flush();
            bw.close();

        } catch (IOException e) {
            TLogger.exception(e, "Error writing to file: " + filename);
        }
    }
}
