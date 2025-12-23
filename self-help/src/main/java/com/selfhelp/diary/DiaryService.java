package com.selfhelp.diary;

import com.selfhelp.user.User;
import com.selfhelp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;


    // 📝 CREATE
    public Diary createDiary(Diary diary, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        diary.setAuthor(user);
        diary.setEntryDate(LocalDate.now());
        return diaryRepository.save(diary);
    }



    public Page<Diary> getMyDiaries(String email, int page, int size) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        return diaryRepository.findByAuthor(user, pageable);
    }




    // 🌍 PUBLIC FEED
    public Page<PublicDiaryDTO> getPublicDiaries(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("entryDate").descending()
        );

        return diaryRepository.findPublicDiaries(pageable)
                .map(diary -> new PublicDiaryDTO(
                        diary.getId(),
                        diary.getTitle(),
                        diary.getGoodThings(),
                        diary.getMood(),
                        diary.getAuthor().getName(),
                        diary.getEntryDate().toString()
                ));
    }



    // ✏️ UPDATE (AUTHOR ONLY)
    public Diary updateDiary(Long id, DiaryUpdateRequest request, String email) {

        Diary diary = diaryRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new RuntimeException("Diary not found ❌"));

        if (!diary.getAuthor().getEmail().equals(email)) {
            throw new RuntimeException("Not allowed ❌");
        }

        if (request.getTitle() != null) diary.setTitle(request.getTitle());
        if (request.getGoodThings() != null) diary.setGoodThings(request.getGoodThings());
        if (request.getBadThings() != null) diary.setBadThings(request.getBadThings());
        if (request.getMood() != null) diary.setMood(request.getMood());
        if (request.getVisibility() != null) diary.setVisibility(request.getVisibility());

        return diaryRepository.save(diary);
    }

    // 🗑️ DELETE (AUTHOR ONLY)
    public void deleteDiary(Long id, String email) {

        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diary not found ❌"));
                if (!diary.getAuthor().getEmail().equals(email)) {
            throw new RuntimeException("Not allowed ❌");
        }

        diaryRepository.delete(diary);

    }
}
