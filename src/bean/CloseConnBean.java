package bean;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Yohann on 2016/8/13.
 */
public class CloseConnBean {
    private ResultSet resultSet;
    private PreparedStatement pstmt;
    private Connection conn;

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public void setPstmt(PreparedStatement pstmt) {
        this.pstmt = pstmt;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public PreparedStatement getPstmt() {
        return pstmt;
    }

    public Connection getConn() {
        return conn;
    }
}
