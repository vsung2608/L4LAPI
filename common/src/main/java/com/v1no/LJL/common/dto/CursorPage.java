package com.v1no.LJL.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CursorPage<T>(
        List<T> content,
        String nextCursor,
        boolean hasNext,
        int size
) {
    public static <T> CursorPage<T> of(List<T> content, String nextCursor, int size) {
        boolean hasNext = nextCursor != null;
        return new CursorPage<>(content, nextCursor, hasNext, size);
    }
}
