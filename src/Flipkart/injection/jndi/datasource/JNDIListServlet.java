package Flipkart.injection.jndi.datasource;

import javax.naming.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

@WebServlet(name = "JNDI", urlPatterns = {"/JNDI"})
public class JNDIListServlet extends HttpServlet {
    private final Date timeStart = new Date();
    private long timeLeft;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* https://www.tutorialspoint.com/servlets/servlets-auto-refresh.htm */
        // Set refresh, autoload time as 5 seconds
        resp.setIntHeader("Refresh", 5);

        resp.setContentType("text/html");
        final Writer writer = resp.getWriter();

        // Get current time
        getCurrentTime();

        writer.append("<html>");
        writer.append("<body>");

        getJNDIList().forEach(s -> {
            try {
                writer.append("<div>");
                writer.append("<p>");
                writer.append(s);
                writer.append("</p>");
                writer.append("</div>");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        writer.append("<h3><a href=\"http://localhost:8080/MOBILES/\"> Hello Servlet </a></h3>\n");

        writer.append("<p>Current Time is: " + getCurrentTime() + "</p>\n");

        writer.append("</body>");
        writer.append("</html>");

    }

    private String getCurrentTime() {
        Calendar calendar = new GregorianCalendar();
        String am_pm;
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        if (calendar.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else
            am_pm = "PM";

        return hour + ":" + minute + ":" + second + " " + am_pm;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private List<String> getJNDIList() {
        long endTime = 0;
        List<String> jndiList = new ArrayList<>();
        Context ctx = null;
        try {
            ctx = (Context) new InitialContext().lookup("java:comp/env");
        } catch (NamingException e) {
            jndiList.add(e.getMessage());
        }
        jndiList.addAll(getListContext(ctx, ""));
        return jndiList;
    }


    private List<String> getListContext(Context ctx, String indent) {
        List<String> jndiList = new ArrayList<>();
        try {
            NamingEnumeration<Binding> list = ctx.listBindings(indent);
            while (list.hasMore()) {

                try {
                    Binding next = list.next();
                    String className = next.getClassName();
                    String name = next.getName();
                    Object object = next.getObject();
                    jndiList.add(className + " = " + name);

//                if ("MySQLDataSource".equals(name) && object instanceof DataSource) {
                    if (object instanceof DataSource) {
                        DataSource ds = (DataSource) ctx.lookup("MySQLDataSource");
                        try (Connection connection = ds.getConnection()) {
                            boolean valid = connection.isValid(1000);
                            jndiList.add(" -  JDBC Connection is " + (valid ? "valid" : "NOT valid"));
                            DatabaseMetaData metaData = connection.getMetaData();
                            jndiList.add(" -  URL:  '" + metaData.getURL() + "'");
                            jndiList.add(" -  DatabaseProductName:  '" + metaData.getDatabaseProductName() + "'");
                            jndiList.add(" -  DriverName:  '" + metaData.getDriverName() + "'");
                            jndiList.add(" -  DriverVersion:  '" + metaData.getDriverVersion() + "'");
                            jndiList.add(" -  UserName:  '" + metaData.getUserName() + "'");
                            jndiList.add("<h3><a href=\"http://localhost:8080/MOBILES/JNDIServlet\"> JNDIServlet </a></h3>");
                            jndiList.add("<h3><a href=\"http://localhost:8088/?server=db&username=admin_db&db=MOBILES\"> Adminer </a></h3>");

                            if (valid) {
                                jndiList.add(String.format("Total load time: %dm %ds", timeLeft / 60, timeLeft % 60));
                            }
                        } catch (SQLException throwable) {
                            jndiList.add("MySQL is not ready. Please wait a few minutes... Try the command: 'docker ps' to check the health of the mysql container.");
                            jndiList.add(throwable.getMessage());

                            timeLeft = (Optional.of(new Date().getTime() - timeStart.getTime()).orElse(0L)) / 1000;
                            jndiList.add(String.format("Elapsed time: %dm %ds", timeLeft / 60, timeLeft % 60));
                        }
                    }
                    if (object instanceof javax.naming.Context) {

                        if (indent == null) {
                            indent = "";
                        }

                        jndiList.addAll(getListContext((Context) object, indent + "/"));
                    }
                } catch (NamingException e) {
                    jndiList.add(e.getMessage());
                }
            }
        } catch (NamingException e) {
            System.err.println("List error: " + e);
        }
        return jndiList;
    }
}
