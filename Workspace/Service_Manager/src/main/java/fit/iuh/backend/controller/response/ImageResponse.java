package fit.iuh.backend.controller.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private Long id;
    private String imageURL;
    private Boolean isThumbnail;
}
