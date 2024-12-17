package com.example.orderapi.repository.pay;

import com.example.orderapi.entity.pay.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRepository extends JpaRepository<Pay, Long> {

}
