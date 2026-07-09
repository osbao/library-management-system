package com.example.springboot.pojo;

/**
 * 图书副本状态枚举
 */
public enum BookItemStatus {
    AVAILABLE("在架", "可被预约或借阅"),
    RESERVED("已预约", "已被用户线上预约，保留中"),
    BORROWED("已借出", "用户已取书，借阅中"),
    MAINTENANCE("维修中", "图书损坏，暂时不可借"),
    LOST("遗失", "图书丢失");

    private final String code;
    private final String description;

    BookItemStatus(String code, String description) {
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
    public static BookItemStatus fromCode(String code) {
        for (BookItemStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
