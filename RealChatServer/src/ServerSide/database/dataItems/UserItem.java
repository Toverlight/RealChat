package ServerSide.database.dataItems;

import java.time.LocalDateTime;

/**
 * <h1>用户数据项</h1>
 * <p>
 * - uid: String<br>
 * - nickname: String 昵称<br>
 * - email: String 邮箱<br>
 * - password: String 密码<br>
 * - registerDatetime: LocalDateTime 注册日期与时间<br>
 * </p>
 */
public class UserItem implements ServerDataItem{
    public String uid = null;
    public String nickname = null;
    public String email = null;
    public String password = null;
    public LocalDateTime registerDatetime = null;
}
