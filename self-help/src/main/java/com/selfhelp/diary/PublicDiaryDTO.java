package com.selfhelp.diary;

public record PublicDiaryDTO(
        Long id,
        String title,
        String goodThings,
        Mood mood,
        String authorName,
        String entryDate
) {}
