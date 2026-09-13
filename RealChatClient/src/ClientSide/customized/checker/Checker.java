package ClientSide.customized.checker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检查器
 */
public class Checker {

    /**
     * 以给定正则表达式匹配串
     * @param regex 正则表达式
     * @param content 要检测的内容
     * @return 是否匹配
     */
    public final boolean matchContent(String regex, String content) {
        if (content == null) return false;
        boolean flag = false;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }

    // 判断一个字符串是否含有数字
    public final boolean hasDigit(String content) {
        return matchContent(".*[0-9].*", content);
    }

    // 判断一个字符串是否是纯数字
    public final boolean hasOnlyDigits(String content) {
        return matchContent("[0-9]+", content);
    }

    // 判断一个字符串是否含有字母
    public final boolean hasLetter(String content) {
        return matchContent(".*[a-zA-z].*", content);
    }

    // 判断一个字符串是否含是纯字母
    public final boolean hasOnlyLetters(String content) {
        return matchContent("[a-zA-z]+", content);
    }

    // 判断一个字符串是否含有中文
    public final boolean hasChinese(String content) {
        return matchContent(".*[\u4e00-\u9fa5].*", content);
    }

    // 判断一个字符串是否是纯中文
    public final boolean hasOnlyChinese(String content) {
        return matchContent("[\u4e00-\u9fa5]+", content);
    }

    // 判断一个字符串是否含有特殊字符
    public final boolean hasSpecialChar(String content) {
        return matchContent(
                ".*[[ _`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。，、？]\\n\\r\\t].*",
                content);
    }

    // 判断一个字符串是否含有下划线
    public final boolean hasUnderline(String content) {
        return matchContent(".*[_].*", content);
    }

    /**
     * 判断一个字符串是否含有除数字、字母、下划线外的其他字符。可覆写，以匹配自定义的情况。
     * @param content 要检测的内容
     * @return 是否匹配
     */
    public boolean hasOther(String content) {
        return matchContent(".*[^0-9a-zA-Z_].*", content);
    }

    /**
     * 判断一个字符串是否含有除中文、数字、字母、下划线外的其他字符。
     * @param content 要检测的内容
     * @return 是否匹配
     */
    public final boolean hasOtherZhCN(String content) {
        return matchContent(".*[^0-9a-zA-Z_\u4e00-\u9fa5].*", content);
    }

    /**
     * 检查串长度是否 ∈ [a, b]
     * @param content 要检测的内容
     * @param a 下界
     * @param b 上界
     * @return 是否匹配
     */
    public boolean isLengthBetween(String content, int a, int b) {
        if (content == null) return false;
        boolean flag = false;
        if (a <= content.length() && content.length() <= b) {
            flag = true;
        }
        return flag;
    }
    /**
     * 检查串长度是否 = v
     * @param content 要检测的内容
     * @param v 长度值
     * @return 是否匹配
     */
    public boolean isLengthValued(String content, int v) {
        if (content == null) return false;
        boolean flag = false;
        if (content.length() == v) {
            flag = true;
        }
        return flag;
    }


}
