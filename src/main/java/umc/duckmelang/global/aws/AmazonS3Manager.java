package umc.duckmelang.global.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import umc.duckmelang.domain.uuid.domain.Uuid;
import umc.duckmelang.domain.uuid.repository.UuidRepository;
import umc.duckmelang.global.config.AmazonConfig;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    public String uploadFile(String keyName, MultipartFile file){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
        } catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String deleteFile(String keyName) {

        String result = "success";

        try {
            boolean isObjectExist = amazonS3.doesObjectExist(amazonConfig.getBucket(), keyName);
            if (isObjectExist) {
                amazonS3.deleteObject(amazonConfig.getBucket(), keyName);
            } else {
                result = "file not found";
            }
        } catch (Exception e) {
            log.debug("Delete File failed", e);
        }

        return result;
    }

    public String generateIdolCategoryKeyName(String uuid) {
        return amazonConfig.getIdolCategoryImagePath() + '/' + uuid;
    }

    public String generateMemberProfileImageKeyName(String uuid) {
        return amazonConfig.getMemberProfileImagePath() + '/' + uuid;
    }

    public String generatePostImageKeyName(String uuid) {
        return amazonConfig.getPostImagePath() + '/' + uuid;
    }

    public String generateChatMessageImageKeyName(String uuid) {
        return amazonConfig.getChatMessageImagePath() + '/' + uuid;
    }

    public String generateChatMessageFileKeyName(String uuid) {
        return amazonConfig.getChatMessageFilePath() + '/' + uuid;
    }


}