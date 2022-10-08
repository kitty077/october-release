package Flipkart.injection.jndi.datasource;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "JNDIServlet", urlPatterns = {"/JNDIServlet"})
public class JNDIServlet extends HttpServlet {
    @Resource(name = "MySQLDataSource")
    private DataSource pool;

    @Override
    public void init(ServletConfig config) throws ServletException {
        if (pool == null) {
            throw new ServletException("Unknown DataSource 'MySQLDataSource'");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (
                Connection con = pool.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select * from task");
        ) {

            response.setContentType("text/html");
            final Writer writer = response.getWriter();
            writer.append("<html>");
            writer.append("<body>");
            writer.append("<h1>Task details</h1>");
            writer.append("<table border=\"1\" cellspacing=10 cellpadding=5>");
            writer.append("<th>ID</th>");
            writer.append("<th>Name</th>");
            writer.append("<th>Description</th>");
            writer.append("<th>Status</th>");
            writer.append("<th>Created date</th>");
            writer.append("<th>Last modified date</th>");
            while (rs.next()) {
                writer.append("<tr>");
                writer.append("<td>").append(rs.getString("id")).append("</td>");
                writer.append("<td>").append(rs.getString("name")).append("</td>");
                writer.append("<td>").append(rs.getString("description")).append("</td>");
                writer.append("<td>").append(rs.getString("status")).append("</td>");
                writer.append("<td>").append(rs.getString("created_date")).append("</td>");
                writer.append("<td>").append(rs.getString("last_modified_date")).append("</td>");
                writer.append("</tr>");
            }
            writer.append("</body>");
            writer.append("</html>");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        doGet(request, response);
    }

}
