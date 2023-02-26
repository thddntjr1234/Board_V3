package com.ebstudy.board_v3.service;

import com.ebstudy.board_v3.dto.CategoryDTO;
import com.ebstudy.board_v3.dto.PaginationDTO;
import com.ebstudy.board_v3.dto.PostDTO;
import com.ebstudy.board_v3.repository.BoardMapper;
import com.ebstudy.board_v3.util.PostServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final BoardMapper boardMapper;
    private final PostServiceUtil postServiceUtil;

    /**
     * 요구 데이터 포맷에 맞게 변환된 게시글 리스트를 가져오는 메소드
     * @param startPostNumber 예외처리 없이 입력된 페이지 값
     * @return viewName과 데이터를 포함안 ModelAndView 객체
     */
    public List<PostDTO> getPostList(int startPostNumber) {

        List<PostDTO> postList = new LinkedList<>();
        List<PostDTO> sourcePostList = boardMapper.getPostList(startPostNumber);

        for (PostDTO post : sourcePostList) {
            if (boardMapper.checkFileExistence(post.getPostId())) {
                post.setFileFlag(true);
            }
            PostDTO convertedPost = postServiceUtil.convertToListFormat(post);
            postList.add(convertedPost);
        }

        return postList;
    }

    /**
     * Pagination에 사용되는 변수들을 반환하는 메소드
     * @param pageNumber uri로 입력된 요청 페이지 값
     * @return 페이징 값 및 SELECT LIMIT
     */
    public PaginationDTO getPaginationValues(Integer pageNumber) {

        int totalPostCount = boardMapper.getPostCount();
        return postServiceUtil.calPagingValues(totalPostCount, pageNumber);
    }

    /**
     * 게시글 불러오기 메소드
     * postId 파라미터에 대한 유효성 검증과 게시글 로딩을 수행
     * @param postId 게시글 id
     * @return 게시글 데이터와 viewName을 가진 ModelAndView 객체 반환
     */
    public PostDTO getPost(Long postId) {
        return postServiceUtil.convertPostDataFormat(boardMapper.getPost(postId));
    }

    /**
     * 카테고리 리스트를 가져오는 메소드
     * @return 카테고리 리스트
     */
    public List<CategoryDTO> getCategoryList() {
        return boardMapper.getCategoryList();
    }

    /**
     * 비밀번호 암호화 및 생성시간을 추가해서 게시글을 저장하는 메소드
     * @param post 저장할 게시글
     */
    public void savePost(PostDTO post) {

        if (!postServiceUtil.checkValidation(post)) {
            throw new RuntimeException();
        }

        // TODO: 2/25. 단방향 암호화만 하고 복호화를 못하게 하는 이유?
        post.setPasswd(postServiceUtil.getSHA512(post.getPasswd()));
        post.setCreatedDate(String.valueOf(LocalDateTime.now()));

        boardMapper.savePost(post);
    }

    public void increaseHits(Long postId) {
        boardMapper.increaseHits(postId);
    }
}
