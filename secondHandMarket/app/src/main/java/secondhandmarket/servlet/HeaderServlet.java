package secondhandmarket.servlet;

import secondhandmarket.vo.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/header")
public class HeaderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("  <meta charset='UTF-8'>");
        out.println("  <title>중고 장터</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>중고 장터</h1>");
        User loginUser = (User) req.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            out.println("<a href='/auth/join'>회원가입</a>");
            out.println("<a href='/auth/login'>로그인</a>");
        } else {
            out.printf("<span>%s</span>\n", loginUser.getNickname());
            out.println("<a href='/auth/logout'>로그아웃</a>");
        }

        out.println("</body>");
        out.println("</html>");
    }
}
