//package someone.alcoholic.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import someone.alcoholic.service.AwsS3Service;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/s3")
//public class AmazonS3Controller {
//
//    private final AwsS3Service awsS3Service;
//
//    @PostMapping("/upload")
//    public String uploadFile(
//            @RequestParam("category") String category,
//            @RequestPart(value = "file") MultipartFile multipartFile) {
//        return awsS3Service.uploadFileV1(category, multipartFile);
//    }
//}
