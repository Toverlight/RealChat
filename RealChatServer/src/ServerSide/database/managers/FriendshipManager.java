package ServerSide.database.managers;

import ServerSide.database.dataItems.FriendshipItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 好友关系数据管理器，用于处理friendship表相关操作
 */
public class FriendshipManager implements ServerDbManager {

    public static class DatabaseConnection {
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

        /**
         * 添加好友关系
         *
         * @param uid1 用户1的ID
         * @param uid2 用户2的ID
         * @return 是否添加成功，成功返回true，失败返回false
         */
        public static boolean insertFriendship(String uid1, String uid2) {
            String queryCheck = "SELECT COUNT(*) FROM friendship WHERE (uid1 =? AND uid2 =?) OR (uid1 =? AND uid2 =?)";
            String queryInsert = "INSERT INTO friendship (uid1, uid2) VALUES (?,?)";
            boolean flag = false;

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statementCheck = connection.prepareStatement(queryCheck);
                 PreparedStatement statementInsert = connection.prepareStatement(queryInsert)) {

                // 设置查询语句的参数，检查两种可能的好友关系组合是否已存在
                statementCheck.setString(1, uid1);
                statementCheck.setString(2, uid2);
                statementCheck.setString(3, uid2);
                statementCheck.setString(4, uid1);
                try (ResultSet resultSet = statementCheck.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        if (count == 0) {
                            // 如果不存在，则执行插入操作
                            statementInsert.setString(1, uid1);
                            statementInsert.setString(2, uid2);
                            int rowsAffected = statementInsert.executeUpdate();
                            flag = (rowsAffected > 0);
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return flag;
        }

        /**
         * 删除好友关系
         *
         * @param uid1 用户1的ID
         * @param uid2 用户2的ID
         * @return 是否删除成功，成功返回true，失败返回false
         */
        public static boolean deleteFriendship(String uid1, String uid2) {
            boolean flag = false;
            String query = "DELETE FROM friendship WHERE (uid1 =? AND uid2 =?) OR (uid1 =? AND uid2 =?)";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, uid1);
                statement.setString(2, uid2);
                statement.setString(3, uid2);
                statement.setString(4, uid1);
                int rowsAffected = statement.executeUpdate();
                flag = (rowsAffected > 0);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return flag;
        }

        /**
         * 获取用户的所有好友关系记录
         *
         * @param uid 用户ID
         * @return 该用户的所有好友关系记录对应的FriendshipItem对象列表（可能为空列表）
         */
        public static List<FriendshipItem> getFriendshipsByUid(String uid) {
            List<FriendshipItem> resultList = new ArrayList<>();
            String query = "SELECT uid1, uid2, fDatetime FROM friendship WHERE uid1 =? OR uid2 =?";

            try (Connection connection = DatabaseConnection.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, Integer.parseInt(uid));
                statement.setInt(2, Integer.parseInt(uid));
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    FriendshipItem item = new FriendshipItem();
                    item.uid1 = resultSet.getString("uid1");
                    item.uid2 = resultSet.getString("uid2");
                    item.fDatetime = resultSet.getTimestamp("fDatetime").toLocalDateTime();
                    resultList.add(item);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return resultList;
        }

//        public static void main(String[] args) {
//            String testUid1 = "100002";
//            String testUid2 = "100003";
//
//            // 测试插入好友关系
//            boolean insertResult = insertFriendship(testUid1, testUid2);
//            System.out.println("插入好友关系结果: " + insertResult);
//
//            // 测试删除好友关系
//            boolean deleteResult = deleteFriendship(testUid1, testUid2);
//            System.out.println("删除好友关系结果: " + deleteResult);
//
//            // 测试根据用户ID获取好友关系记录
//            List<FriendshipItem> getByUidResult = getFriendshipsByUid(testUid1);
//            for (FriendshipItem item : getByUidResult) {
//                System.out.println("用户1 ID: " + item.uid1 + ", 用户2 ID: " + item.uid2 + ", 好友关系建立时间: " + item.fDatetime);
//            }
//        }
    }
}