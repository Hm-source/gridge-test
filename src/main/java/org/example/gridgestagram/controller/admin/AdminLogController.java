package org.example.gridgestagram.controller.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.admin.dto.AdminLogResponse;
import org.example.gridgestagram.controller.admin.dto.AdminLogSearchCondition;
import org.example.gridgestagram.repository.log.entity.vo.LogType;
import org.example.gridgestagram.service.domain.AdminLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
@Slf4j
public class AdminLogController {

    private final AdminLogService adminLogService;

    @GetMapping
    public ResponseEntity<Page<AdminLogResponse>> searchLogs(
        @ModelAttribute AdminLogSearchCondition condition,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AdminLogResponse> result = adminLogService.searchLogs(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = Arrays.stream(LogType.values())
            .map(LogType::getCategory)
            .distinct()
            .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/types")
    public ResponseEntity<Map<String, List<LogType>>> getLogTypesByCategory() {
        Map<String, List<LogType>> typesByCategory = Arrays.stream(LogType.values())
            .collect(Collectors.groupingBy(LogType::getCategory));
        return ResponseEntity.ok(typesByCategory);
    }
}
