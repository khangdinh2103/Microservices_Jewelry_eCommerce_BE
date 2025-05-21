package fit.iuh.backend.controller.response;

import java.time.LocalDateTime;

import fit.iuh.backend.common.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String userName;
    private String userEmail;
    private OrderStatus orderStatus;
    private LocalDateTime createAt;
    private String address;
    private OrderStatus status;

}
