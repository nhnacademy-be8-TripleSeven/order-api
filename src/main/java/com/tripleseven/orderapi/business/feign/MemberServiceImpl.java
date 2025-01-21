package com.tripleseven.orderapi.business.feign;

import com.tripleseven.orderapi.client.MemberApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberApiClient memberApiClient;

    @Override
    public void clearCart(Long userId, String guestId, List<Long> bookIds) {
        bookIds.stream()
                .forEach(bookId -> memberApiClient.deleteCart(userId, guestId, bookId));
    }

    @Override
    public Long getGradePoint(Long userId, long totalAmount){
        return memberApiClient.getGradePoint(userId, totalAmount);
    }
}
