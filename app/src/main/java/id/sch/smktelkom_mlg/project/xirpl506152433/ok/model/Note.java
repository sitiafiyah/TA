package id.sch.smktelkom_mlg.project.xirpl506152433.ok.model;

import java.io.Serializable;

/**
 * Created by Siti Afiyah on 10/29/2016.
 */

public class Note implements Serializable {

    public String tanggal;
    public String category;
    public String diary;
    public String quotes;
    public String foto;

    public Note(String tanggal, String category, String diary, String quotes, String foto) {
        this.tanggal = tanggal;
        this.category = category;
        this.diary = diary;
        this.quotes = quotes;
        this.foto = foto;
    }

}
