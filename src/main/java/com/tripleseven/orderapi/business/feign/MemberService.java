package com.tripleseven.orderapi.business.feign;

import java.util.List;

public interface MemberService {
    void clearCart(Long userId, String guestId, List<Long> bookIds);

    Long getGradePoint(Long userId, long totalAmount);
}
