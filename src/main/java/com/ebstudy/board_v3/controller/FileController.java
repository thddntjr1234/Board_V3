package com.ebstudy.board_v3.controller;

import com.ebstudy.board_v3.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/boards/free")
public class FileController {

    private final FileService fileService;

    // TODO: {fileName}뒤에 파라미터를 붙이는 방식으로 fileRealName을 가져왔지만 더 좋은 방법이 있을 것 같다(uri가 너무 지저분함)
    /**
     * fileName 파라미터를 통해 경로를 노출하지 않고 파일을 다운로드
     * @param fileName 서버상에 저장된 파일명
     * @param fileRealName 사용자가 입력한 실제 파일명
     * @return 사용자가 입력한 실제 파일명의 파일을 담은 ResponseEntity<Resource> 객체
     */
    @GetMapping("/file/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, @RequestParam String fileRealName) {
        return fileService.downloadFile(fileName, fileRealName);
    }
}
