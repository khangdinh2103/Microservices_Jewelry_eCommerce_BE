package fit.iuh.backend.service;

import fit.iuh.backend.common.OrderStatus;
import fit.iuh.backend.controller.response.OrderPageResponse;

public interface OrderService {

    OrderPageResponse getAllOrders(int page, int size);
    Long updateOrder(Long orderId, OrderStatus newStock);

}
