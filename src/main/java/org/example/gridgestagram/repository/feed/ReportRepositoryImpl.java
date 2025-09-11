package org.example.gridgestagram.repository.feed;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.gridgestagram.controller.admin.dto.ReportQueryDto;
import org.example.gridgestagram.repository.feed.entity.QComment;
import org.example.gridgestagram.repository.feed.entity.QFeed;
import org.example.gridgestagram.repository.feed.entity.QReport;
import org.example.gridgestagram.repository.feed.entity.vo.ReportStatus;
import org.example.gridgestagram.repository.feed.entity.vo.ReportType;
import org.example.gridgestagram.repository.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Page<ReportQueryDto> findReports(Pageable pageable,
        ReportStatus status,
        String reporterName,
        String writerName,
        LocalDate startDate,
        LocalDate endDate) {
        QReport report = QReport.report;
        QFeed feed = QFeed.feed;
        QComment comment = QComment.comment;
        QUser reporter = new QUser("reporter");
        QUser feedWriter = new QUser("feedWriter");
        QUser commentWriter = new QUser("commentWriter");

        BooleanBuilder whereClause = new BooleanBuilder();

        if (status != null) {
            whereClause.and(report.status.eq(status));
        }

        if (reporterName != null && !reporterName.isBlank()) {
            whereClause.and(report.reporter.name.containsIgnoreCase(reporterName));
        }

        if (writerName != null && !writerName.isBlank()) {
            whereClause.and(feed.user.name.containsIgnoreCase(writerName)
                .or(comment.user.name.containsIgnoreCase(writerName)));
        }

        if (startDate != null) {
            whereClause.and(report.createdAt.goe(LocalDateTime.of(startDate, LocalTime.MIN)));
        }
        if (endDate != null) {
            whereClause.and(report.createdAt.loe(LocalDateTime.of(endDate, LocalTime.MAX)));
        }

        JPQLQuery<ReportQueryDto> mainQuery = queryFactory
            .select(Projections.constructor(ReportQueryDto.class,
                report.id,
                report.type,
                report.targetId,
                new CaseBuilder()
                    .when(report.type.eq(ReportType.FEED))
                    .then(feed.content)
                    .when(report.type.eq(ReportType.COMMENT))
                    .then(comment.content)
                    .otherwise(""),
                report.reporter.name, // reporter name can be directly accessed
                new CaseBuilder()
                    .when(report.type.eq(ReportType.FEED))
                    .then(feed.user.name)
                    .when(report.type.eq(ReportType.COMMENT))
                    .then(comment.user.name)
                    .otherwise(""),
                report.reason,
                report.status,
                report.createdAt,
                report.processedAt
            ))
            .from(report)
            .leftJoin(reporter).on(report.reporter.id.eq(reporter.id))
            .leftJoin(feed).on(report.type.eq(ReportType.FEED).and(report.targetId.eq(feed.id)))
            .leftJoin(feedWriter).on(feed.user.id.eq(feedWriter.id))
            .leftJoin(comment)
            .on(report.type.eq(ReportType.COMMENT).and(report.targetId.eq(comment.id)))
            .leftJoin(commentWriter).on(comment.user.id.eq(commentWriter.id));

        mainQuery.where(whereClause);

        long total = mainQuery.fetch().size();

        List<ReportQueryDto> content = mainQuery
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(report.createdAt.desc())
            .fetch();

        return new PageImpl<>(content, pageable, total);
    }

}