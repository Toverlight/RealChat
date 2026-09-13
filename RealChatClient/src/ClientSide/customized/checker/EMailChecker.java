package ClientSide.customized.checker;

public class EMailChecker implements ValidChecker{
    private Checker checker;

    public EMailChecker() {
        checker = new Checker();
    }
    /**
     * 匹配合法邮箱地址
     * @param content 要检测的内容
     * @return 是否匹配
     */
    @Override
    public boolean checkValid(String content) {
        return checker.matchContent("^[a-zA-Z0-9_]+(?:[\\.][a-zA-Z0-9_]+)*@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$",
                content);
    }
}
