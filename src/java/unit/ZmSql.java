package unit;

import af.sql.AfSql;

public class ZmSql {
	
	//对sql转义防止注入
	public static String format(String sql, String... args) throws Exception {
		String[] querys = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			arg = AfSql.escape(arg);
			querys[i] = arg;
		}

		sql = String.format(sql, querys);
		return sql;

	}

}
