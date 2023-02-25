package com.ebstudy.board_v3.service;

import com.ebstudy.board_v3.dto.CommentDTO;
import com.ebstudy.board_v3.repository.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final BoardMapper boardMapper;

    /**
     * 게시글 id에 해당하는 댓글 리스트를 가져오는 메소드
     * @param postId 가져올 댓글의 게시글 id
     * @return 댓글 리스트
     */
    public List<CommentDTO> getCommentList(Long postId) {
        List<CommentDTO> commentList = convertCommentListData(boardMapper.getCommentList(postId));
        return commentList;
    }

    /**
     * 게시글 리스트의 생성시간 값을 요구 포맷에 맞게 변환하는 메소드
     * @param commentDTOList 댓글 리스트
     * @return 변환된 댓글 리스트
     */
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

    //TODO: NullPointerException이 일어날 메소드가 아닌데 발생, intellij에서 자동으로 @NotNull 어노테이션을 추가해서 발생한 문제
    // 해결 --> 생성자 주입을 쓰는데 private final 접근제어자를 안 붙여서 초기화가 안 되었기 때문에 null 오류가 생겼던 것
    /**
     * 댓글 저장 메소드
     * @param comment 저장할 댓글
     */
    public void saveComment( CommentDTO comment) {
        comment.setCreatedDate(String.valueOf(LocalDateTime.now()));
        boardMapper.saveComment(comment);
    }
}
