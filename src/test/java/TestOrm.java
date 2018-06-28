import java.util.Arrays;
import java.util.List;

import com.fishqq.orm.sql.Template;
import com.fishqq.biz.schemas.Schemas;
import com.fishqq.biz.schemas.User;

import com.fishqq.orm.Record;
import com.fishqq.orm.Repo;
import com.fishqq.orm.jdbc.ConnectionPool;
import com.fishqq.orm.jdbc.JdbcDataSource;
import com.fishqq.orm.jdbc.ThreadLocalPool;
import org.junit.Test;

/**
 * @author 白路 bailu.zjj@alibaba-inc.com
 * @date 2018/6/26
 */
public class TestOrm {
    @Test
    public void testRepo() {
        String driverClass = org.postgresql.Driver.class.getCanonicalName();
        String url = "jdbc:postgresql://tatdataphin2.pg.rdstest.tbsite.net:3432/dataphin";
        String user = "dataphin";
        String pwd = "dataphin";
        int timeout = 3;

        JdbcDataSource jdbcDataSource = new JdbcDataSource(driverClass, url, user, pwd, timeout);

        ConnectionPool connectionPool = new ThreadLocalPool(jdbcDataSource);

        Repo repo = new Repo(connectionPool);

        String sql = sql();

        System.out.println(sql);

        List<Record> records = repo.queryToRecord(sql);

        records.stream().forEach(record -> System.out.println(record));

        System.out.println(records.size());
    }

    private String sql() {
        String template = "select <columns> "
            + "            from od_user \n"
            + "     where id = :userId\n"
            + "     <and source_user_id = :sourceUserId>\n"
            + "     <or id in (:userIds)>";

        Integer itemId = 1;
        List<Integer> userIds = Arrays.asList(1,2,3);

        Template sql = Template.create(template)
            .renderVar("columns", Schemas.get(User.class).columnsSql())
            .renderParam("userId", "1")
            .renderExpr("sourceUserId", "2")
            .renderExpr(userIds != null && !userIds.isEmpty(), "userIds", "1", "2", "3");

        return sql.toSql();
    }
}
