package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import constants.JpaConst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * いいねデータのDTOモデル
 *
 */
//実際使うNameQuery
@Table(name = JpaConst.TABLE_FAV)
@NamedQueries({
    @NamedQuery(
            //  //showのフォームで表示させるために「いいね」の数をあらかじめ取得するクエリ
            name = JpaConst.Q_FAV_COUNT_REGISTERED_FOR_EACH_REPORT,
            query = JpaConst.Q_FAV_COUNT_REGISTERED_FOR_EACH_REPORT_DEF),
    @NamedQuery(
            name = JpaConst.Q_FAV_CHECK_FAVORITE,
            query = JpaConst.Q_FAV_CHECK_FAVORITE_DEF),
    @NamedQuery(
            name = JpaConst.Q_FAV_COUNT_CHECK_FAVORITE,
            query = JpaConst.Q_FAV_COUNT_CHECK_FAVORITE_DEF)
})
@Getter //全てのクラスフィールドについてgetterを自動生成する(Lombok)
@Setter //全てのクラスフィールドについてsetterを自動生成する(Lombok)
@NoArgsConstructor //引数なしコンストラクタを自動生成する(Lombok)
@AllArgsConstructor //全てのクラスフィールドを引数にもつ引数ありコンストラクタを自動生成する(Lombok)
@Entity
public class Favorite {

    /**
     * id
     */
    @Id
    @Column(name = JpaConst.FAV_COL_ID)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * いいねをした従業員
     */
    @ManyToOne
    @JoinColumn(name = JpaConst.FAV_COL_EMP, nullable = false)
    private Employee employee_id;

    /**
     * いいねされた日報
     */
    @ManyToOne
    @JoinColumn(name=JpaConst.FAV_COL_REP, nullable = false)
    private Report report_id;

}