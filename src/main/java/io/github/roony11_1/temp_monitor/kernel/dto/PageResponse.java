package io.github.roony11_1.temp_monitor.kernel.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record PageResponse<T>(
    List<T> content,
    int page,
    int pageSize,
    long total,
    int totalPages) 
{
    public static <T> PageResponse<T> from(Page<T> page) 
    {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber() + 1,
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages());
    }
}
