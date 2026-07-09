package com.example.springboot.pojo;

/**
 * 借阅状态枚举
 */
public enum BorrowStatus {
    WAITING_PICKUP("待取书", "用户已预约，等待取书"),
    BORROWED("已借出", "用户已取书，借阅中"),
    RETURNED("已归还", "正常归还"),
    OVERDUE("已逾期", "超过应还日期未还"),
    CANCELLED("已取消", "预约被取消");

    private final String code;
    private final String description;

    BorrowStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     */
    public static BorrowStatus fromCode(String code) {
        for (BorrowStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
