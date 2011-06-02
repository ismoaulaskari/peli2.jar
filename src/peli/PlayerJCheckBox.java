package peli;
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PlayerJCheckBox.java
import javax.swing.JCheckBox;

/**
 * v1.1 tried to add equals to ban duplicate entries
 * @author aulaskar
 *
 */
public class PlayerJCheckBox extends JCheckBox {

    private int rank;

    PlayerJCheckBox(int rnk, String text) {
        super(text);
        rank = 0;
        rank = rnk;
    }

    PlayerJCheckBox(int rnk, String text, boolean flag) {
        super(text, flag);
        rank = 0;
        rank = rnk;
    }

    public int getRank() {
        return rank;
    }

    public boolean lt(PlayerJCheckBox playerjcheckbox) {
        return rank < playerjcheckbox.getRank();
    }

    public boolean gt(PlayerJCheckBox playerjcheckbox) {
        return rank > playerjcheckbox.getRank();
    }

    public boolean eq(PlayerJCheckBox playerjcheckbox) {
        return rank == playerjcheckbox.getRank();
    }

    //added to get working Treeset.contains() for duplicate players 
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlayerJCheckBox)) {
            return false;
        }
        PlayerJCheckBox other = (PlayerJCheckBox) o;
        if (this.getText().equals(other.getText())) {
            return true;
        }
        return false;
    }
}
