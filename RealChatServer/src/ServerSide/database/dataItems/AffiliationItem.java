package ServerSide.database.dataItems;

import java.time.LocalDateTime;

/**
 * <h1>归属关系数据项</h1>
 * <p>
 * - gid: String<br>
 * - uid: String<br>
 * - joinDatetime: LocalDatetime 入群时间<br>
 * </p>
 */
public class AffiliationItem implements ServerDataItem{
    public String gid = null;
    public String uid = null;
    public LocalDateTime joinDatetime = null;
}
