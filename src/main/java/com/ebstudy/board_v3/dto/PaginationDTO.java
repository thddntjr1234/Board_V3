package com.ebstudy.board_v3.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString @Builder
public class PaginationDTO {
    int totalPostCount;
    int startPage;
    int endPage;
    Integer currentPage;
    int totalPage;
    int startPostNumber;
}
