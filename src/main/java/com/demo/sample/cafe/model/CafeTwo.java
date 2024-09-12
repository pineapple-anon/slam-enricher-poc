package com.demo.sample.cafe.model;

import org.springframework.core.annotation.Order;
import org.springframework.integration.annotation.Gateway;

public interface CafeTwo {

    @Gateway(requestChannel="orders")
    void placeOrder(Order order);

}
