package kakaotechbootcamp.communityservice.controller;
import jakarta.servlet.http.HttpServletRequest;
import kakaotechbootcamp.communityservice.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/presigned-upload")
    public ImageService.UploadUrlResponse createPresignedUploadUrl(
            @RequestParam String kind,
            @RequestParam String filename,
            @RequestParam(required = false) String contentType,
            HttpServletRequest request
    ) {
        // 너가 JWT 필터/인터셉터에서 넣어준 userId 사용 (기존 코드랑 맞춰서)
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        return imageService.createUploadUrl(kind, filename, contentType, userId);
    }
}
