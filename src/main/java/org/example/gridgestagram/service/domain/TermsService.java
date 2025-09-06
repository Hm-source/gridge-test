package org.example.gridgestagram.service.domain;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gridgestagram.controller.auth.dto.TermsResponse;
import org.example.gridgestagram.exceptions.CustomException;
import org.example.gridgestagram.exceptions.ErrorCode;
import org.example.gridgestagram.repository.term.TermsRepository;
import org.example.gridgestagram.repository.term.entity.Terms;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermsService {

    private final TermsRepository termsRepository;

    @Transactional(readOnly = true)
    public List<TermsResponse> getActiveTerms() {
        List<Terms> activeTerms = termsRepository.findAllByOrderByIsRequiredDescCreatedAtAsc();
        return activeTerms.stream()
            .map(TermsResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Terms> getRequiredTerms() {
        return termsRepository.findByIsRequired(true);
    }

    @Transactional(readOnly = true)
    public Terms getTermsById(Long id) {
        return termsRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.TERMS_NOT_FOUND));
    }

}