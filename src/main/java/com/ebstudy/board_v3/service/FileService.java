package com.ebstudy.board_v3.service;

import com.ebstudy.board_v3.dto.FileDTO;
import com.ebstudy.board_v3.repository.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FileService {

    private final BoardMapper boardMapper;

    // TODO: 확장자, 크기 컬럼 추가
    public List<FileDTO> getFileList(Long postId) {
        List<FileDTO> fileList = boardMapper.getFileList(postId);
        return fileList;
    }
}
