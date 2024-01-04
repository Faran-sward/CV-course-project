package palm.detector.palm_detector.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import palm.detector.palm_detector.dto.checkRequestDTO;
import palm.detector.palm_detector.dto.checkResponseDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author raoji
 * @date 2023/12/19
 * @Description
 */
@Service
public class checkService {
    private final String WORKING_DIR = "/root/palm_detector/palm_detector";
    private final String IMG_PATH = WORKING_DIR +  "/image";
    private final String TEMP_PATH = WORKING_DIR +  "/temp";
    
    private final String pythonScript = "/root/palm_detector/palm_model/myDeepMPV+.py";
    private final String condaPath = "/root/miniconda3/bin/conda";
    private final String envName = "base";

    public checkResponseDTO check(checkRequestDTO requestDTO){
        checkResponseDTO responseDTO = new checkResponseDTO();

        try {
            // Check if the image directory exists
            if (!Files.exists(Path.of(IMG_PATH))) {
                responseDTO.setMessage("data empty");
                return responseDTO;
            }

            // Check if the requested ID exists
            String fileName = StringUtils.hasText(requestDTO.getId()) ? requestDTO.getId() + ".png" : "default.png";
            Path imagePath = Path.of(IMG_PATH, fileName);

            if (!Files.exists(imagePath)) {
                responseDTO.setMessage("Information not found");
                return responseDTO;
            }

            // Save request image to temp directory
            Path directoryPath = Path.of(TEMP_PATH);

            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            Path tempImagePath = Path.of(TEMP_PATH, requestDTO.getId() + ".png");
            Files.copy(requestDTO.getImage().getInputStream(), tempImagePath);

            System.out.println(imagePath.toString());
            System.out.println(tempImagePath.toString());
//            // Execute the script with image paths as inputs
//            String command = SCRIPT_PATH + " " + imagePath.toString() + " " + tempImagePath.toString();
//            Process process = Runtime.getRuntime().exec(command);
//            int exitCode = process.waitFor();
//
//            // Read the script output
//            String scriptOutput = new String(process.getInputStream().readAllBytes());
//
//            responseDTO.setMessage(scriptOutput.trim()); // Trim to remove leading/trailing whitespace
            // 路径参数

            // 构建命令
            String[] command = {condaPath, "run", "-n", envName, "python", pythonScript, imagePath.toString(), tempImagePath.toString()};

            System.out.println(Arrays.toString(command));
            // 创建进程构建器
            ProcessBuilder pb = new ProcessBuilder(command);

            // 启动进程执行命令
            Process process = pb.start();

            int exitCode = process.waitFor();
            System.out.println("exitcode is :");
            System.out.println(exitCode);

            // Read the script output
            String scriptOutput = new String(process.getInputStream().readAllBytes());
//            String scriptOutput = new String(readInputStream(process.getInputStream()));
            System.out.println(scriptOutput);

            if(scriptOutput.contains("result1"))
                scriptOutput = "Palmprint Verification Success.";
            else if (scriptOutput.contains("result2"))
                scriptOutput = "Palmprint Verification Failed.";
            else if (scriptOutput.contains("result3"))
                scriptOutput = "yolo detect failed.";
            
            responseDTO.setMessage(scriptOutput.trim()); // Trim to remove leading/trailing whitespace

        } catch (IOException | InterruptedException e) {
            // Handle exceptions (e.g., file IO exception, script execution exception)
            responseDTO.setMessage("Error occurred during processing");
            return responseDTO;
        } finally {
            // Delete the temporary images
            deleteTempImages();
        }
        System.out.println(responseDTO.getMessage());
        return responseDTO;
    }

    // // 读取输入流的辅助方法
    // private static String readInputStream(InputStream inputStream) throws IOException {
    //     try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
    //         StringBuilder output = new StringBuilder();
    //         String line;
    //         while ((line = reader.readLine()) != null) {
    //             output.append(line).append("\n");
    //         }
    //         return output.toString();
    //     }
    // }

    private void deleteTempImages() {
        try {
            // Delete all files in the temp directory
            Files.walk(Path.of(TEMP_PATH))
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            // Delete the temp directory itself
            Files.deleteIfExists(Path.of(TEMP_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
