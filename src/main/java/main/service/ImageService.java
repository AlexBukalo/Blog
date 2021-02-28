package main.service;

import main.service.Response.DefaultResponse;
import main.service.dto.Errors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

@Service
public class ImageService {

    public Object uploadImage(MultipartFile image, HttpServletRequest request) {
        DefaultResponse response = new DefaultResponse();
        Errors errors = new Errors();

        if (!image.isEmpty()) {
            if (!image.getContentType().substring(6).equals("jpeg") && !image.getContentType().substring(6).equals("png")) {
                errors.setImage("Поддерживаются изображения .jpeg и .png");
                response.setErrors(errors);
            } else if (image.getSize() > 2_097_152) {
                errors.setImage("Размер файла превышает допустимый размер");
                response.setErrors(errors);
            } else {
                try {
                    String file = image.getOriginalFilename().toLowerCase();
                    Random r = new Random();
                    String path = "/upload/" + (char)(r.nextInt(26) + 'a') + (char)(r.nextInt(26) + 'a') + "/" +
                            (char)(r.nextInt(26) + 'a') + (char)(r.nextInt(26) + 'a') + "/" +
                            (char)(r.nextInt(26) + 'a') + (char)(r.nextInt(26) + 'a') + "/";
                    String realPath = request.getServletContext().getRealPath(path);
                    if (!Files.exists(Path.of(realPath))) {
                        Files.createDirectories(Path.of(realPath));
                    }
                    File transferFile = new File(realPath + "/" + file);
                    byte[] bytes = image.getBytes();
                    BufferedOutputStream stream =
                            new BufferedOutputStream(new FileOutputStream(transferFile));
                    stream.write(bytes);
                    stream.close();
                    return path + file;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }
}
