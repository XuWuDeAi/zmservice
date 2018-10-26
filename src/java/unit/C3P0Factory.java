package unit;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import af.sql.AfSqlConnection;
import af.sql.c3p0.AfC3P0Pool;

/* 如果应用程序里只有一个数据源，可以仿照这个类，自己建一个C3P0Factory
 * 
 */

public class C3P0Factory {
	private static AfC3P0Pool pool = null;

	/* 全局对象初始化 */
	static {
		pool = new AfC3P0Pool();
	}

	/* 获取连接 */
	public static AfSqlConnection getConnection() throws Exception {
		return pool.getConnection();
	}

	// 查询，并自动映射到pojo，返回pojo列表
	public static List executeQuery(String sql, Class clazz) throws Exception {
		AfSqlConnection connection = getConnection();
		try {
			return connection.executeQuery(sql, clazz);
		} finally {
			connection.close();
		}
	}

	// 查询，并自动映射到pojo，返回pojo列表
	public static JSONArray executeQuery(String sql) throws Exception {
		AfSqlConnection connection = getConnection();
		try {

			return resultSetToJsonArry(connection.executeQuery(sql));

		} finally {
			connection.close();
		}
	}

	// 获取json
	public static JSONObject executeQueryGetOne(String sql) throws Exception {
		AfSqlConnection connection = getConnection();
		JSONObject val = new JSONObject();
		try {
			val = resultSetToJsonArry(connection.executeQuery(sql)).getJSONObject(0);

		} catch (Exception e) {
			System.out.print(e.getMessage());
			return val;
		} finally {
			connection.close();
		}
		return val;
	}

	// 获取唯一一行记录
	public static Object get(String sql, Class clazz) throws Exception {
		AfSqlConnection connection = getConnection();
		try {
			List rows = executeQuery(sql, clazz);
			if (rows == null || rows.size() == 0) {
				return null;
			} else {
				return rows.get(0);
			}
		} finally {
			connection.close();
		}
	}

	// 插入 insert
	public static void insert(Object pojo) throws Exception {
		AfSqlConnection connection = getConnection();
		try {
			connection.insert(pojo);
		} finally {
			connection.close();
		}
	}

	// 执行 insert update delete 等SQL
	public static void execute(String sql) throws Exception {
		AfSqlConnection connection = getConnection();
		try {
			connection.execute(sql);
		} finally {
			connection.close();
		}
	}

	public static JSONArray resultSetToJsonArry(ResultSet rs) throws SQLException, JSONException

	{

		// json数组

		JSONArray array = new JSONArray();

		// 获取列数

		ResultSetMetaData metaData = rs.getMetaData();

		int columnCount = metaData.getColumnCount();

		// 遍历ResultSet中的每条数据

		while (rs.next()) {

			JSONObject jsonObj = new JSONObject();

			// 遍历每一列

			for (int i = 1; i <= columnCount; i++) {

				String columnName = metaData.getColumnLabel(i);

				String value = rs.getString(columnName);

				jsonObj.put(columnName, value);

			}

			array.put(jsonObj);

		}
		return array;
	}
}
