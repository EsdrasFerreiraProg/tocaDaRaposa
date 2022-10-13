package tocadaraposa.domain;

@SuppressWarnings("unused")
public enum PerfilTipo {
    ADMIN(1, "ADMIN");

    private final long cod;
    private final String desc;

    PerfilTipo(long cod, String desc){
        this.cod = cod;
        this.desc = desc;
    }

    public Perfil buildPerfil(){
        return new Perfil(cod, desc);
    }

    public long getCod(){
        return cod;
    }

    public String getDesc(){
        return desc;
    }

}
