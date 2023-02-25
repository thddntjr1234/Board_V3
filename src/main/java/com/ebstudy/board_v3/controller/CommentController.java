package com.ebstudy.board_v3.controller;

import com.ebstudy.board_v3.dto.CommentDTO;
import com.ebstudy.board_v3.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/boards/free")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    public ResponseEntity saveComment(@ModelAttribute CommentDTO comment) {
        log.info("commnet : " + comment);
        try {
            commentService.saveComment(comment);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
