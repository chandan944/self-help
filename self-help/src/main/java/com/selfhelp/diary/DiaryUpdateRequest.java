package com.selfhelp.diary;

import lombok.Data;

@Data
public class DiaryUpdateRequest {
    private String title;
    private String goodThings;
    private String badThings;
    private Mood mood;
    private Visibility visibility;
}
