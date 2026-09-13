package ClientSide.customized.enums.protocol;

/**
 * FETCH 协议规定的数据标识
 */
public class DataId {
    /**
     * 无数据，服务器拒传
     */
    public static final int REFUSED = 0;
    /**
     * 坏的请求
     */
    public static final int BAD_REQ = 1;
    /**
     * 昵称
     */
    public static final int NICKNAME = 2;
    /**
     * 电子邮箱
     */
    public static final int EMAIL = 3;
    /**
     * UID
     */
    public static final int UID = 4;
    /**
     * 用户头像
     */
    public static final int AVATAR = 5;
    /**
     * 自己的好友
     */
    public static final int FRIENDS = 6;
    /**
     * 加入的群组
     */
    public static final int GROUPS = 7;
    /**
     * 对方的头像
     */
    public static final int OTHER_AVATAR = 8;

}
