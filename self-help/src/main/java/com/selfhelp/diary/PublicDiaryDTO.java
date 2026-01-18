package com.selfhelp.diary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicDiaryDTO {
    private Long id;
    private String title;
    private String goodThings;
    private String badThings;
    private Mood mood;
    private String authorName;
    private String entryDate;
    private Long commentCount; // Add this field

    // Keep existing constructor and add new one
    public PublicDiaryDTO(Long id, String title, String goodThings, String badThings,
                          Mood mood, String authorName, String entryDate) {
        this.id = id;
        this.title = title;
        this.goodThings = goodThings;
        this.badThings = badThings;
        this.mood = mood;
        this.authorName = authorName;
        this.entryDate = entryDate;
        this.commentCount = 0L;
    }
}