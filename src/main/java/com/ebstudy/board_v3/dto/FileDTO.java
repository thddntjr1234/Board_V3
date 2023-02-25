package com.ebstudy.board_v3.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class FileDTO {
    private Long postId;
    private Long fileId;
    private String fileName;
    private String fileRealName;
    private String extension;
    private Long size;
}
