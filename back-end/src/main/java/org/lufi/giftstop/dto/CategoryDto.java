package org.lufi.giftstop.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private String slug;
    private String metaTitle;
    private String metaDescription;
    private String imageUrl;
    private Long parentId;
    private List<CategoryDto> children;
}
