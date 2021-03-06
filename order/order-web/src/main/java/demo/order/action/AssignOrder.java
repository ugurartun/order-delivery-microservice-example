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

/**
 * Connects an {@link Order} to an Account.
 *
 * @author Kenny Bastani
 */
@Service
@Transactional
public class AssignOrder extends Action<Order> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Order apply(Order order, Long restaurantId) {
        Assert.isTrue(order.getStatus() == OrderStatus.ORDER_CREATED, "Order must be in a created state");

        OrderService orderService = order.getModule(OrderModule.class).getDefaultService();

        // Connect the account
        order.setRestaurantId(restaurantId);
        order.setStatus(OrderStatus.ORDER_ASSIGNED);
        order = orderService.update(order);

        try {
            // Trigger the account connected event
            order.sendAsyncEvent(new OrderEvent(OrderEventType.ORDER_ASSIGNED, order));
        } catch (Exception ex) {
            log.error("Could not assign order to restaurant", ex);
            order.setStatus(OrderStatus.ORDER_CREATED);
            order = orderService.update(order);
        }

        return order;
    }

}
