package controllers.login;

import java.io.IOException;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Worktime;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("_token", request.getSession().getId());
        request.setAttribute("hasError", false);
        if(request.getSession().getAttribute("flush") != null) {
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
        rd.forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     // 認証結果を格納する変数
        Boolean check_result = false;

        String code = request.getParameter("code");
        String plain_pass = request.getParameter("password");

        Employee e = null;


        if(code != null && !code.equals("") && plain_pass != null && !plain_pass.equals("")) {
            EntityManager em = DBUtil.createEntityManager();

            String password = EncryptUtil.getPasswordEncrypt(
                    plain_pass,
                    (String)this.getServletContext().getAttribute("salt")
                    );

            // 社員番号とパスワードが正しいかチェックする
            try {
                e = em.createNamedQuery("checkLoginCodeAndPassword", Employee.class)
                      .setParameter("code", code)
                      .setParameter("pass", password)
                      .getSingleResult();
            } catch(NoResultException ex) {}

            em.close();

            if(e != null) {
                check_result = true;
            }
        }

        if(!check_result) {
            // 認証できなかったらログイン画面に戻る
            request.setAttribute("_token", request.getSession().getId());
            request.setAttribute("hasError", true);
            request.setAttribute("code", code);

            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
            rd.forward(request, response);
        } else {
            // 認証できたらログイン状態にしてトップページへリダイレクト
            request.getSession().setAttribute("login_employee", e);

            //ToDo 出勤時間を登録するロジック追加
                EntityManager em = DBUtil.createEntityManager();

                Worktime w = new Worktime();
                Worktime worktimes = null;

              //ToDo Worktimeテーブルのend_atに退勤時間を登録
              // 条件は以下の通り
              // 1.ログインユーザーの情報(e.getId) = Worktime.employee_id
              // 2.ログインの日付 = Worktime.start_at(日時を日付に変換する必要あり)

                Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                try {
                    worktimes = em.createNamedQuery("getStart_at", Worktime.class)
                            .setParameter("employee", e.getId())
                            .setParameter("start_at", currentTime)
                            .getSingleResult();
                } catch (NoResultException ex) {}


                if(worktimes == null) {

                    w.setEmployee(e);

                    w.setStart_at(currentTime);

                    em.getTransaction().begin();
                    em.persist(w);
                    em.getTransaction().commit();
                }

                em.close();

            request.getSession().setAttribute("flush", "ログインしました。");
            response.sendRedirect(request.getContextPath() + "/");
        }
    }


}
