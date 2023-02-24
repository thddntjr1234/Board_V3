package com.ebstudy.board_v3.controller;

import com.ebstudy.board_v3.service.CommentService;
import com.ebstudy.board_v3.service.FileService;
import com.ebstudy.board_v3.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/boards/free")
public class BoardController {

    private final PostService postService;
    private final FileService fileService;
    private final CommentService commentService;

    @GetMapping(value = {"/list/{pageNumber}", "/list"})
    public ModelAndView getPostList(@PathVariable(required = false) Integer pageNumber) {

        return postService.getPostList(pageNumber);
    }

    @GetMapping(value = {"/view/{postId}" , "/view"})
    public ModelAndView getPost(@PathVariable(required = false) Long postId) {

        ModelAndView mv = postService.getPost(postId);
        if (!mv.getViewName().startsWith("redirect")) {
            mv.addObject("fileList", fileService.getFileList(postId));
            mv.addObject("commentList", commentService.getCommentList(postId));
            log.info("getPost 정상 수행에 따른 파일 리스트 및 댓글 리스트 로딩 완료");
        }
        return mv;
    }
}
