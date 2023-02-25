package com.ebstudy.board_v3.util;

import com.ebstudy.board_v3.dto.PostDTO;
import com.ebstudy.board_v3.repository.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostServiceUtil {
    private final BoardMapper boardMapper;

    /**
     * 페이징에 필요한 변수값과 현재 페이지에 잘못된 값이 입력된 경우 이를 보정하는 메소드
     *
     * @param totalPostCount 전체 게시글 개수
     * @param pageNumber     입력받은 요청 페이지 번호
     * @return 페이징에 필요한 시작페이지와 끝 페이지 값과 보정된 요청 페이지 값, 총 페이지 값(끝으로 이동 시 사용), DB LIMIT 시작값
     */
    public HashMap<String, Integer> calPagingValues(int totalPostCount, Integer pageNumber) {
        // TODO: 2/25 검색조건 추가시 pagination 로직 안에 검색조건 포함해서 넣기
        HashMap<String, Integer> values = new HashMap<>();

        int totalPage = totalPostCount / 10;
        if (totalPostCount % 10 > 0) {
            totalPage++;
        }

        Integer currentPage = pageNumber;
        // 유효 페이지 이외의 값을 파라미터로 받게 되는 경우의 예외처리
        if (currentPage == null || currentPage > totalPage || currentPage <= 0) {
            currentPage = 1;
        }

        int startPage = ((currentPage - 1) / 10) * 10 + 1;
        int endPage = startPage + 10 - 1;
        // DB LIMIT 시작값(+1번째부터 10개 추출)
        int startPostNumber = (currentPage - 1) * 10;

        // 마지막 페이지에서 10개 이하로 페이징이 되는 경우 존재하는 만큼만 표시하기 위해 endPage 값 조정
        if (endPage > totalPage) {
            endPage = totalPage;
        }

        values.put("startPage", startPage);
        values.put("endPage", endPage);
        values.put("currentPage", currentPage);
        values.put("totalPage", totalPage);
        values.put("startPostNumber", startPostNumber);

        return values;
    }

    /**
     * getPostList()시 날짜 변환과 수정일이 공란일 시 "-"을 추가하고 각 포스트마다 파일 보유 여부를 추가하는 메소드
     *
     * @param postList 게시글 리스트
     * @return 수정된 파라미터값(게시글 리스트)을 반환
     */
    public List<PostDTO> convertPostListData(List<PostDTO> postList) {

        List<PostDTO> result = new LinkedList<PostDTO>();

        for (PostDTO post : postList) {

            post = convertPostDataFormat(post);

            // 제목 길이 수정
            if (post.getTitle().length() > 80) {
                //TODO: 2/25. byte단위로 잘라야 할 때 post.getTitle().getBytes()로 하면 한글은 잘려버릴 텐데 어떻게 해야 할까?
                post.setTitle(post.getTitle().substring(0, 80) + "...");
            }

            // 현재 게시글의 파일 여부 확인시 fileFlag 설정
            long postId = post.getPostId();
            // TODO: 파라미터로 interface(ex: externalLogic) 넣거나, 리스트가 아닌 단건을 foreach로 처리하면서 그 사이에 checkFileExistence를 넣어주는 방식으로 리팩토링
            if (boardMapper.checkFileExistence(postId)) {
                post.setFileFlag(true);
            }

            result.add(post);
        }
        return result;
    }

    /**
     * getPost()시 필요한 DB에서 가져온 게시글 데이터를 요구 형식에 맞게 변환하는 메소드
     *
     * @param post 변환할 게시글
     * @return 변환된 게시글
     */
    public PostDTO convertPostDataFormat(PostDTO post) {

        // 날짜 부분 메소
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

        // 줄 띄어쓰기 적용
        post.setContent(post.getContent().replaceAll("\r\n", "<br>"));

        return post;
    }

    /**
     * 서버 사이드 유효성 검사
     *
     * @param post 유효성 검사를 수행할 게시글
     * @return 검사 결과(true, false)
     */
    public boolean checkValidation(PostDTO post) {

        if (post.getCategoryId() == null) {
            return false;
        }

        int authorLength = post.getAuthor().length();
//        authorLength += 100; // error유도
        if (authorLength < 3 || authorLength >= 5) {
            return false;
        }

        if (!post.getPasswd().matches("^.*(?=^.{4,15}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$")) {
            return false;
        } else if (!post.getPasswd().equals(post.getSecondPasswd())) {
            return false;
        }

        int titleLength = post.getTitle().length();
        if (titleLength < 4 || titleLength >= 100) {
            return false;
        }

        int contentLength = post.getContent().length();
        if (contentLength < 4 || contentLength >= 2000) {
            return false;
        }

        return true;
    }


    // TODO: 스프링부트에서 보편적으로 사용하는 암호화 방식으로 리팩토링
    /**
     * jsp/servlet 게시판에서 사용하던 단방향 암호화 메소드
     * @param pw 암호화할 패스워드
     * @return 암호화된 패스워드
     */
    public String getSHA512(String pw) {
        String encryptPw = null; //암호화된 비밀번호를 저장할 변수

        //1) 해시 함수를 수행할 객체를 참조할 변수 선언
        MessageDigest md = null;

        try {
            // 2) SHA-512 방식의 해시 함수를 수행할 수 있는 객체를 얻어옴
            md = MessageDigest.getInstance("SHA-512");

            // 3) md를 이용해 암호화를 진행할 수 있도록 pw를 byte[] 배열 형태로 변환
            byte[] bytes = pw.getBytes(Charset.forName("UTF-8"));
            //UTF-8 문자열인거 감안하고 변환해라라는 의미

            // 4) 암호화  -> 암호화 결과가 md 내부에 저장함.
            md.update(bytes);

            // 5) 암호화된 비밀번호를 encryptPw에 대입
            //digest가 보관하고 있다
            //byte[]배열을 String으로 변환할 필요가 있다.
            encryptPw = Base64.getEncoder().encodeToString(md.digest());
            //Base64 : byte 코드를 문자열로 변환하는 객체
            //base64 에서 변환할수 있게 해주는 객체를 가져와 변환한다

        } catch (NoSuchAlgorithmException e) {
            //SHA-512 해시 함수가 존재하지 않을 때 예외 발생
            e.printStackTrace();
        }
        return encryptPw;
    }
}