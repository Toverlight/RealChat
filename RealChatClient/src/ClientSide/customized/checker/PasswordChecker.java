package ClientSide.customized.checker;

/**
 *  密码合法性检查器
 */
public class PasswordChecker implements ValidChecker{
    private final Checker checker;
    private int l;
    private int u;

    /**
     * 给定上下界初始化，上下界用于检测长度
     * @param lower 下界
     * @param upper 上界
     */
    public PasswordChecker(int lower, int upper) {
        checker = new Checker();
        l = lower;
        u = upper;
    }

    /**
     * 检查密码的合法性（是否含有数字、字母、下划线 且 不含其它（即非法）字符）
     * @param content 要检测的内容
     * @return 是否合法
     */
    @Override
    public boolean checkValid(String content) {
        boolean flag = false;
        if (!checker.hasOther(content) && checker.isLengthBetween(content, l, u)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 设置上下界
     * @param lower 下界
     * @param upper 上界
     */
    public void setBounds(int lower, int upper) {
        l = lower;
        u = upper;
    }
}
