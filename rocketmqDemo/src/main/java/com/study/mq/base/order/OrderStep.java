package com.study.mq.base.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderStep {
    private long orderId;
    private String desc;

    // 模拟订单数据
    public static List<OrderStep> buildOrders() {
        // 订单1000L与2000L
        ArrayList<OrderStep> orders = new ArrayList<>();

        OrderStep order = new OrderStep();
        order = new OrderStep(1039L, "创建");
        orders.add(order);

        order = new OrderStep(1039L, "付款");
        orders.add(order);

        order = new OrderStep(2065L, "创建");
        orders.add(order);

        order = new OrderStep(1039L, "推送");
        orders.add(order);

        order = new OrderStep(2065L, "付款");
        orders.add(order);

        order = new OrderStep(2065L, "推送");
        orders.add(order);

        order = new OrderStep(1039L, "完成");
        orders.add(order);

        order = new OrderStep(2065L, "完成");
        orders.add(order);

        return orders;
    }
}

