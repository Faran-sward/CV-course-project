package palm.detector.palm_detector.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author raoji
 * @date 2023/12/19
 * @Description
 */
@Getter
@Setter
public class checkRequestDTO {
    private String id;
    private MultipartFile image;
}
