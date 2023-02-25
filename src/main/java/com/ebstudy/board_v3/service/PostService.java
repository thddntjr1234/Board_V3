package com.ebstudy.board_v3.service;

import com.ebstudy.board_v3.dto.CategoryDTO;
import com.ebstudy.board_v3.dto.PostDTO;
import com.ebstudy.board_v3.repository.BoardMapper;
import com.ebstudy.board_v3.util.PostServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final BoardMapper boardMapper;
    private final PostServiceUtil postServiceUtil;

    /**
     * 요구 데이터 포맷에 맞게 변환된 게시글 리스트를 가져오는 메소드
     * @param pageNumber 예외처리 없이 입력된 페이지 값
     * @return viewName과 데이터를 포함안 ModelAndView 객체
     */
    public ModelAndView getPostList(Integer pageNumber) {

        int totalPostCount = boardMapper.getPostCount();

        // TODO: 2/25 Map으로 리턴하는 게 아니라 PagenationDTO와 같이 객체 형식으로 해줘야 안에 데이터가 무슨 값이 들었는지 유추할 수 있다
        HashMap<String, Integer> pagingValues = postServiceUtil.calPagingValues(totalPostCount, pageNumber);
        List<CategoryDTO> categoryList = boardMapper.getCategoryList();

        // 가져올 게시글의 개수를 지정, ex) 15개의 게시글이 있다면 1페이지는 10개, 2페이지는 5개를 가져오도록 함
        int startPostNumber = pagingValues.get("startPostNumber");
        //List<PostDTO> postList = postServiceUtil.convertPostListData(boardMapper.getPostList(startPostNumber));
        // TODO: postList를 단건으로 convert하도록 메소드를 수정
        List<PostDTO> postList = postServiceUtil.convertPostListData(boardMapper.getPostList(startPostNumber));
        List<PostDTO> sourcePostList = boardMapper.getPostList(startPostNumber);
//        sourcePostList.forEach((post)=>{
//                postServiceUtil.convertPostListData(post);
//        });

        // 페이징에 필요한 변수와 카테고리 리스트, 총 게시글 개수와 페이징된 게시글 리스트를 ModelAndView 객체에 저장
        ModelAndView mv = new ModelAndView("list");

        mv.addObject("pagingValues", pagingValues);
        mv.addObject("categoryList", categoryList);
        mv.addObject("totalPostCount", totalPostCount);
        mv.addObject("postList", postList);

        return mv;
    }

    /**
     * 게시글 불러오기 메소드
     * postId 파라미터에 대한 유효성 검증과 게시글 로딩을 수행
     * @param postId 게시글 id
     * @return 게시글 데이터와 viewName을 가진 ModelAndView 객체 반환
     */
    public PostDTO getPost(Long postId) {
        // 게시글 로드
        return postServiceUtil.convertPostDataFormat(boardMapper.getPost(postId));
    }

    /**
     * /write get 요청을 처리하는 메소드
     * @return 게시글 작성 viewName과 카테고리 리스트
     */
    public ModelAndView getWriteForm() {

        ModelAndView mv = new ModelAndView("write-form");
        mv.addObject("categoryList", boardMapper.getCategoryList());

        return mv;
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
}
