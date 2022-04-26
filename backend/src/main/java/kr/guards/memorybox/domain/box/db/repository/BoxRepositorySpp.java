package kr.guards.memorybox.domain.box.db.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.guards.memorybox.domain.box.db.bean.BoxDetailBean;
import kr.guards.memorybox.domain.box.db.bean.BoxUserDetailBean;
import kr.guards.memorybox.domain.box.db.bean.OpenBoxReadyBean;
import kr.guards.memorybox.domain.box.db.entity.QBox;
import kr.guards.memorybox.domain.box.db.entity.QBoxUser;
import kr.guards.memorybox.domain.user.db.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;


@Repository
public class BoxRepositorySpp {

    private final JPAQueryFactory jpaQueryFactory;

    public BoxRepositorySpp(JPAQueryFactory jpaQueryFactory) {this.jpaQueryFactory = jpaQueryFactory;}

    QBox qBox = QBox.box;
    QBoxUser qBoxUser = QBoxUser.boxUser;
    QUser qUser = QUser.user;

    
    // 열린 함 조회
    public List<BoxDetailBean> findOpenBoxByUserSeq(Long userSeq) {
       return jpaQueryFactory.select(Projections.constructor(BoxDetailBean.class, qBox.boxSeq, qBox.boxName, qBox.boxDescription,
                       qBox.boxCreatedAt, qBox.boxOpenAt, qBox.boxLocLat, qBox.boxLocLng, qBox.boxLocAddress)).from(qBox)
               .leftJoin(qBoxUser).on(qBoxUser.boxSeq.eq(qBox.boxSeq))
               .leftJoin(qUser).on(qUser.userSeq.eq(qBoxUser.userSeq))
               .where(qBoxUser.userSeq.eq(userSeq).and(qBox.boxIsOpen.isTrue()))
               .groupBy(qBox.boxSeq)
               .fetch();
    }

    // 닫힌 함 조회
    public List<BoxDetailBean> findCloseBoxByUserSeq(Long userSeq) {
        return jpaQueryFactory.select(Projections.constructor(BoxDetailBean.class, qBox.boxSeq, qBox.boxName, qBox.boxDescription,
                        qBox.boxCreatedAt, qBox.boxOpenAt, qBox.boxLocLat, qBox.boxLocLng, qBox.boxLocAddress)).from(qBox)
                .leftJoin(qBoxUser).on(qBoxUser.boxSeq.eq(qBox.boxSeq))
                .leftJoin(qUser).on(qUser.userSeq.eq(qBoxUser.userSeq))
                .where(qBoxUser.userSeq.eq(userSeq).and(qBox.boxIsOpen.isFalse()))
                .groupBy(qBox.boxSeq)
                .fetch();
    }

    // 열린 함 개인 혹은 멤버 조회
    public List<BoxUserDetailBean> findOpenBoxUserByUserSeq(Long userSeq) {
        return jpaQueryFactory.select(Projections.constructor(BoxUserDetailBean.class, qBoxUser.boxSeq, qUser.userSeq, qUser.userProfileImage)).from(qBoxUser)
                .leftJoin(qBox).on(qBox.boxSeq.eq(qBoxUser.boxSeq))
                .leftJoin(qUser).on(qUser.userSeq.eq(qBoxUser.userSeq))
                .where(qBoxUser.boxSeq.in(select(qBoxUser.boxSeq).from(qBoxUser).where(qBoxUser.userSeq.eq(userSeq))).and(qBox.boxIsOpen.isTrue()))
                .fetch();
    }

    // 닫힌 함 개인 혹은 멤버 조회
    public List<BoxUserDetailBean> findCloseBoxUserByUserSeq(Long userSeq) {
        return jpaQueryFactory.select(Projections.constructor(BoxUserDetailBean.class, qBoxUser.boxSeq, qUser.userSeq, qUser.userProfileImage)).from(qBoxUser)
                .leftJoin(qBox).on(qBox.boxSeq.eq(qBoxUser.boxSeq))
                .leftJoin(qUser).on(qUser.userSeq.eq(qBoxUser.userSeq))
                .where(qBoxUser.boxSeq.in(select(qBoxUser.boxSeq).from(qBoxUser).where(qBoxUser.userSeq.eq(userSeq))).and(qBox.boxIsOpen.isFalse()))
                .fetch();
    }

    // 기억함 열기 대기 상태 조회
    public List<OpenBoxReadyBean> findOpenBoxReadyByBoxSeq(Long boxSeq) {
        return jpaQueryFactory.select(Projections.constructor(OpenBoxReadyBean.class, qBoxUser.boxUserSeq, qBoxUser.userSeq, qUser.userNickname, qBoxUser.boxUserIsCome)).from(qBoxUser)
                .leftJoin(qUser).on(qUser.userSeq.eq(qBoxUser.userSeq))
                .where(qBoxUser.boxSeq.eq(boxSeq))
                .fetch();
    }
}
