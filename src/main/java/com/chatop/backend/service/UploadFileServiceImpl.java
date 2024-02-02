package com.chatop.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

// import static com.chatop.backend.service.RentalServiceImpl.logger;

@Service
public class UploadFileServiceImpl implements UploadFileService {
    private final Cloudinary cloudinary;
    private static final Logger logger = LoggerFactory.getLogger(UploadFileServiceImpl.class);

    public UploadFileServiceImpl() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
                "api_key", System.getenv("CLOUDINARY_API_KEY"),
                "api_secret", System.getenv("CLOUDINARY_API_SECRET")));
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Le fichier est vide ou manquant.");
        }

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of("public_id", UUID.randomUUID().toString()));
        return uploadResult.get("url").toString();
    }

}

