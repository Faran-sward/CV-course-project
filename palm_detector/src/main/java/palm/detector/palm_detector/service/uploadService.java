package palm.detector.palm_detector.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import palm.detector.palm_detector.dto.uploadRequestDTO;
import palm.detector.palm_detector.dto.uploadResponseDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author raoji
 * @date 2023/12/19
 * @Description
 */
@Service
public class uploadService {
    private String IMG_PATH = "./image";
    public uploadResponseDTO upload(uploadRequestDTO requestDTO){
        uploadResponseDTO responseDTO = new uploadResponseDTO();

        // check if there is a folder
        Path dirPath = Path.of(IMG_PATH);
        if(!Files.exists(dirPath)){
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                responseDTO.setSuccess(false);
//                throw new RuntimeException(e);
                return responseDTO;
            }
        }
        String fileName = StringUtils.hasText(requestDTO.getId())
                ? requestDTO.getId() + ".png" : "default.png";
        Path imgPath = Path.of(IMG_PATH, fileName);

        if(requestDTO.getImage().isEmpty()){
            responseDTO.setSuccess(false);
            System.out.println("failed");
            return responseDTO;
        }
        try {
            Files.copy(requestDTO.getImage().getInputStream(), imgPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            responseDTO.setSuccess(false);
//            throw new RuntimeException(e);
            return responseDTO;
        }
        responseDTO.setSuccess(true);
        System.out.println("success");
        return responseDTO;
    }
}
