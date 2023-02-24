package com.ebstudy.board_v3.service;

import com.ebstudy.board_v3.dto.CategoryDTO;
import com.ebstudy.board_v3.dto.PostDTO;
import com.ebstudy.board_v3.repository.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final BoardMapper boardMapper;

    /**
     *
     * @param pageNumber
     * @return
     */
    public ModelAndView getPostList(Integer pageNumber) {

        if (pageNumber == null || pageNumber == 0) {
            pageNumber = 1;
        }

        int totalPostCount = boardMapper.getPostCount();

        HashMap<String, Integer> pagingValues = calPagingValues(totalPostCount, pageNumber);
        List<CategoryDTO> categoryList = boardMapper.getCategoryList();
        // 가져올 게시글의 개수를 지정, ex) 15개의 게시글이 있다면 1페이지는 10개, 2페이지는 5개를 가져오도록 함
        int maximumPostCount = (pageNumber - 1) * 10;
        List<PostDTO> postList = convertPostListData(boardMapper.getPostList(maximumPostCount));

        // 페이징에 필요한 변수와 카테고리 리스트, 총 게시글 개수와 페이징된 게시글 리스트를 ModelAndView 객체에 저장
        ModelAndView mv = new ModelAndView("list");

        mv.addObject("pagingValues", pagingValues);
        mv.addObject("categoryList", categoryList);
        mv.addObject("totalPostCount", totalPostCount);
        mv.addObject("postList", postList);

        return mv;
    }

    /**
     *
     * @param postId
     * @return 게시글 데이터와
     */
    public ModelAndView getPost(Long postId) {

        // TODO: 로그 기능 별도로 사용하기

        ModelAndView mv = new ModelAndView();

        // null, 0, ""은 list 페이지로 리다이렉트
        if ("0".equals(postId) || "".equals(postId) || postId == null) {
            mv.setViewName("redirect:/boards/free/list");
            log.info("파라미터 postId값이 유효하지 않아 /list 리다이렉트됨");
            return mv;
        }

        List<CategoryDTO> categoryList = boardMapper.getCategoryList();

        // 게시글 로드
        boardMapper.increaseHits(postId);
        PostDTO post = convertPostDateFormat(boardMapper.getPost(postId));
        mv.addObject("post", post);
        mv.addObject("categoryList", categoryList);
        mv.setViewName("view");

        return mv;
    }



    /**
     * 페이징에 필요한 변수값과 현재 페이지에 잘못된 값이 입력된 경우 이를 보정하는 메소드
     * @param totalPostCount 전체 게시글 개수
     * @param pageNumber 입력받은 요청 페이지 번호
     * @return 페이징에 필요한 시작페이지와 끝 페이지 값과 보정된 요청 페이지 값, 총 페이지 값(끝으로 이동 시 사용)
     */
    private HashMap<String, Integer> calPagingValues(int totalPostCount, Integer pageNumber) {
        HashMap<String, Integer> values = new HashMap<>();

        int totalPage = totalPostCount / 10;
        if (totalPostCount % 10 > 0) {
            totalPage++;
        }

        int currentPage = pageNumber;

        // 유효 페이지 이외의 값을 파라미터로 받게 되는 경우의 예외처리
        if (currentPage > totalPage || currentPage <= 0) {
            currentPage = 1;
        }

        int startPage = ((currentPage - 1) / 10) * 10 + 1;
        int endPage = startPage + 10 - 1;

        // 마지막 페이지에서 페이지 범위 조정
        if (endPage > totalPage) {
            endPage = totalPage;
        }

        values.put("startPage", startPage);
        values.put("endPage", endPage);
        values.put("currentPage", currentPage);
        values.put("totalPage", totalPage);

        return values;
    }

    /**
     * 날짜 변환과 수정일이 공란일 시 "-"을 추가하고 각 포스트마다 파일 보유 여부를 추가하는 메소드
     * @param postList 게시글 리스트
     * @return 수정된 파라미터값(게시글 리스트)을 반환
     */
    private List<PostDTO> convertPostListData(List<PostDTO> postList) {

        List<PostDTO> result = new LinkedList<>();

        for (PostDTO post : postList) {

            post = convertPostDateFormat(post);

            // 제목 길이 수정
            if (post.getTitle().length() > 80) {
                post.setTitle(post.getTitle().substring(0, 80) + "...");
            }

            // 줄 띄어쓰기 적용
            post.setContent(post.getContent().replaceAll("\r\n", "<br>"));

            // 현재 게시글의 파일 여부 확인시 fileFlag 설정
            // checkFileExistence 메소드는 file쪽 메소드인데 file에 놓지 않고 post에 놓아도 될까?(이 메소드를 이용해서 Post 데이터의 필드에 관여하는 기능이기 때문에 post에 놓았음)
            long postId = post.getPostId();
            if (boardMapper.checkFileExistence(postId)) {
                post.setFileFlag(true);
            }

            result.add(post);
        }
        return result;
    }

    private PostDTO convertPostDateFormat(PostDTO post) {

        String createdDate = post.getCreatedDate();
        // 날짜 포맷 변경
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(createdDate, format);

        post.setCreatedDate(ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        if (post.getModifiedDate() == null) {
            post.setModifiedDate("-");
        } else {
            String modifiedDate = post.getModifiedDate();
            ldt = LocalDateTime.parse(modifiedDate, format);
            post.setModifiedDate(ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }

        return post;
    }
}
