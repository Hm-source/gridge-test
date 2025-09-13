package org.example.gridgestagram.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Slf4j
public class PaginationUtils {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private PaginationUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Pageable validateAndAdjust(Pageable pageable) {
        if (pageable == null) {
            return PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by("createdAt").descending());
        }

        int pageNumber = Math.max(0, pageable.getPageNumber());

        int pageSize = pageable.getPageSize();
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        } else if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
            log.warn("페이지 크기가 최대값({})을 초과하여 조정되었습니다. 요청값: {}", MAX_PAGE_SIZE,
                pageable.getPageSize());
        }

        Sort sort = pageable.getSort();
        if (sort.isUnsorted()) {
            sort = Sort.by("createdAt").descending();
        }

        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
