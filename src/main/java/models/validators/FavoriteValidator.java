//package models.validators;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import actions.views.FavoritesView;
//import constants.MessageConst;
//import models.Employee;
//import models.Report;
//
///**
// * 日報インスタンスに設定されている値のバリデーションを行うクラス
// */
//public class FavoriteValidator {
//
//    /**
//     * いいねインスタンスの各項目についてバリデーションを行う
//     * @param fa いいねインスタンス
//     * @return エラーのリスト
//     */
//    public static List<Employee> validate(FavoritesView fa) {
//        List<Employee> errors = new ArrayList<Employee>();
//        List<Report>contenterrors=new ArrayList<Report>();
//
//        Employee  validateEmployee=(Employee)(fa.getEmployee_id());
//        //いいねした従業員をチェック
//        Employee employeeError = validateEmployee;
//        if (!employeeError.equals("")) {
//            errors.add(employeeError);
//        }
//
//        //いいねした日報
//        String contentError = validateReport(fa.getReport_id());
//        if (!contentError.equals("")) {
////            contenterrors.add(contentError);
//        }
//
//        return errors;
//    }
//    /**
//     * 内容に入力値があるかをチェックし、入力値がなければエラーメッセージを返却
//     * @param content 内容
//     * @return エラーメッセージ
//     */
//    private static String validateReport(Report report) {
//        if (report == null || report.equals("")) {
//            return MessageConst.E_NOCONTENT.getMessage();
//        }
//
//        //入力値がある場合は空文字を返却
//        return "";
//    }
//}