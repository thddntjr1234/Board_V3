package com.ebstudy.board_v3.controller;

import com.ebstudy.board_v3.service.CommentService;
import com.ebstudy.board_v3.service.FileService;
import com.ebstudy.board_v3.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
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
        System.out.println("getpost");
        mv.addObject("fileList", fileService.getFileList(postId));
        System.out.println("getfilelist");
        mv.addObject("commentList", commentService.getCommentList(postId));
        System.out.println("getcommentlist");

        return mv;
    }
}
