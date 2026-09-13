package ServerSide.database.dataItems;

import java.time.LocalDateTime;

/**
 * <h1>好友关系数据项</h1>
 * <p>
 * - uid1: String<br>
 * - uid2: String<br>
 * - fDatetime: LocalDatetime 好友关系建立时间<br>
 * </p>
 */
public class FriendshipItem implements ServerDataItem{
    public String uid1 = null;
    public String uid2 = null;
    public LocalDateTime fDatetime = null;
}
