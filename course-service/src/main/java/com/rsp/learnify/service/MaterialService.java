package com.rsp.learnify.service;

import java.io.IOException;
import java.io.InputStream;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

public class MaterialService {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    public String storeFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .metadata(new org.bson.Document("type", file.getContentType()));

            ObjectId fileId = gridFsTemplate.store(inputStream, file.getOriginalFilename(), options);
            return fileId.toString();
        }
    }

    public InputStream getFile(String fileId) {
        return gridFSBucket.openDownloadStream(new ObjectId(fileId));
    }
}
