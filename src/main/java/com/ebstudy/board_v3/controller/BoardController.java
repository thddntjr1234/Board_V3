package com.ebstudy.board_v3.controller;

import com.ebstudy.board_v3.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/boards/free")
public class BoardController {

    private final PostService postService;

    @GetMapping(value = {"/list/{pageNumber}", "/list"})
    public ModelAndView loadPostList(@PathVariable(required = false) Integer pageNumber) {
        return postService.loadPostList(pageNumber);
    }
}
