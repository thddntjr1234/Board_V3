package com.ebstudy.board_v3.service;

import com.ebstudy.board_v3.dto.FileDTO;
import com.ebstudy.board_v3.repository.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private final BoardMapper boardMapper;

    @Value("${spring.servlet.multipart.location}")
    String basicPath;

    /**
     * 파일 리스트를 가져오는 메소드
     * @param postId 가져올 파일 리스트가 포함된 게시글의 id
     * @return 파일 리스트
     */
    public List<FileDTO> getFileList(Long postId) {
        List<FileDTO> fileList = boardMapper.getFileList(postId);
        return fileList;
    }

    /**
     * 파일의 논리정보를 DB에, 실제 파일을 서버의 파일 저장소 폴더에 저장
     * @param postId 저장할 파일의 부모 게시글 id
     * @param multipartFileList MultipartFile로 변환된 파일 리스트
     * @throws IOException transferTo 메소드에서 발생할 수 있는 예외
     */
    public void saveFile(Long postId, List<MultipartFile> multipartFileList) throws IOException {

        for (MultipartFile multipartFile : multipartFileList) {
            if (!(multipartFile.getSize() > 0)) {
                continue;
            }
            System.out.println();

            FileDTO file = new FileDTO();

            file.setPostId(postId);
            file.setFileName(UUID.randomUUID()+ "_" + multipartFile.getOriginalFilename());
            file.setFileRealName(multipartFile.getOriginalFilename());
            file.setExtension(multipartFile.getContentType());
            file.setSize(multipartFile.getSize());

            File fileName = new File(file.getFileName());
            multipartFile.transferTo(fileName);

            boardMapper.saveFile(file);
        }
    }

    /**
     * fileName을 주소에서 입력받아 실제 파일을 저장소에서 전달하는 메소드
     * @param fileName 서버에서 변환된 파일명
     * @param fileRealName 사용자가 실제로 입력한 파일명
     * @return 파일과 헤더, HttpStatus 정보를 포함한 ResponseEntity<Resource> 객체
     */
    public ResponseEntity<Resource> downloadFile(String fileName, String fileRealName) {

        // TODO: 2/25 path는 프로퍼티에서 관리(개발 환경에 따라 달라질 수 있기 때문에)


        String requestPath = basicPath + fileName;
        log.info("requestPath : " + requestPath);

        try {
            Path filePath = Paths.get(requestPath);
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(fileRealName, StandardCharsets.UTF_8)
                    .build());

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }
}
