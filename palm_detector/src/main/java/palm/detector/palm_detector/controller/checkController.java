package palm.detector.palm_detector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import palm.detector.palm_detector.dto.checkRequestDTO;
import palm.detector.palm_detector.dto.checkResponseDTO;
import palm.detector.palm_detector.service.checkService;

/**
 * @author raoji
 * @date 2023/12/19
 * @Description
 */
@RestController
public class checkController {
    @Autowired
    private checkService checkService;
    @PostMapping("check")
    public ResponseEntity<checkResponseDTO> check(@RequestParam String id, @RequestParam MultipartFile image){
        checkRequestDTO requestDTO = new checkRequestDTO();
        requestDTO.setId(id);
        requestDTO.setImage(image);
        System.out.println(id);
        checkResponseDTO responseDTO = checkService.check(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
