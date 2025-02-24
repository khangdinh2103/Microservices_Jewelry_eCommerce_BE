package fit.iuh.backend.controller.request;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {
    private String url;
    private boolean thumbnail;

}
