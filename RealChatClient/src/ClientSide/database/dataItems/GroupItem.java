package ClientSide.database.dataItems;


import java.time.LocalDateTime;

/**
 * <h1>群组数据项</h1>
 * <p>
 * - gid: String<br>
 * - groupName: String 群名称<br>
 * - peopleNum: String 群组总人数<br>
 * - foundDatetime: LocalDatetime 群组建立时间<br>
 * </p>
 */
public class GroupItem implements ClientDataItem {
    public String gid = null;
    public String groupName = null;
    public String peopleNum = null;
    public LocalDateTime foundDatetime = null;
}
