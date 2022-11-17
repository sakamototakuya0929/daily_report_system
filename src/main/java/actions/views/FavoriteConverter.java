package actions.views;

import java.util.ArrayList;
import java.util.List;

import models.Favorite;

/**
 * いいねデータのDTOモデル⇔Viewモデルの変換を行うクラス 書き換え完了
 * 「View」は、JSPなどの「Model」をユーザインタフェースに出力するのを担当するもの
 *
 */
public class FavoriteConverter {


    /**
     * ViewモデルのインスタンスからDTOモデルのインスタンスを作成する
     * @param rv FavoritesViewのインスタンス
     * @return Favoritesのインスタンス
     */
    public static Favorite toModel(FavoritesView fa) {
        return new Favorite(
                fa.getId(),
                EmployeeConverter.toModel(fa.getEmployee_id()),
                ReportConverter.toModel(fa.getReport()));
    }
    /**
     * DTOモデルのインスタンスからViewモデルのインスタンスを作成する
     * @param f Favoriteのインスタンス
     * @return FavoriteViewのインスタンス
     */
    public static FavoritesView toView(Favorite f) {

        if (f == null) {
            return null;
        }

        return new FavoritesView(
                f.getId(),
                EmployeeConverter.toView(f.getEmployee_id()),
                ReportConverter.toView(f.getReport_id()));
    }

    /**
     * DTOモデルのリストからViewモデルのリストを作成する
     * @param favorites DTOモデルのリスト
     * @return Viewモデルのリスト
     */
    public static List<FavoritesView> toViewList(List<Favorite> list) {
        List<FavoritesView> fav = new ArrayList<>();

        for (Favorite f : list) {
            fav.add(toView(f));
        }

        return fav;
    }

    /**
     * Viewモデルの全フィールドの内容をDTOモデルのフィールドにコピーする
     * @param f DTOモデル(コピー先)
     * @param fa Viewモデル(コピー元)
     */
    public static void copyViewToModel(Favorite f, FavoritesView fa) {
        f.setId(fa.getId());
        f.setEmployee_id(EmployeeConverter.toModel(fa.getEmployee_id()));
        f.setReport_id(ReportConverter.toModel(fa.getReport()));


    }

}