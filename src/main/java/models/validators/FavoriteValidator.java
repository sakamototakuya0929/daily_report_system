package models.validators;

import java.util.ArrayList;
import java.util.List;

import actions.views.FavoritesView;
import models.Employee;
import models.Report;

/**
 * 日報インスタンスに設定されている値のバリデーションを行うクラス
 */
public class FavoriteValidator {

    /**
     * 日報インスタンスの各項目についてバリデーションを行う
     * @param rv 日報インスタンス
     * @return エラーのリスト
     */
    //いいね追加
    public static List<String> validate(FavoritesView fa) {
        List<String> errors = new ArrayList<String>();
        return errors;
    }
    //いいね削除
    public static List<String> validate(Employee e, Report r) {
        List<String> errors = new ArrayList<String>();
        return errors;
    }
}