package com.ebstudy.board_v3.service;

import com.ebstudy.board_v3.dto.CommentDTO;
import com.ebstudy.board_v3.repository.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final BoardMapper boardMapper;

    public List<CommentDTO> getCommentList(Long postId) {
        List<CommentDTO> commentList = convertCommentListData(boardMapper.getCommentList(postId));
        return commentList;
    }

    private List<CommentDTO> convertCommentListData(List<CommentDTO> commentDTOList) {

        List<CommentDTO> result = new LinkedList<>();

        for (CommentDTO comment : commentDTOList) {

            String createdDate = comment.getCreatedDate();

            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime ldt = LocalDateTime.parse(createdDate, format);

            comment.setCreatedDate(ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            result.add(comment);
        }

        return result;
    }
}
