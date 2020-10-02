package controllers.login;

import java.io.IOException;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import models.Employee;
import models.Worktime;
import utils.DBUtil;

/**
 * Servlet implementation class LogoutServlet
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //ToDo 退勤時間を登録するロジックの追加

        HttpSession session = ((HttpServletRequest)request).getSession();

        Employee e = (Employee)session.getAttribute("login_employee");

        EntityManager em = DBUtil.createEntityManager();

        Worktime worktimes = null;

        // ToDo Worktimeテーブルのend_atにレコードを登録
        // 条件は以下の通り
        // 1.セッションスコープに保存された従業員（ログインユーザ）情報を取得 = Employee e = (Employee)session.getAttribute("login_employee")
        // 2.ログアウトを押下した日時から退勤時間の取得 = Worktime.end_at

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        try {
            worktimes = em.createNamedQuery("getStart_at", Worktime.class)
                    .setParameter("employee", e.getId())
                    .setParameter("start_at", currentTime)
                    .getSingleResult();
        } catch (NoResultException ex) {}

        if (worktimes != null) {

            worktimes.setEnd_at(currentTime);

            em.getTransaction().begin();
            em.getTransaction().commit();
        }


        em.close();
        request.getSession().removeAttribute("login_employee");
        request.getSession().setAttribute("flush", "ログアウトしました。");
        response.sendRedirect(request.getContextPath() + "/login");
    }

}
