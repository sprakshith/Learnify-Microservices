package com.rsp.learnify.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/materials")
public class MaterialController {

    // @Autowired
    // private MaterialService materialService;

    // @PostMapping("/upload")
    // public Material uploadFile(@RequestParam("file") MultipartFile file,
    // @RequestParam("type") Material.Type type)
    // throws IOException {
    // String fileId = materialService.storeFile(file);

    // Material material = Material.builder()
    // .type(type)
    // .fileName(file.getOriginalFilename())
    // .fileId(fileId)
    // .build();

    // // Save material metadata to MongoDB (assuming you have a repository for
    // // Material)
    // // materialRepository.save(material);

    // return material;
    // }

    // @GetMapping("/download/{fileId}")
    // public void downloadFile(@PathVariable String fileId, HttpServletResponse
    // response) throws IOException {
    // InputStream fileStream = materialService.getFile(fileId);
    // IOUtils.copy(fileStream, response.getOutputStream());
    // response.flushBuffer();
    // }
}