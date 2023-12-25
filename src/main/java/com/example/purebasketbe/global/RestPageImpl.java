package com.example.purebasketbe.global;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
public class RestPageImpl<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    private RestPageImpl(@JsonProperty("content") List<T> content,
                         @JsonProperty("number") int number,
                         @JsonProperty("size") int size,
                         @JsonProperty("totalElements") Long totalElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }

    private RestPageImpl(Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    public static<T> RestPageImpl<T> from(Page<T> page) {
        return new RestPageImpl<T>(page);
    }
}