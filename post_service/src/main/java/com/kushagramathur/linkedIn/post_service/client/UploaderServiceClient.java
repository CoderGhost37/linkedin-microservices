package com.kushagramathur.linkedIn.post_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "uploader-service", path = "/uploads/file", url = "${UPLOADER_SERVICE_URI:}")
public interface UploaderServiceClient {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadFile(@RequestBody MultipartFile file);
}
