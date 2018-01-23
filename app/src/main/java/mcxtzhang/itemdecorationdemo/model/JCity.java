package mcxtzhang.itemdecorationdemo.model;

/**
 * Created With Android Studio
 * Email: sielee@163.com
 * Author: Lee Sie
 * CopyRight: CL
 * <p>
 * Description: TODO
 * </p>
 */

public class JCity {
    /**
     * name : 北京
     * name_en : beijing
     */

    private String name;
    private String name_en;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    @Override
    public String toString() {
        return "JCity{" +
                "name='" + name + '\'' +
                ", name_en='" + name_en + '\'' +
                '}';
    }
}
