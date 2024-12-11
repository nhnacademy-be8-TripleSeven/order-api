package com.example.orderapi.service.impl;

import com.example.orderapi.entity.PointHistory.PointHistory;
import com.example.orderapi.repository.PointHistoryRepository;
import com.example.orderapi.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;


    @Override
    public Page<PointHistory> findByMemberId(Long memberId, Pageable pageable) {
        return pointHistoryRepository.findAllByMemberId(memberId, pageable);
    }

    @Override
    public Page<PointHistory> findAll(Pageable pageable) {
        return pointHistoryRepository.findAll(pageable);
    }

    @Override
    public void deleteByPointHistoryId(Long pointHistoryId) {
        pointHistoryRepository.deleteById(pointHistoryId);
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        pointHistoryRepository.deleteAllByMemberId(memberId);
    }

    @Override
    public PointHistory findByPointHistoryId(Long pointHistoryId) {
        return pointHistoryRepository.findById(pointHistoryId).orElse(null);
    }
}
