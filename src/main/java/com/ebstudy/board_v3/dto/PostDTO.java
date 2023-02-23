package com.ebstudy.board_v3.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
public class PostDTO {

    // 논리 필드가 추가되게 된다면 그 부분에 대한 설명만 충분히 적으면 된다.
    private Long postId;
    private Long hits;
    private Long categoryId;
    private String createdDate;
    private String modifiedDate;
    private String title;
    private String content;
    private String author;
    private String category;
    private String passwd;
    private String secondPasswd;
    private boolean fileFlag = false;

}
