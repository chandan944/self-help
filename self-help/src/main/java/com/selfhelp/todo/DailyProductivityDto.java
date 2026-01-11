package com.selfhelp.todo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyProductivityDto {
    private LocalDate date;
    private Long completed;
    private Long created;
}
