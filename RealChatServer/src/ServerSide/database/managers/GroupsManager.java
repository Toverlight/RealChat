package ServerSide.database.managers;

import ServerSide.database.dataItems.GroupItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 群组数据管理器，用于处理groups表相关操作
 */
public class GroupsManager implements ServerDbManager {

    private static class DatabaseConnection {
        private static final String DATABASE_URL = "jdbc:sqlite:Server\\UserData.db";  // SQLite数据库路径

        static {
            try {
                // 加载SQLite驱动（对于SQLite 3.x，JDBC驱动是自动加载的，因此不需要显式调用Class.forName）
                Class.forName("org.sqlite.JDBC"); // 显式加载驱动，确保兼容性
                DriverManager.getConnection(DATABASE_URL); // 确保数据库文件已存在
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }

        /**
         * 获取数据库连接
         *
         * @return 数据库连接对象
         * @throws SQLException 数据库连接异常
         */
        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DATABASE_URL);
        }
    }

    // 记录下一个可用的gid值，初始化为10000
    private static int nextAvailableGid = 10000;

    // 查询数据库中已有的最大gid值，用于初始化nextAvailableGid
    private static void initNextAvailableGid() {
        String query = "SELECT MAX(gid) AS max_gid FROM groups";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int maxGidFromDb = resultSet.getInt("max_gid");
                nextAvailableGid = Math.max(nextAvailableGid, maxGidFromDb + 1);
            }
        } catch (SQLException e) {
            System.err.println("查询最大gid值时出现SQL异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 创建群组
     *
     * @param groupName 群组名称
     * @return 新创建群组对应的GroupItem对象（若创建失败则返回null）
     */
    public static GroupItem createGroup(String groupName) {
        String query = "INSERT INTO groups (gid, groupName, peopleNum) VALUES (?,?, 0)"; // 修改插入语句，包含gid字段
        GroupItem item = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // 设置gid值为下一个可用的gid
            statement.setInt(1, nextAvailableGid);
            statement.setString(2, groupName);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // 获取自动生成的gid值（此处虽然获取了，但我们使用自己设定的nextAvailableGid）
                        int gid = generatedKeys.getInt(1);
                        item = new GroupItem();
                        item.gid = String.valueOf(nextAvailableGid);
                        item.groupName = groupName;
                        item.peopleNum = 0;
                        item.foundDatetime = LocalDateTime.now();
                    }
                }
                // 更新下一个可用的gid值，使其自增
                nextAvailableGid++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return item;
    }

    /**
     * 删除群组
     *
     * @param gid 群组ID
     * @return 是否删除成功，成功返回true，失败返回false
     */
    public static boolean deleteGroup(int gid) {
        boolean flag = false;
        String query = "DELETE FROM groups WHERE gid =?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, gid);
            int rowsAffected = statement.executeUpdate();
            flag = (rowsAffected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 修改群组名称
     *
     * @param gid 群组ID
     * @param newGroupName 新群组名称
     * @return 是否修改成功，成功返回true，失败返回false
     */
    public static boolean modifyGroupName(int gid, String newGroupName) {
        boolean flag = false;
        String query = "UPDATE groups SET groupName =? WHERE gid =?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newGroupName);
            statement.setInt(2, gid);
            int rowsAffected = statement.executeUpdate();
            flag = (rowsAffected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * 获取群组信息
     *
     * @param gid 群组ID
     * @return 该群组对应的GroupItem对象（若不存在则返回null）
     */
    public static GroupItem getGroupInfo(int gid) {
        GroupItem item = null;
        String query = "SELECT gid, groupName, peopleNum, foundDatetime FROM groups WHERE gid =?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                item = new GroupItem();
                item.gid = resultSet.getString("gid");
                item.groupName = resultSet.getString("groupName");
                item.peopleNum = resultSet.getInt("peopleNum");
                item.foundDatetime = resultSet.getTimestamp("foundDatetime").toLocalDateTime();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return item;
    }

    /**
     * 获取所有群组信息
     *
     * @return 所有群组对应的GroupItem对象列表（可能为空列表）
     */
    public static List<GroupItem> getGroups() {
        List<GroupItem> resultList = new ArrayList<>();
        String query = "SELECT gid, groupName, peopleNum, foundDatetime FROM groups";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                GroupItem item = new GroupItem();
                item.gid = resultSet.getString("gid");
                item.groupName = resultSet.getString("groupName");
                item.peopleNum = resultSet.getInt("peopleNum");
                item.foundDatetime = resultSet.getTimestamp("foundDatetime").toLocalDateTime();
                resultList.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public static void main(String[] args) {
        // 初始化下一个可用的gid值
        initNextAvailableGid();

        String testGroupName = "测试群组";

        // 测试创建群组
        GroupItem createResult = createGroup(testGroupName);
        if (createResult!= null) {
            System.out.println("创建群组结果: 成功，群组ID: " + createResult.gid);
        } else {
            System.out.println("创建群组结果: 失败");
        }

        int testGid = createResult!= null? Integer.parseInt(createResult.gid) : 0; // 根据创建结果获取群组ID

        // 测试删除群组
        boolean deleteResult = deleteGroup(testGid);
        System.out.println("删除群组结果: " + deleteResult);

        // 测试修改群组名称
        boolean modifyResult = modifyGroupName(testGid, "修改后的群组名");
        System.out.println("修改群组名称结果: " + modifyResult);

        // 测试获取群组信息
        GroupItem getInfoResult = getGroupInfo(testGid);
        if (getInfoResult!= null) {
            System.out.println("获取群组信息结果: " + getInfoResult.groupName + "（ID：" + getInfoResult.gid + "）");
        } else {
            System.out.println("获取群组信息结果: 未找到对应群组信息");
        }

        // 测试获取所有群组信息
        List<GroupItem> getGroupsResult = getGroups();
        for (GroupItem groupItem : getGroupsResult) {
            System.out.println("群组名称: " + groupItem.groupName + "（ID：" + groupItem.gid + "），群组人数: " + groupItem.peopleNum + "，建立时间: " + groupItem.foundDatetime);
        }
    }
}

