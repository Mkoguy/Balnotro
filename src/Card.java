import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;
    String r;
    String s;
    int val;

    public Card(String r, String s, int val) {
        this.r = r;
        this.s = s;
        this.val = val;
    }
}
