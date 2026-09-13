package ServerSide.database.managers;

import ServerSide.database.dataItems.AffiliationItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 归属关系数据管理器，用于处理affiliation表相关操作
 */
public class AffiliationManager implements ServerDbManager {

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
         * 添加归属关系（用户加入群组记录）
         *
         * @param gid 群组ID（整数类型，与表结构对应）
         * @param uid 用户ID（整数类型，与表结构对应）
         * @return 是否添加成功，成功返回true，失败返回false
         */
        public static boolean insertAffiliation(int gid, int uid) {
            String query = "INSERT INTO affiliation (gid, uid, joinDatetime) VALUES (?,?,?)"; // 明确列出所有字段，更严谨
            boolean flag = false;

            // 检查groups表中是否存在对应的gid
            if (!checkGroupExists(gid)) {
                System.err.println("要添加归属关系的群组不存在，gid: " + gid);
                return false;
            }

            // 检查user表中是否存在对应的uid
            if (!checkUserExists(uid)) {
                System.err.println("要添加归属关系的用户不存在，uid: " + uid);
                return false;
            }

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, gid);
                statement.setInt(2, uid);
                statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now())); // 设置加入时间为当前时间
                int rowsAffected = statement.executeUpdate();
                flag = (rowsAffected > 0);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return flag;
        }

        /**
         * 检查groups表中是否存在指定的gid
         *
         * @param gid 群组ID
         * @return 存在返回true，不存在返回false
         */
        public static boolean checkGroupExists(int gid) {
            String query = "SELECT COUNT(*) FROM groups WHERE gid =?";
            boolean exists = false;
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, gid);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    exists = count > 0;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return exists;
        }

        /**
         * 检查user表中是否存在指定的uid
         *
         * @param uid 用户ID
         * @return 存在返回true，不存在返回false
         */
        public static boolean checkUserExists(int uid) {
            String query = "SELECT COUNT(*) FROM users WHERE uid =?";
            boolean exists = false;
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, uid);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    exists = count > 0;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return exists;
        }

        /**
         * 根据群组ID和用户ID删除归属关系（用户退群记录）
         *
         * @param gid 群组ID（整数类型，与表结构对应）
         * @param uid 用户ID（整数类型，与表结构对应）
         * @return 是否删除成功，成功返回true，失败返回false
         */
        public static boolean deleteAffiliation(int gid, int uid) {
            boolean flag = false;
            String query = "DELETE FROM affiliation WHERE gid =? AND uid =?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, gid);
                statement.setInt(2, uid);
                int rowsAffected = statement.executeUpdate();
                flag = (rowsAffected > 0);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return flag;
        }

        /**
         * 根据群组ID获取该群组下所有归属关系记录（获取群成员列表记录）
         *
         * @param gid 群组ID（整数类型，与表结构对应）
         * @return 该群组下所有归属关系记录的列表（可能为空列表）
         */
        public static List<AffiliationItem> getAffiliationsByGid(int gid) {
            List<AffiliationItem> resultList = new ArrayList<>();
            String query = "SELECT gid, uid, joinDatetime FROM affiliation WHERE gid =?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    AffiliationItem item = new AffiliationItem();
                    item.gid = resultSet.getString("gid");
                    item.uid = resultSet.getString("uid");
                    item.joinDatetime = resultSet.getTimestamp("joinDatetime").toLocalDateTime();
                    resultList.add(item);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return resultList;
        }

        /**
         * 根据用户ID获取该用户所属的所有群组的归属关系记录（获取用户加入的所有群组记录）
         *
         * @param uid 用户ID（整数类型，与表结构对应）
         * @return 该用户所属的所有群组的归属关系记录的列表（可能为空列表）
         */
        public static List<AffiliationItem> getAffiliationsByUid(int uid) {
            List<AffiliationItem> resultList = new ArrayList<>();
            String query = "SELECT gid, uid, joinDatetime FROM affiliation WHERE uid =?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    AffiliationItem item = new AffiliationItem();
                    item.gid = resultSet.getString("gid");
                    item.uid = resultSet.getString("uid");
                    item.joinDatetime = resultSet.getTimestamp("joinDatetime").toLocalDateTime();
                    resultList.add(item);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return resultList;
        }

        public static void main(String[] args) {
            int testGid = 10001; // 修改为整数类型
            int testUid = 100001; // 修改为整数类型

            // 测试插入归属关系
            boolean insertResult = insertAffiliation(testGid, testUid);
            System.out.println("插入归属关系结果: " + insertResult);

            // 测试删除归属关系
            boolean deleteResult = deleteAffiliation(testGid, testUid);
            System.out.println("删除归属关系结果: " + deleteResult);

            // 测试根据群组ID获取归属关系记录
            List<AffiliationItem> getByGidResult = getAffiliationsByGid(testGid);
            System.out.println("根据群组ID获取归属关系记录结果（列表大小）: " + getByGidResult.size());
            for (AffiliationItem item : getByGidResult) {
                System.out.println("群组ID: " + item.gid + ", 用户ID: " + item.uid + ", 加入时间: " + item.joinDatetime);
            }

            // 测试根据用户ID获取归属关系记录
            List<AffiliationItem> getByUidResult = getAffiliationsByUid(testUid);
            System.out.println("根据用户ID获取归属关系记录结果（列表大小）: " + getByUidResult.size());
            for (AffiliationItem item : getByUidResult) {
                System.out.println("群组ID: " + item.gid + ", 用户ID: " + item.uid + ", 加入时间: " + item.joinDatetime);
            }
        }
    }
}