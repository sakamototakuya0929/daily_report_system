package actions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.FavoritesView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import services.FavoriteService;
import services.ReportService;
/**
 * 日報に関する処理を行うActionクラス
 *
 */
public class ReportAction extends ActionBase {

    private ReportService service;
    private FavoriteService favservice;


    /**
     * メソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException {
        System.out.println("process");
        service = new ReportService();

        //メソッドを実行
        invoke();
        service.close();
    }

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException {
        System.out.println("表示index");
        //指定されたページ数の一覧画面に表示する日報データを取得
        int page = getPage();
        List<ReportView> reports = service.getAllPerPage(page);

        //全日報データの件数を取得
        long reportsCount = service.countAll();

        putRequestScope(AttributeConst.REPORTS, reports); //取得した日報データ
        putRequestScope(AttributeConst.REP_COUNT, reportsCount); //全ての日報データの件数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

        //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_REP_INDEX);
    }


//     ＊新規登録ページ
    public void entryNew() throws ServletException, IOException {
        System.out.println("表示entryNew");
        putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン

        //日報情報の空インスタンスに、日報の日付＝今日の日付を設定する
        ReportView rv = new ReportView();
        rv.setReportDate(LocalDate.now());
        putRequestScope(AttributeConst.REPORT, rv); //日付のみ設定済みの日報インスタンス

        //新規登録画面を表示
        forward(ForwardConst.FW_REP_NEW);
    }
    public void create() throws ServletException, IOException {
        System.out.println("表示create");
        //CSRF対策 tokenのチェック
        if (checkToken()) {

            //日報の日付が入力されていなければ、今日の日付を設定
            LocalDate day = null;
            if (getRequestParam(AttributeConst.REP_DATE) == null
                    || getRequestParam(AttributeConst.REP_DATE).equals("")) {
                day = LocalDate.now();
            } else {
                day = LocalDate.parse(getRequestParam(AttributeConst.REP_DATE));
            }

            //セッションからログイン中の従業員情報を取得
            EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

            //パラメータの値をもとに日報情報のインスタンスを作成する
            ReportView rv = new ReportView(
                    null,
                    ev, //ログインしている従業員を、日報作成者として登録する
                    day,
                    getRequestParam(AttributeConst.REP_TITLE),
                    getRequestParam(AttributeConst.REP_CONTENT),
                    null,
                    null);

            //日報情報登録
            List<String> errors = service.create(rv);

            if (errors.size() > 0) {
                //登録中にエラーがあった場合

                putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                putRequestScope(AttributeConst.REPORT, rv);//入力された日報情報
                putRequestScope(AttributeConst.ERR, errors);//エラーのリスト

                //新規登録画面を再表示
                forward(ForwardConst.FW_REP_NEW);

            } else {
                //登録中にエラーがなかった場合

                //セッションに登録完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
            }
        }

    }
    /**
     * 詳細画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void show() throws ServletException, IOException {
        System.out.println("表示show");
        //idを条件に日報データを取得する
        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

        // FavoriteServiceクラスのインスタンス生成
        FavoriteService fs = new FavoriteService();
        // FavoriteServiceクラスに定義した getFavCountメソッドを使って、今注目している日報にいいねされた数を求める
        long fav_count = fs.getFavCount(rv);



        if (rv == null) {
            //該当の日報データが存在しない場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);

        } else {
            putRequestScope(AttributeConst.TOKEN, getTokenId());
            putRequestScope(AttributeConst.REPORT, rv); //取得した日報データ
            putRequestScope(AttributeConst.FAV_COUNT, fav_count); //取得したいいね数データ

            //詳細画面を表示
            forward(ForwardConst.FW_REP_SHOW);
        }
    }
    /**
     * 編集画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void edit() throws ServletException, IOException {
        System.out.println("表示edit");
        //idを条件に日報データを取得する
        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

        //セッションからログイン中の従業員情報を取得
        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        if (rv == null || ev.getId() != rv.getEmployee().getId()) {
            //該当の日報データが存在しない、または
            //ログインしている従業員が日報の作成者でない場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);

        } else {

            putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
            putRequestScope(AttributeConst.REPORT, rv); //取得した日報データ

            //編集画面を表示
            forward(ForwardConst.FW_REP_EDIT);
        }

    }
    /**
     * 更新を行う
     * @throws ServletException
     * @throws IOException
     */
    public void update() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {
            System.out.println("表示update");
            //idを条件に日報データを取得する
            ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

            //入力された日報内容を設定する
            rv.setReportDate(toLocalDate(getRequestParam(AttributeConst.REP_DATE)));
            rv.setTitle(getRequestParam(AttributeConst.REP_TITLE));
            rv.setContent(getRequestParam(AttributeConst.REP_CONTENT));

            //日報データを更新する
            List<String> errors = service.update(rv);

            if (errors.size() > 0) {
                //更新中にエラーが発生した場合

                putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                putRequestScope(AttributeConst.REPORT, rv); //入力された日報情報
                putRequestScope(AttributeConst.ERR, errors); //エラーのリスト

                //編集画面を再表示
                forward(ForwardConst.FW_REP_EDIT);
            } else {
                //更新中にエラーがなかった場合

                //セッションに更新完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
            }
        }
     }
//            いいね機能追加
            public void favorite() throws ServletException, IOException{

                //CSRF対策 tokenのチェック
                if (checkToken()) {
//                    Integer id=getSessionScope(AttributeConst.FAV_ID);
                    EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

//                    System.out.println("id="+id);
//                  //セッションからログイン中の従業員情報を取得
//                  EmployeeView employee_id =  getSessionScope(AttributeConst.LOGIN_EMP);
//                  System.out.println("employee_id="+employee_id);
//                  日報ID取得
//                  Report report_id= getSessionScope(AttributeConst.REPORT);
//                  System.out.println("レポートid="+report_id);

                  //パラメータの値をもとに日報情報のインスタンスを作成する
                  FavoritesView fa= new FavoritesView(
                          null,
                          ev, //ログインしている従業員を、いいね作成者として登録する
                          getSessionScope(AttributeConst.REP_DATE)
                          );
                  System.out.println("favoriteメソッド起動");
                    //いいねデータを更新する

                    //日報情報登録
                    List<String> errors = favservice.Favoritecreate(fa);

                    if (errors.size() > 0) {
                        //登録中にエラーがあった場合

                        putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                        putRequestScope(AttributeConst.FAVORT, fa);//入力されたいいね情報
                        putRequestScope(AttributeConst.ERR, errors);//エラーのリスト

                        //新規登録画面を再表示
                        forward(ForwardConst.FW_REP_SHOW);

                    } else {
                        //登録中にエラーがなかった場合

                        //セッションに登録完了のフラッシュメッセージを設定(いいね押しました)
                        putSessionScope(AttributeConst.FLUSH, MessageConst.F_FAVORITE.getMessage());

                        //一覧画面にリダイレクト
                        redirect(ForwardConst.ACT_REP, ForwardConst.CMD_FAVORITE);
                    }

                }
        }

}