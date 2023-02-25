package com.ebstudy.board_v3.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CommentDTO {
    private Long postId;
    private String createdDate;
    private String comment;
}
