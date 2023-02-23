package com.ebstudy.board_v3.service;

import com.ebstudy.board_v3.dto.CategoryDTO;
import com.ebstudy.board_v3.dto.PostDTO;
import com.ebstudy.board_v3.repository.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final BoardMapper boardMapper;

    /**
     *
     * @param pageNumber
     * @return
     */
    public ModelAndView loadPostList(Integer pageNumber) {

        if (pageNumber == null || pageNumber == 0) {
            pageNumber = 1;
        }

        int totalPostCount = boardMapper.getPostCount();

        HashMap<String, Integer> pagingValues = calPagingValues(totalPostCount, pageNumber);
        List<CategoryDTO> categoryList = boardMapper.getCategoryList();
        // 가져올 게시글의 개수를 지정, ex) 15개의 게시글이 있다면 1페이지는 10개, 2페이지는 5개를 가져오도록 함
        int maximumPostCount = (pageNumber - 1) * 10;
        List<PostDTO> postList = transDataFormat(boardMapper.getPostList(maximumPostCount));

        // TODO: fileFlagList를 사용하면 타임리프에서 이를 체크해야 하기 때문에 서비스 계층에서 이를 dto의 fileFlag 필드에 넣기
        // 현재 게시글 리스트별로 파일 소유 여부를 확인
//        FileDAO fileDAO = FileDAO.getInstance();
//        HashMap<Long, Boolean> postFileFlagList = new HashMap<>();
//
//        for (PostDTO post : postList) {
//            long postId = post.getPostId();
//
//            if (fileDAO.checkFileExistence(postId)) {
//                postFileFlagList.put(postId, true);
//                continue;
//            }
//            postFileFlagList.put(postId, false);
//        }

        // 페이징에 필요한 변수와 카테고리 리스트, 총 게시글 개수와 페이징된 게시글 리스트를 ModelAndView 객체에 저장
        ModelAndView mv = new ModelAndView("list");

        mv.addObject("pagingValues", pagingValues);
        mv.addObject("categoryList", categoryList);
        mv.addObject("totalPostCount", totalPostCount);
        mv.addObject("postList", postList);

        System.out.println(mv);
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
     * 뷰 로직에 있던 날짜 변환 및 수정일 확인 로직을 서비스로 이동
     * 날짜 변환과 수정일이 공란일 시 "-"을 추가하는 메소드
     * @param postList 게시글 리스트
     * @return 수정된 파라미터값(게시글 리스트)을 반환
     */
    private List<PostDTO> transDataFormat(List<PostDTO> postList) {

        List<PostDTO> result = new LinkedList<>();

        for (PostDTO post : postList) {

            String createdDate = post.getCreatedDate();
            // String -> LocalDateTime
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime ldt = LocalDateTime.parse(createdDate, format);
            // LocalDateTime을 지정된 포맷으로 변환
            post.setCreatedDate(ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            // modifiedDate이 null이면 -값을
            if (post.getModifiedDate() == null) {
                post.setModifiedDate("-");
            } else {
                String modifiedDate = post.getModifiedDate();
                ldt = LocalDateTime.parse(modifiedDate, format);
                post.setModifiedDate(ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }

            // 제목 길이 수정
            if (post.getTitle().length() > 80) {
                post.setTitle(post.getTitle().substring(0, 80) + "...");
            }

            result.add(post);
        }
        return result;
    }


}
