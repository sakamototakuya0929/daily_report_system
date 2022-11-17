package services;

import java.util.List;

import actions.views.FavoriteConverter;
import actions.views.FavoritesView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Employee;
import models.Favorite;
import models.Report;
import models.validators.FavoriteValidator;

/**
 * いいねテーブルの操作に関わる処理を行うクラス
 *
 */
public class FavoriteService extends ServiceBase {

    /**
     * 指定した従業員が作成した日報データを、指定されたページ数の一覧画面に表示する分取得しFavoriteViewのリストで返却する
     * @param employee 従業員
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト
     */
    public List<FavoritesView> getMinePerPage(FavoritesView favorites, int page) {

        List<Favorite> favorite = em.createNamedQuery(JpaConst.Q_REP_GET_ALL_MINE, Favorite.class)
                .setParameter(JpaConst.JPQL_PARM_FAVORITE, FavoriteConverter.toModel((FavoritesView) favorites))
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return FavoriteConverter.toViewList(favorite);
    }
    /**
     * 指定した従業員が作成した日報データの件数を取得し、返却する
     * @param favorite
     * @return いいねデータの件数
     */
    public long countAllMine(FavoritesView favorite) {

        long count = (long) em.createNamedQuery(JpaConst.Q_REP_COUNT_ALL_MINE, Long.class)
                .setParameter(JpaConst.JPQL_PARM_FAVORITE, FavoriteConverter.toModel(favorite))
                .getSingleResult();

        return count;
    }
    /**
     * 指定されたページ数の一覧画面に表示する日報データを取得し、ReportViewのリストで返却する
     * @param page ページ数
     * @return 一覧画面に表示するデータのリスト
     */
    public List<FavoritesView> getAllPerPage(int page) {

        List<Favorite> favorites = em.createNamedQuery(JpaConst.Q_REP_GET_ALL, Favorite.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE * (page - 1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return FavoriteConverter.toViewList(favorites);
    }

    /**
     * idを条件に取得したデータをFavoritestViewのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    public FavoritesView findOne(int id) {
        return FavoriteConverter.toView(findOneInternal(id));
    }


    /**
     * idを条件にデータを1件取得し、Favoriteのインスタンスで返却する
     * @param id
     * @return 取得データのインスタンス
     */
    private Favorite findOneInternal(int id) {
        Favorite f = em.find(Favorite.class, id);

        return f;
    }

    /**
     * 画面から入力されたいいね元にデータを1件作成し、いいねテーブルに登録する
     * @param fa いいねの登録
     */
    public List<String> Favoritecreate(FavoritesView fa) {
        List<String> errors = FavoriteValidator.validate(fa);
        if (errors.size() == 0) {
            createFavorite(fa);
        }
        return errors;
}
    /**
     * いいねデータを1件登録する
     * @param fa いいねデータ
     * @return 登録結果(成功:true 失敗:false)
     */
    private void createFavorite(FavoritesView fa) {

        em.getTransaction().begin();
        em.persist(FavoriteConverter.toModel(fa));
        em.getTransaction().commit();
    }

    /**
     * 画面から入力された日報の登録内容を元に、日報データを更新する
     * @param fa 日報の更新内容
     */
    public List<String> Favoriteupdate(FavoritesView fa) {
            updateFavorite(fa);
            return Favoriteupdate(fa);

        }


    /**
     * いいねされた日報更新する
     * @param fa 画面から入力されたいいねの登録
     */
    private void updateFavorite(FavoritesView fa) {

        em.getTransaction().begin();
        Favorite f = findOneInternal(fa.getId());
        FavoriteConverter.copyViewToModel(f, fa);
        em.getTransaction().commit();

    }
//いいねの表示するのにcountするメソッド
    public long getFavCount(ReportView rv){
        long count = (long) em.createNamedQuery(JpaConst.Q_FAV_COUNT_REGISTERED_FOR_EACH_REPORT, Long.class)
                .setParameter(JpaConst.JPQL_PARM_REPORT, ReportConverter.toModel(rv))
                .getSingleResult();

        return count;
    }
    /**
     * 画面から入力されたいいね元にデータを1件のいいねを削除する
     * @param fa いいねの削除
     */
    //いいね削除
    public List<String> Favoritedestroy(Employee e, Report r) {
        List<String> errors = FavoriteValidator.validate(e,r);
        if (errors.size() == 0) {
            destroy(e,r);
        }
        return errors;
}

    //いいね削除
    public void destroy(Employee e, Report r ){

        Favorite f = (Favorite)em.createNamedQuery(JpaConst.Q_FAV_CHECK_FAVORITE, Favorite.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, e)
                .setParameter(JpaConst.JPQL_PARM_REPORT, r)
                .getSingleResult();

        em.getTransaction().begin();
        em.remove(f);       // データ削除
        em.getTransaction().commit();
        em.close();
    }

//いいね有無判断メソッド

public Boolean checkFavorite(Employee e, Report r){
    Long count = em.createNamedQuery(JpaConst.Q_FAV_COUNT_CHECK_FAVORITE, Long.class)

            .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, e)
            .setParameter(JpaConst.JPQL_PARM_REPORT, r)
            .getSingleResult();

        // そんな情報がデータベースに存在しなければ
        if (count == 0) {
            return false ;
        } else {
            return true ;
        }
    }
}

