package demo.order.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import demo.domain.AbstractEntity;
import demo.domain.Aggregate;
import demo.domain.Command;
import demo.domain.Module;
import demo.order.action.*;
import demo.order.controller.OrderController;
import demo.order.event.OrderEvent;
import org.springframework.hateoas.Link;

import javax.persistence.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Entity(name = "orders")
public class Order extends AbstractEntity<OrderEvent, Long> {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    private String orderLocationLat;
    private String orderLocationLon;

    private Long accountId, restaurantId;

    public Order() {
        this.status = OrderStatus.ORDER_CREATED;
    }

    public Order(Long accountId) {
        this();
        this.accountId = accountId;
    }

    @JsonProperty("orderId")
    @Override
    public Long getIdentity() {
        return this.id;
    }

    @Override
    public void setIdentity(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getOrderLocationLat() {
        return orderLocationLat;
    }

    public void setOrderLocationLat(String orderLocationLat) {
        this.orderLocationLat = orderLocationLat;
    }

    public String getOrderLocationLon() {
        return orderLocationLon;
    }

    public void setOrderLocationLon(String orderLocationLon) {
        this.orderLocationLon = orderLocationLon;
    }

    @Command(method = "assignOrder", controller = OrderController.class)
    public Order assignOrder(Long restaurantId) {
        return getAction(AssignOrder.class)
                .apply(this, restaurantId);
    }

    @Command(method = "updateOrderLocation", controller = OrderController.class)
    public Order updateOrderLocation(String lat, String lon) {
        return getAction(UpdateOrderLocation.class)
                .apply(this, lat, lon);
    }

    @Command(method = "prepareOrder", controller = OrderController.class)
    public Order prepareOrder() {
        return getAction(PrepareOrder.class)
                .apply(this);
    }

    @Command(method = "orderReady", controller = OrderController.class)
    public Order orderReady() {
        return getAction(OrderReady.class)
                .apply(this);
    }

    @Command(method = "orderPickedUp", controller = OrderController.class)
    public Order orderPickedUp() {
        return getAction(OrderPickedUp.class)
                .apply(this);
    }

    @Command(method = "deliverOrder", controller = OrderController.class)
    public Order deliverOrder() {
        return getAction(DeliverOrder.class)
                .apply(this);
    }

    @Command(method = "orderDelivered", controller = OrderController.class)
    public Order orderDelivered() {
        return getAction(OrderDelivered.class)
                .apply(this);
    }

    @Command(method = "updateOrderStatus", controller = OrderController.class)
    public Order updateOrderStatus(OrderStatus orderStatus) {
        return getAction(UpdateOrderStatus.class)
                .apply(this, orderStatus);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Module<A>, A extends Aggregate<OrderEvent, Long>> T getModule() throws
            IllegalArgumentException {
        OrderModule orderModule = getModule(OrderModule.class);
        return (T) orderModule;
    }

    /**
     * Returns the {@link Link} with a rel of {@link Link#REL_SELF}.
     */
    @Override
    public Link getId() {
        return linkTo(OrderController.class)
                .slash("orders")
                .slash(getIdentity())
                .withSelfRel();
    }
}
