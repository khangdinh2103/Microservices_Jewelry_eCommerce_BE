package fit.iuh.backend.controller.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequest {
    private String url;
    @JsonProperty("isThumbnail")
    private boolean thumbnail;


}
