package palm.detector.palm_detector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import palm.detector.palm_detector.dto.uploadRequestDTO;
import palm.detector.palm_detector.dto.uploadResponseDTO;
import palm.detector.palm_detector.service.uploadService;

@RestController
public class uploadController {
    @Autowired
    private uploadService uploadService;

    @PostMapping("upload")
    public ResponseEntity<uploadResponseDTO> upload(@RequestParam String id, @RequestParam MultipartFile image){
        uploadRequestDTO requestDTO = new uploadRequestDTO();
        requestDTO.setId(id);
        requestDTO.setImage(image);
        uploadResponseDTO responseDTO = uploadService.upload(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
