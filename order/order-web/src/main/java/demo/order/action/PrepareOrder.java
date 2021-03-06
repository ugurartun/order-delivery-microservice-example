package demo.order.action;

import demo.domain.Action;
import demo.order.domain.Order;
import demo.order.domain.OrderModule;
import demo.order.domain.OrderService;
import demo.order.domain.OrderStatus;
import demo.order.event.OrderEvent;
import demo.order.event.OrderEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@Transactional
public class PrepareOrder extends Action<Order> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Order apply(Order order) {
        Assert.isTrue(order
                .getStatus() == OrderStatus.ORDER_ASSIGNED, "Order must be in an ORDER_ASSIGNED state");

        OrderService orderService = order.getModule(OrderModule.class).getDefaultService();

        order.setStatus(OrderStatus.ORDER_PREPARING);
        order = orderService.update(order);

        try {
            order.sendAsyncEvent(new OrderEvent(OrderEventType.ORDER_PREPARING, order));
        } catch (Exception ex) {
            log.error("Could not prepare order", ex);
            order.setStatus(OrderStatus.ORDER_ASSIGNED);
            order = orderService.update(order);
        }

        return order;
    }
}
