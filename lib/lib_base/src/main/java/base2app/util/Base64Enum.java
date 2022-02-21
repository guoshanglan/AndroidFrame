package base2app.util;

/**
 * base64 类型
 * @author wangjiafang
 * @date 2019/8/6
 */
public enum Base64Enum {

    /**
     * mp4
     */
    MP4("data:video/mp4;base64,","mp4"),

    /**
     * avi
     */
    AVI("data:video/avi;base64,","avi"),

    /**
     * flv
     */
    FLV("data:video/flv;base64,","flv"),

    /**
     * png
     */
    PNG("data:image/png;base64,","png"),

    /**
     * jpeg
     */
    JPEG("data:image/jpeg;base64,","jpeg"),

    /**
     * jpg
     */
    JPG("data:image/jpg;base64,","jpg");



    private String code;
    private String desc;

    public static Base64Enum getByCode(String code) {

        for (Base64Enum ts : values()) {
            if (ts.getCode().equalsIgnoreCase(code)) {
                return ts;
            }
        }
        throw new RuntimeException("没有找到对应的枚举");
    }


    Base64Enum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}