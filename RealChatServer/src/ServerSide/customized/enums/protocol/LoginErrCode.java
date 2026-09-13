package ServerSide.customized.enums.protocol;

/**
 * LOGIN 协议规定的登录错误码
 */
public class LoginErrCode {
    /**
     * 登录成功
     */
    public static final int OK = 0;
    /**
     * 损坏的请求
     */
    public static final int BAD_REQ = 1;
    /**
     * UID错误（查无此人）
     */
    public static final int UID_ERR = 2;
    /**
     * PWD错误
     */
    public static final int PWD_ERR = 3;
    /**
     * 用户已经登录
     */
    public static final int USER_HAS_LOGGED_IN = 4;
}
