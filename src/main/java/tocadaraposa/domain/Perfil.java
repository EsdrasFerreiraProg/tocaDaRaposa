package tocadaraposa.domain;

import javax.persistence.*;

@Entity
@Table(name = "profile")
public class Perfil extends AbstractEntity{

    @Column(name = "desc", nullable = false, unique = true)
    private String desc;

    public Perfil() {
        super();
    }

    public Perfil(Long id) {
        super.setId(id);
    }

    public Perfil(Long id, String desc) {
        super.setId(id);
        setDesc(desc);
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
