package fit.iuh.backend.controller.response;

import fit.iuh.backend.common.OrderStatus;
import fit.iuh.backend.model.OrderDetail;
import fit.iuh.backend.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
