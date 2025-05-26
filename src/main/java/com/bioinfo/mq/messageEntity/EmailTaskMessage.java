package com.bioinfo.mq.messageEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 刘家雯
 * @Date 2025/5/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailTaskMessage {
    private Long userId;
    private String resultPath;
}

