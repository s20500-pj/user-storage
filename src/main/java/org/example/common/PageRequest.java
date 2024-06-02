package org.example.common;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequest {
    private int pageNumber = 1;
    private int pageSize = 10;
    private String sortBy;
    private boolean asc = true;
    private Map<String, String> searchParams = new HashMap<>();
}
