package services;

import java.util.List;

import actions.views.FavoriteConverter;
import actions.views.FavoritesView;
import actions.views.ReportConverter;
import actions.views.ReportView;
import constants.JpaConst;
import models.Favorite;

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
     * いいねテーブルのデータの件数を取得し、返却する
     * @return データの件数
     */
    public long countAll() {
        long favorites_count = (long) em.createNamedQuery(JpaConst.Q_FAV_COUNT, Long.class)
                .getSingleResult();
        return favorites_count;
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
            createFavorite(fa);
            return Favoritecreate(fa);
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

//
////いいねテーブルに追加するQuery
//    public long addFavorite(FavoritesView fa){
//        long count = (long) em.createNamedQuery(JpaConst.Q_FAVORITE_ADD_DEF, Long.class)
//                .setParameter(JpaConst.JPQL_PARM_FAVORITE, FavoriteConverter.toModel(fa))
//                .getSingleResult();
//
//        return count;
//    }

//    どの日報かという情報を与えないと いいね数なんかも求めることはできないので、引数にReportViewインスタンスを渡す
    public long getFavCount(ReportView rv){
        long count = (long) em.createNamedQuery(JpaConst.Q_FAV_COUNT_REGISTERED_FOR_EACH_REPORT, Long.class)
                .setParameter(JpaConst.JPQL_PARM_REPORT, ReportConverter.toModel(rv))
                .getSingleResult();

        return count;
    }
}